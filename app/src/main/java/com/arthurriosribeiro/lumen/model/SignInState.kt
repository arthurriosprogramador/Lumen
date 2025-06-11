package com.arthurriosribeiro.lumen.model

sealed class SignInState {
    data object SignedOut : SignInState()
    data object Loading : SignInState()
    data object Success : SignInState()
    data class Error(val message: String) : SignInState()
}