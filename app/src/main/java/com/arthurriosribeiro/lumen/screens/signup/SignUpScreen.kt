package com.arthurriosribeiro.lumen.screens.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.components.LumenTextField
import com.arthurriosribeiro.lumen.components.LumenTopAppBar
import com.arthurriosribeiro.lumen.components.MiddleTextDivider


@Composable
fun SignUpScreen(navController: NavController) {
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

    val isSystemInDarkTheme = isSystemInDarkTheme()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            LumenTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                placeHolder = { Text(stringResource(R.string.email_text_field_label)) },
                keyboardType = KeyboardType.Email
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
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
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
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
            )
            ElevatedButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 48.dp),
                onClick = {}
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
                onClick = {}
            ) {
                Image(
                    if (isSystemInDarkTheme) painterResource(R.drawable.google_dark) else painterResource(
                        R.drawable.google_light
                    ), contentDescription = "Google logo"
                )
            }
        }
    }
}