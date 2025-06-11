package com.arthurriosribeiro.lumen.screens.signup

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.screens.viewmodel.AuthViewModel

class SignUpTextFieldsState(
    private val context: Context,
    private val authViewModel: AuthViewModel) {

    private val _signUpFieldsState: MutableState<List<SignUpValidity>> = mutableStateOf(listOf())
    val signUpFieldsState: State<List<SignUpValidity>> = _signUpFieldsState

    private val _signUpIsValid: MutableState<Boolean> = mutableStateOf(true)
    val signUpIsValid: State<Boolean> = _signUpIsValid

    fun checkFields(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        val listFieldError = mutableListOf<SignUpValidity>()
        if (name.isBlank()) listFieldError.add(SignUpValidity.NameError)

        if (email.isBlank() || !authViewModel.isValidEmail(email)) listFieldError.add(SignUpValidity.EmailError)

        if (password.isBlank() || !authViewModel.isValidPassword(password)) {
            listFieldError.add(SignUpValidity.PasswordError(context.getString(R.string.sign_up_password_does_not_match_requirements)))
        } else if (password != confirmPassword) {
            listFieldError.add(SignUpValidity.PasswordError(context.getString(R.string.sign_up_password_fields_do_not_match)))
        }

        _signUpFieldsState.value = listFieldError

        _signUpIsValid.value = _signUpFieldsState.value.isEmpty()
    }

    sealed class SignUpValidity {
        data object NameError : SignUpValidity()
        data object EmailError : SignUpValidity()
        data class PasswordError(val errorText: String) : SignUpValidity()
    }
}