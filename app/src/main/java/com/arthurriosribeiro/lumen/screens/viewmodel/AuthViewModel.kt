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
import com.arthurriosribeiro.lumen.model.SignInState
import com.arthurriosribeiro.lumen.repository.LumenRepository
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

    private val _signInState: MutableStateFlow<SignInState?> =
        MutableStateFlow(null)
    val signInState: StateFlow<SignInState?> = _signInState

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
            _signInState.value = SignInState.Loading

            val response = credentialManager.getCredential(context = context, request = request)
            handleSignInResponse(response, accountConfiguration)
        } catch (e: Exception) {
            _signInState.value =
                SignInState.Error(e.message ?: context.getString(R.string.default_error))
        }
    }

    private suspend fun handleSignInResponse(
        response: GetCredentialResponse,
        accountConfiguration: AccountConfiguration
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
                        accountConfiguration
                    )
                    _signInState.value = SignInState.Success
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
        _signInState.value = SignInState.Loading
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendUserInformationToFirestore(
                        name,
                        accountConfiguration
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
                            SignInState.Error(context.getString(R.string.default_error))
                    } else {
                        _signInState.value = SignInState.Error(exceptionMessage)
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
        _signInState.value = SignInState.Loading
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _signInState.value = SignInState.Success
                viewModelScope.launch {
                    lumenRepository.updateUserLoggedIn(
                        id = accountConfiguration.id,
                        isUserLoggedIn = true
                    )
                }
            } else {
                val exceptionMessage = task.exception?.message
                if (exceptionMessage.isNullOrBlank()) {
                    _signInState.value =
                        SignInState.Error(context.getString(R.string.default_error))
                } else {
                    _signInState.value = SignInState.Error(exceptionMessage)
                }
            }
        }
    }

    suspend fun signOut(accountConfiguration: AccountConfiguration) {
        _signInState.value = SignInState.Loading
        firebaseAuth.signOut()
        lumenRepository.updateUserLoggedIn(
            isUserLoggedIn = false,
            id = accountConfiguration.id
        )
        _signInState.value = SignInState.SignedOut
    }

    private fun sendUserInformationToFirestore(
        name: String? = null,
        accountConfiguration: AccountConfiguration
    ): Task<QuerySnapshot> {
        return firestore.collection(USERS_COLLECTION)
            .whereEqualTo(USER_EMAIL, firebaseAuth.currentUser?.email).get()
            .addOnCompleteListener { queryTask ->
                if (queryTask.isSuccessful && queryTask.result.isEmpty) {
                    val userData = hashMapOf(
                        USER_EMAIL to firebaseAuth.currentUser?.email,
                        USER_NAME to name,
                        USER_SELECTED_LANGUAGE to accountConfiguration.selectedLanguage,
                        USER_SELECTED_CURRENCY to accountConfiguration.selectedCurrency
                    )

                    firestore.collection(USERS_COLLECTION).document().set(userData)
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

    fun cleanSignInState() {
        _signInState.value = null
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val USER_EMAIL = "email"
        private const val USER_NAME = "name"
        private const val USER_SELECTED_LANGUAGE = "selected_language"
        private const val USER_SELECTED_CURRENCY = "selected_currency"
    }
}
