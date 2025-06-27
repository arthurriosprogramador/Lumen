package com.arthurriosribeiro.lumen.screens.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.model.AccountConfiguration
import com.arthurriosribeiro.lumen.model.Currencies
import com.arthurriosribeiro.lumen.model.Languages
import com.arthurriosribeiro.lumen.model.RequestState
import com.arthurriosribeiro.lumen.model.UserTransaction
import com.arthurriosribeiro.lumen.repository.LumenRepository
import com.arthurriosribeiro.lumen.utils.FirestoreCollectionUtils
import com.arthurriosribeiro.lumen.utils.convertFirestoreDocumentToUserTransactionList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val lumenRepository: LumenRepository,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) :
    ViewModel() {

    private val _accountConfig: MutableState<AccountConfiguration?> = mutableStateOf(null)
    val accountConfig: State<AccountConfiguration?> = _accountConfig

    private val _addTransactionState: MutableStateFlow<RequestState<Unit>?> = MutableStateFlow(null)
    val addTransactionState: StateFlow<RequestState<Unit>?> = _addTransactionState

    private val _deleteTransactionState: MutableStateFlow<RequestState<Unit>?> = MutableStateFlow(null)
    val deleteTransaction: StateFlow<RequestState<Unit>?> = _deleteTransactionState

    private val _transactions: MutableStateFlow<RequestState<List<UserTransaction>>?> = MutableStateFlow(null)
    val transactions: StateFlow<RequestState<List<UserTransaction>>?> = _transactions

    suspend fun getAccountConfig() : AccountConfiguration? {
            val config = runCatching {
                lumenRepository.selectAccountConfiguration()
            }.getOrNull()

        _accountConfig.value = config

        return config
    }

    fun createAccountConfig(
        name: String? = null,
        selectedLanguage: Languages,
        selectedCurrency: Currencies
    ) {
        val config = AccountConfiguration(
            id = 0,
            name = name,
            selectedLanguage = selectedLanguage.name,
            selectedCurrency = selectedCurrency.name
        )
        viewModelScope.launch {
            runCatching {
                lumenRepository.insertAccountConfiguration(
                    config
                )
            }.onSuccess {
                _accountConfig.value = config
            }.onFailure {
                _accountConfig.value = null
            }
        }
    }

    fun updateUserImage(userImagePath: String, id: Int) {
        viewModelScope.launch {
            runCatching {
                lumenRepository.updateUserImage(userImagePath, id)
            }.onSuccess {
                getAccountConfig()
            }
        }
    }

    fun updateUserName(name: String, id:Int) {
        viewModelScope.launch {
            runCatching {
                lumenRepository.updateUserName(name, id)
                updateUserInformationOnFireStore(
                    fieldToUpdate = FirestoreCollectionUtils.USER_NAME,
                    fieldValue = name
                )
            }.onSuccess {
                getAccountConfig()
            }
        }
    }

    fun updateUserCurrency(currency: String, id:Int) {
        viewModelScope.launch {
            runCatching {
                lumenRepository.updateUserCurrency(currency, id)
                updateUserInformationOnFireStore(
                    fieldToUpdate = FirestoreCollectionUtils.USER_SELECTED_CURRENCY,
                    fieldValue = currency
                )
            }.onSuccess {
                getAccountConfig()
            }
        }
    }

    fun updateUserLanguage(language: String, id:Int) {
        viewModelScope.launch {
            runCatching {
                lumenRepository.updateUserLanguage(language, id)
                updateUserInformationOnFireStore(
                    fieldToUpdate = FirestoreCollectionUtils.USER_SELECTED_LANGUAGE,
                    fieldValue = language
                )
            }.onSuccess {
                getAccountConfig()
            }
        }
    }

    private fun updateUserInformationOnFireStore(fieldToUpdate: String, fieldValue: String) {
        firestore.collection(FirestoreCollectionUtils.USERS_COLLECTION)
            .whereEqualTo(FirestoreCollectionUtils.USER_EMAIL, firebaseAuth.currentUser?.email)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val document = snapshot.documents.first()

                    document.reference.update(fieldToUpdate, fieldValue)
                }
            }
    }

    fun addTransactionOnFirestore(transaction: UserTransaction, context: Context) {
        _addTransactionState.value = RequestState.Loading
        firestore.collection(FirestoreCollectionUtils.TRANSACTIONS_COLLECTION)
            .add(
                mapOf(
                    FirestoreCollectionUtils.USER_ID to firebaseAuth.uid,
                    FirestoreCollectionUtils.TRANSACTIONS_UNIQUE_ID to transaction.uniqueId,
                    FirestoreCollectionUtils.TRANSACTION_TITLE to transaction.title,
                    FirestoreCollectionUtils.TRANSACTION_DESCRIPTION to transaction.description,
                    FirestoreCollectionUtils.TRANSACTION_VALUE to transaction.value,
                    FirestoreCollectionUtils.TRANSACTION_TIMESTAMP to transaction.timestamp,
                    FirestoreCollectionUtils.TRANSACTION_TYPE to transaction.type,
                    FirestoreCollectionUtils.TRANSACTION_CATEGORY_NAME to transaction.categoryName
                )
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _addTransactionState.value = RequestState.Success(Unit)
                    addTransactionToSql(transaction, context)
                } else {
                    val exceptionMessage = task.exception?.message
                    _addTransactionState.value = handleExceptionMessage(exceptionMessage, context)
                }
            }
    }

    fun deleteTransactionFromFirestore(uniqueId: String, context: Context) {
        _deleteTransactionState.value = RequestState.Loading
        firestore.collection(FirestoreCollectionUtils.TRANSACTIONS_COLLECTION)
            .whereEqualTo(FirestoreCollectionUtils.TRANSACTIONS_UNIQUE_ID, uniqueId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.documents.firstOrNull()?.reference?.delete()?.addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            _deleteTransactionState.value = RequestState.Success(Unit)
                        } else {
                            val exceptionMessage = deleteTask.exception?.message
                            _deleteTransactionState.value = handleExceptionMessage(exceptionMessage, context)
                        }
                    }
                } else {
                    val exceptionMessage = task.exception?.message
                    _deleteTransactionState.value = handleExceptionMessage(exceptionMessage, context)
                }
            }
    }

    fun addTransactionToSql(transaction: UserTransaction, context: Context) {
        _addTransactionState.value = RequestState.Loading
        viewModelScope.launch {
            try {
                lumenRepository.insertTransaction(transaction)
                _addTransactionState.value = RequestState.Success(Unit)
            } catch (e: Exception) {
                _addTransactionState.value = handleExceptionMessage(e.message, context)
            }
        }
    }

    fun getAllTransactionsFromFirestore(context: Context) {
        _transactions.value = RequestState.Loading
        firestore.collection(FirestoreCollectionUtils.TRANSACTIONS_COLLECTION)
            .whereEqualTo(FirestoreCollectionUtils.USER_ID, firebaseAuth.currentUser?.uid)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val transactions = task.result.documents.convertFirestoreDocumentToUserTransactionList()
                    viewModelScope.launch {
                        val isFirestoreUpdated = isFirestoreDataUpdated(transactions, context)

                        if (isFirestoreUpdated) {
                            _transactions.value = RequestState.Success(transactions)
                        } else {
                            getAllTransactionsFromSql(context)
                        }
                    }
                } else {
                    val exceptionMessage = task.exception?.message
                    _transactions.value = handleExceptionMessage(exceptionMessage, context)
                }
            }
    }

    fun getAllTransactionsFromSql(context: Context) {
        _transactions.value = RequestState.Loading
        viewModelScope.launch {
            try {
                val transactionList = lumenRepository.selectAllTransactions()
                _transactions.value = RequestState.Success(transactionList)
            } catch (e: Exception) {
                _addTransactionState.value = handleExceptionMessage(e.message, context)
            }
        }
    }

    private suspend fun isFirestoreDataUpdated(transactionsFromFirestore: List<UserTransaction>, context: Context) : Boolean {

        val transactionsFromRoom = withContext(Dispatchers.IO) {
            lumenRepository.selectAllTransactions()
        }

        val roomIds = transactionsFromRoom.map { it.uniqueId }.toSet()
        val firestoreIds = transactionsFromFirestore.map { it.uniqueId }.toSet()

        val transactionsToAdd = transactionsFromRoom.filter { it.uniqueId !in firestoreIds }
        val transactionsToDelete = transactionsFromFirestore.filter { it.uniqueId !in roomIds }

        transactionsToAdd.forEach { transaction ->
            addTransactionOnFirestore(transaction, context)
        }

        transactionsToDelete.forEach {
            deleteTransactionFromFirestore(it.uniqueId, context)
        }
        return false
    }

    private fun handleExceptionMessage(exceptionMessage: String?, context: Context) : RequestState.Error {
        return if (!exceptionMessage.isNullOrBlank()) RequestState.Error(exceptionMessage)
        else RequestState.Error(context.getString(R.string.default_error))
    }

    fun copyUriToInternalStorage(context: Context, uri: Uri): String? {
        val file = File(context.filesDir, "user_profile.jpg")
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getLanguageLabel(language: String?): Int {
        val languageCode = Languages.valueOf(language ?: Languages.EN.name)

        return when (languageCode) {
            Languages.EN -> R.string.user_configuration_english_language
            Languages.PT -> R.string.user_configuration_portuguese_language
            Languages.ES -> R.string.user_configuration_spanish_language
        }
    }

    fun getPrefixByCurrency(): String {
        return when(accountConfig.value?.selectedCurrency) {
            Currencies.USD.name -> "$"
            Currencies.BRL.name -> "R$"
            Currencies.EUR.name -> "€"
            else -> ""
        }
    }

    fun getLocaleByCurrency() : Locale {
        return when(accountConfig.value?.selectedCurrency) {
            Currencies.USD.name -> Locale.US
            Currencies.BRL.name -> Locale("pt", "BR")
            Currencies.EUR.name -> Locale.FRANCE
            else -> Locale.US
        }
    }

    fun getLocaleByLanguage() : Locale {
        return when(accountConfig.value?.selectedLanguage) {
            Languages.EN.name -> Locale.US
            Languages.PT.name -> Locale("pt", "BR")
            Languages.ES.name -> Locale.FRANCE
            else -> Locale.US
        }
    }

    suspend fun deleteAllTransactions() = lumenRepository.deleteAllTransactions()

    fun clearAddTransactionState() {
        _addTransactionState.value = null
    }
 }