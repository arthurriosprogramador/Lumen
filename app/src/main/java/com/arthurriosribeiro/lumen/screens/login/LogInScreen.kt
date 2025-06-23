package com.arthurriosribeiro.lumen.screens.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.components.GoogleButton
import com.arthurriosribeiro.lumen.components.LumenCircularProgressIndicator
import com.arthurriosribeiro.lumen.components.LumenTextField
import com.arthurriosribeiro.lumen.components.LumenTopAppBar
import com.arthurriosribeiro.lumen.components.MiddleTextDivider
import com.arthurriosribeiro.lumen.model.AccountConfiguration
import com.arthurriosribeiro.lumen.model.RequestState
import com.arthurriosribeiro.lumen.screens.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LogInScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    accountConfiguration: State<AccountConfiguration?>) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val email = rememberSaveable {
        mutableStateOf("")
    }

    val password = rememberSaveable {
        mutableStateOf("")
    }

    var isPasswordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val signInState by authViewModel.signInState.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            LumenTopAppBar(title = stringResource(R.string.login_label), actions = {
                IconButton(
                    modifier = Modifier.padding(end = 24.dp),
                    onClick = {
                        navController.popBackStack()
                        authViewModel.cleanSignInState()
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
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    stringResource(R.string.log_in_title),
                    modifier = Modifier.padding(top = 24.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                LumenTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    value = email,
                    placeHolder = { Text(stringResource(R.string.email_text_field_label)) },
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
                )
                ElevatedButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 48.dp),
                    onClick = {
                        accountConfiguration.value?.let {
                            authViewModel.signInWithEmailAndPassword(
                                email = email.value,
                                password = password.value,
                                context = context,
                                accountConfiguration = it
                            )
                        }
                    }
                ) {
                    Text(stringResource(R.string.login_label), modifier = Modifier.padding(8.dp))
                }
                MiddleTextDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    text = stringResource(R.string.or_label)
                )
                GoogleButton(
                    modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(top = 24.dp)) {
                    coroutineScope.launch {
                        accountConfiguration.value?.let {
                            authViewModel.signInWithGoogle(
                                context,
                                it
                            )
                        }
                    }
                }
            }

            LaunchedEffect(signInState) {
                when (authViewModel.signInState.value) {
                    is RequestState.Loading -> isLoading = true
                    is RequestState.Success -> {
                        isLoading = false
                        navController.popBackStack()
                    }
                    is RequestState.Error -> {
                        isLoading = false
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(
                                message = (signInState as RequestState.Error).message
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