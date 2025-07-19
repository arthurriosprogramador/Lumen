package com.arthurriosribeiro.lumen.screens.viewmodel

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.model.AccountConfiguration
import com.arthurriosribeiro.lumen.model.Currencies
import com.arthurriosribeiro.lumen.model.Languages
import com.arthurriosribeiro.lumen.model.RequestState
import com.arthurriosribeiro.lumen.repository.LumenRepository
import com.arthurriosribeiro.lumen.utils.FirestoreCollectionUtils
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val lumenRepository: LumenRepository
) : ViewModel() {

    private val _signInState: MutableStateFlow<RequestState<Unit>?> =
        MutableStateFlow(null)
    val signInState: StateFlow<RequestState<Unit>?> = _signInState

    fun isValidEmail(email: String): Boolean {
        val regex =
            Regex("""^[a-zA-Z0-9^$*.{}\[\]()?"!@#%&/\\,><':;|_~]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""")
        return regex.matches(email)
    }

    fun isValidPassword(password: String): Boolean {
        val regex =
            Regex("""^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\w\s])(?=^[a-zA-Z\d^$*.\[\]{}()?"!@#%&/\\,><':;|_~]+$).{8,}$""")
        return regex.matches(password)
    }

    suspend fun signInWithGoogle(context: Context, accountConfiguration: AccountConfiguration) {
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        try {
            _signInState.value = RequestState.Loading

            val response = credentialManager.getCredential(context = context, request = request)
            handleSignInResponse(response, accountConfiguration, context)
        } catch (e: Exception) {
            _signInState.value =
                RequestState.Error(e.message ?: context.getString(R.string.default_error))
        }
    }

    private suspend fun handleSignInResponse(
        response: GetCredentialResponse,
        accountConfiguration: AccountConfiguration,
        context: Context
    ) {
        when (val credential = response.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseCredential =
                        GoogleAuthProvider.getCredential(googleCredential.idToken, null)

                    firebaseAuth.signInWithCredential(firebaseCredential).await()
                    sendUserInformationToFirestore(
                        (firebaseAuth.currentUser?.displayName ?: accountConfiguration.name),
                        accountConfiguration,
                        context
                    )
                    _signInState.value = RequestState.Success(Unit)
                }
            }

            else -> {
                throw Exception("Unexpected credential type")
            }
        }
    }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        name: String,
        context: Context,
        accountConfiguration: AccountConfiguration
    ) {
        _signInState.value = RequestState.Loading
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendUserInformationToFirestore(
                        name,
                        accountConfiguration,
                        context
                    ).addOnCompleteListener {
                        if (it.isSuccessful) signInWithEmailAndPassword(
                            email = email,
                            password = password,
                            context = context,
                            accountConfiguration = accountConfiguration
                        )
                    }
                } else {
                    val exceptionMessage = task.exception?.message
                    if (exceptionMessage.isNullOrBlank()) {
                        _signInState.value =
                            RequestState.Error(context.getString(R.string.default_error))
                    } else {
                        _signInState.value = RequestState.Error(exceptionMessage)
                    }
                }
            }
    }

    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        context: Context,
        accountConfiguration: AccountConfiguration
    ) {
        _signInState.value = RequestState.Loading
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _signInState.value = RequestState.Success(Unit)
                viewModelScope.launch {
                    lumenRepository.updateUserLoggedIn(
                        id = accountConfiguration.id,
                        isUserLoggedIn = true
                    )
                    getUserInformation(
                        email = email,
                        accountConfiguration = accountConfiguration,
                        context = context
                    )
                }
            } else {
                val exceptionMessage = task.exception?.message
                if (exceptionMessage.isNullOrBlank()) {
                    _signInState.value =
                        RequestState.Error(context.getString(R.string.default_error))
                } else {
                    _signInState.value = RequestState.Error(exceptionMessage)
                }
            }
        }
    }

    suspend fun signOut(accountConfiguration: AccountConfiguration) {
        _signInState.value = RequestState.Loading
        firebaseAuth.signOut()
        lumenRepository.updateUserLoggedIn(
            isUserLoggedIn = false,
            id = accountConfiguration.id
        )
        _signInState.value = RequestState.SignedOut
    }

    private fun sendUserInformationToFirestore(
        name: String? = null,
        accountConfiguration: AccountConfiguration,
        context: Context
    ): Task<QuerySnapshot> {
        return firestore.collection(FirestoreCollectionUtils.USERS_COLLECTION)
            .whereEqualTo(FirestoreCollectionUtils.USER_EMAIL, firebaseAuth.currentUser?.email)
            .get()
            .addOnCompleteListener { queryTask ->
                if (queryTask.isSuccessful && queryTask.result.isEmpty) {
                    val userData = mapOf(
                        FirestoreCollectionUtils.USER_ID to firebaseAuth.currentUser?.uid,
                        FirestoreCollectionUtils.USER_EMAIL to firebaseAuth.currentUser?.email,
                        FirestoreCollectionUtils.USER_NAME to name,
                        FirestoreCollectionUtils.USER_SELECTED_LANGUAGE to accountConfiguration.selectedLanguage,
                        FirestoreCollectionUtils.USER_SELECTED_CURRENCY to accountConfiguration.selectedCurrency
                    )

                    firestore.collection(FirestoreCollectionUtils.USERS_COLLECTION).document()
                        .set(userData)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                viewModelScope.launch {
                                    lumenRepository.updateUserName(
                                        id = accountConfiguration.id,
                                        name = name.orEmpty()
                                    )
                                    lumenRepository.updateUserLoggedIn(
                                        id = accountConfiguration.id,
                                        isUserLoggedIn = true
                                    )
                                }
                            } else {
                                val exceptionMessage = task.exception?.message
                                if (exceptionMessage.isNullOrBlank()) {
                                    _signInState.value =
                                        RequestState.Error(context.getString(R.string.default_error))
                                } else {
                                    _signInState.value = RequestState.Error(exceptionMessage)
                                }
                            }
                        }
                } else {
                    viewModelScope.launch {
                        lumenRepository.updateUserName(
                            id = accountConfiguration.id,
                            name = name.orEmpty()
                        )
                        lumenRepository.updateUserLoggedIn(
                            id = accountConfiguration.id,
                            isUserLoggedIn = true
                        )
                    }
                }
            }
    }

    private fun getUserInformation(
        email: String,
        accountConfiguration: AccountConfiguration,
        context: Context
    ) {
        firestore.collection(FirestoreCollectionUtils.USERS_COLLECTION)
            .whereEqualTo(FirestoreCollectionUtils.USER_EMAIL, email).get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val document = snapshot.documents.first()
                    viewModelScope.launch {
                        lumenRepository.updateUserName(
                            document.getString(FirestoreCollectionUtils.USER_NAME)
                                ?: context.getString(R.string.user_logged_off),
                            accountConfiguration.id
                        )
                        lumenRepository.updateUserLanguage(
                            document.getString(
                                FirestoreCollectionUtils.USER_SELECTED_LANGUAGE
                            ) ?: Languages.EN.name, accountConfiguration.id
                        )
                        lumenRepository.updateUserCurrency(
                            document.getString(
                                FirestoreCollectionUtils.USER_SELECTED_CURRENCY
                            ) ?: Currencies.USD.name, accountConfiguration.id
                        )
                    }
                }
            }
    }

    fun deleteAllUserData(context: Context, accountConfiguration: AccountConfiguration) {
        val currentUser = firebaseAuth.currentUser
        _signInState.value = RequestState.Loading
        firestore.collection(FirestoreCollectionUtils.USERS_COLLECTION)
            .whereEqualTo(FirestoreCollectionUtils.USER_EMAIL, currentUser?.email)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result

                    if (!snapshot.isEmpty) {
                        val document = snapshot.documents.first()
                        document.reference.delete()
                    }

                    viewModelScope.launch {
                        val language = accountConfiguration.selectedLanguage
                        val currency = accountConfiguration.selectedCurrency
                        lumenRepository.deleteAllTransactions()
                        lumenRepository.deleteAllAccountConfiguration()
                        lumenRepository.insertAccountConfiguration(
                            AccountConfiguration(id = 0,
                                name = context.getString(R.string.user_logged_off),
                                selectedLanguage = language,
                                selectedCurrency = currency
                            )
                        )
                    }

                    currentUser?.delete()?.addOnCompleteListener { deleteUserTask ->
                        if (deleteUserTask.isSuccessful) {
                            _signInState.value = RequestState.SignedOut
                        } else {
                            val deleteUserTaskExceptionMessage = deleteUserTask.exception?.message
                            if (!deleteUserTaskExceptionMessage.isNullOrBlank()){
                                _signInState.value = RequestState.Error(deleteUserTaskExceptionMessage)
                            } else {
                                _signInState.value = RequestState.Error(context.getString(R.string.default_error))
                            }
                        }
                    }
                } else {
                    val taskExceptionMessage = task.exception?.message
                    if (!taskExceptionMessage.isNullOrBlank()) {
                        _signInState.value = RequestState.Error(taskExceptionMessage)
                    } else {
                        _signInState.value = RequestState.Error(context.getString(R.string.default_error))
                    }
                }
            }
    }

    fun cleanSignInState() {
        _signInState.value = null
    }
}
