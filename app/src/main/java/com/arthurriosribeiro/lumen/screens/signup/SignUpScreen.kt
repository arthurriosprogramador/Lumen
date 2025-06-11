package com.arthurriosribeiro.lumen.screens.signup

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.components.LumenCircularProgressIndicator
import com.arthurriosribeiro.lumen.components.LumenTextField
import com.arthurriosribeiro.lumen.components.LumenTopAppBar
import com.arthurriosribeiro.lumen.components.MiddleTextDivider
import com.arthurriosribeiro.lumen.model.AccountConfiguration
import com.arthurriosribeiro.lumen.model.SignInState
import com.arthurriosribeiro.lumen.screens.signup.SignUpTextFieldsState.*
import com.arthurriosribeiro.lumen.screens.viewmodel.AuthViewModel
import kotlinx.coroutines.launch


@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    accountConfiguration: State<AccountConfiguration?>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val name = rememberSaveable {
        mutableStateOf("")
    }

    val email = rememberSaveable {
        mutableStateOf("")
    }

    val password = rememberSaveable {
        mutableStateOf("")
    }

    val confirmPassword = rememberSaveable {
        mutableStateOf("")
    }

    var isPasswordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    val signUpTextFieldsState = SignUpTextFieldsState(context, authViewModel)

    var isLoading by remember {
        mutableStateOf(false)
    }

    val signInState by authViewModel.signInState.collectAsState()

    val isSystemInDarkTheme = isSystemInDarkTheme()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            LumenTopAppBar(title = stringResource(R.string.sign_up_label), actions = {
                IconButton(
                    modifier = Modifier.padding(end = 24.dp),
                    onClick = {
                        navController.popBackStack()
                    }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.close_icon_description)
                    )
                }

            })
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        Box() {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(R.string.sign_up_title_instructions),
                    modifier = Modifier.padding(top = 24.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                LumenTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    value = name,
                    placeHolder = { Text(stringResource(R.string.sign_up_name_text_field_label)) },
                    isError = signUpTextFieldsState.signUpFieldsState.value.contains(SignUpValidity.NameError)
                )
                LumenTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    value = email,
                    placeHolder = { Text(stringResource(R.string.email_text_field_label)) },
                    keyboardType = KeyboardType.Email,
                    isError = signUpTextFieldsState.signUpFieldsState.value.contains(SignUpValidity.EmailError)
                )
                LumenTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    value = password,
                    placeHolder = { Text(stringResource(R.string.password_text_field_label)) },
                    keyboardType = KeyboardType.Password,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                isPasswordVisible = !isPasswordVisible
                            }
                        ) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = if (isPasswordVisible) stringResource(R.string.visibility_icon_description) else stringResource(
                                    R.string.visibility_off_icon_description
                                )
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = signUpTextFieldsState.signUpFieldsState.value.any { it is SignUpValidity.PasswordError },
                    supportingText = {
                        val error = signUpTextFieldsState.signUpFieldsState.value.find { it is SignUpValidity.PasswordError }
                        Text(
                            if (error != null) (error as SignUpValidity.PasswordError).errorText else "",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.error
                            ))
                    }
                )
                LumenTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    value = confirmPassword,
                    placeHolder = { Text(stringResource(R.string.sign_up_confirm_password_text_field_label)) },
                    keyboardType = KeyboardType.Password,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                isPasswordVisible = !isPasswordVisible
                            }
                        ) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = if (isPasswordVisible) stringResource(R.string.visibility_icon_description) else stringResource(
                                    R.string.visibility_off_icon_description
                                )
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = signUpTextFieldsState.signUpFieldsState.value.any { it is SignUpValidity.PasswordError },
                    supportingText = {
                        val error = signUpTextFieldsState.signUpFieldsState.value.find { it is SignUpValidity.PasswordError }
                        Text(
                            if (error != null) (error as SignUpValidity.PasswordError).errorText else "",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.error
                            ))
                    }
                )
                ElevatedButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 48.dp),
                    onClick = {
                        signUpTextFieldsState.checkFields(
                            name.value,
                            email.value,
                            password.value,
                            confirmPassword.value
                        )

                        if (signUpTextFieldsState.signUpIsValid.value) {
                            accountConfiguration.value?.let {
                                authViewModel.createUserWithEmailAndPassword(
                                    email.value.trim(),
                                    password.value.trim(),
                                    name.value.trim(),
                                    context,
                                    it
                                )
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.sign_up_label), modifier = Modifier.padding(8.dp))
                }
                MiddleTextDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    text = stringResource(R.string.or_label)
                )
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    onClick = {
                        coroutineScope.launch {
                            accountConfiguration.value?.let {
                                authViewModel.signInWithGoogle(
                                    context,
                                    it
                                )
                            }
                        }
                    }
                ) {
                    Image(
                        if (isSystemInDarkTheme) painterResource(R.drawable.google_dark) else painterResource(
                            R.drawable.google_light
                        ), contentDescription = "Google logo"
                    )
                }
            }

            LaunchedEffect(signInState) {
                when (authViewModel.signInState.value) {
                    is SignInState.Loading -> isLoading = true
                    is SignInState.Success -> {
                        isLoading = false
                        navController.popBackStack()
                    }
                    is SignInState.Error -> {
                        isLoading = false
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(
                                message = (signInState as SignInState.Error).message
                            )
                        }
                    }
                    else -> {}
                }
            }
        }

        if (isLoading) LumenCircularProgressIndicator()
    }
}