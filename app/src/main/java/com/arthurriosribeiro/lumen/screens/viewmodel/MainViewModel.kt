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
import com.arthurriosribeiro.lumen.repository.LumenRepository
import com.arthurriosribeiro.lumen.utils.FirestoreCollectionUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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

    suspend fun getAccountConfig() : AccountConfiguration?{
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

    fun getLanguageLabel(language: String): Int {
        val languageCode = Languages.valueOf(language)

        return when (languageCode) {
            Languages.EN -> R.string.user_configuration_english_language
            Languages.PT -> R.string.user_configuration_portuguese_language
            Languages.ES -> R.string.user_configuration_spanish_language
        }
    }
}