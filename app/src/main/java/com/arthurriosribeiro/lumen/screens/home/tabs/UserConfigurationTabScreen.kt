package com.arthurriosribeiro.lumen.screens.home.tabs

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.components.AccountMenuSection
import com.arthurriosribeiro.lumen.components.CircleAvatar
import com.arthurriosribeiro.lumen.components.LumenBasicAlertDialog
import com.arthurriosribeiro.lumen.components.LumenBottomSheet
import com.arthurriosribeiro.lumen.components.LumenCircularProgressIndicator
import com.arthurriosribeiro.lumen.components.LumenRadioButton
import com.arthurriosribeiro.lumen.components.LumenTextField
import com.arthurriosribeiro.lumen.model.Currencies
import com.arthurriosribeiro.lumen.model.Languages
import com.arthurriosribeiro.lumen.model.SignInState
import com.arthurriosribeiro.lumen.navigation.LumenScreens
import com.arthurriosribeiro.lumen.screens.viewmodel.AuthViewModel
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import com.arthurriosribeiro.lumen.utils.animation.orDash
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserConfigurationScreen(
    navController: NavController,
    viewModel: MainViewModel,
    authViewModel: AuthViewModel
) {

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    val bottomSheetState = rememberModalBottomSheetState()

    var showNameBottomSheet by remember { mutableStateOf(false) }
    var showLanguageBottomSheet by remember { mutableStateOf(false) }
    var showCurrencyBottomSheet by remember { mutableStateOf(false) }

    val name = remember {
        mutableStateOf(viewModel.accountConfig.value?.name.orEmpty())
    }
    val (selectedLanguage, onLanguageSelected) = remember {
        mutableStateOf(Languages.valueOf(viewModel.accountConfig.value?.selectedLanguage.orEmpty()))
    }
    val (selectedCurrency, onCurrencySelected) = remember {
        mutableStateOf(viewModel.accountConfig.value?.selectedCurrency.orEmpty())
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var isShowingExcludeAccountAlert by remember {
        mutableStateOf(false)
    }

    val signInState by authViewModel.signInState.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold {

        if (showNameBottomSheet) {
            LumenBottomSheet(
                onDismissRequest = { showNameBottomSheet = false },
                sheetState = bottomSheetState,
                title = stringResource(R.string.user_configuration_name_label),
                content = {
                    LumenTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        placeHolder = {},
                        value = name
                    )
                },
                isEditBottomSheet = true,
                onEditButtonClick = {
                    viewModel.updateUserName(name.value, viewModel.accountConfig.value?.id ?: 0)
                    showNameBottomSheet = false
                }
            )
        }

        if (showLanguageBottomSheet) {
            LumenBottomSheet(
                onDismissRequest = { showLanguageBottomSheet = false },
                sheetState = bottomSheetState,
                title = stringResource(R.string.user_configuration_selected_language),
                content = {
                    LumenRadioButton(
                        modifier = Modifier.padding(top = 24.dp),
                        options = Languages.entries.toList(),
                        selectedOption = selectedLanguage,
                        onOptionSelected = onLanguageSelected,
                        viewModel = viewModel
                    )
                },
                isEditBottomSheet = true,
                onEditButtonClick = {
                    viewModel.updateUserLanguage(
                        selectedLanguage.name,
                        viewModel.accountConfig.value?.id ?: 0
                    )
                    showLanguageBottomSheet = false
                }
            )
        }

        if (showCurrencyBottomSheet) {
            LumenBottomSheet(
                onDismissRequest = { showCurrencyBottomSheet = false },
                sheetState = bottomSheetState,
                title = stringResource(R.string.user_configuration_selected_currency),
                content = {
                    LumenRadioButton(
                        modifier = Modifier.padding(top = 24.dp),
                        options = Currencies.entries.map { currency -> currency.name }.toList(),
                        selectedOption = selectedCurrency,
                        onOptionSelected = onCurrencySelected
                    )
                },
                isEditBottomSheet = true,
                onEditButtonClick = {
                    viewModel.updateUserCurrency(
                        selectedCurrency,
                        viewModel.accountConfig.value?.id ?: 0
                    )
                    showCurrencyBottomSheet = false
                }
            )
        }

        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .clickable {
                                showNameBottomSheet = true
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircleAvatar(Modifier, viewModel)
                        Text(
                            stringResource(
                                R.string.user_configuration_greeting_user,
                                viewModel.accountConfig.value?.name.orEmpty()
                            ),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = stringResource(R.string.chevron_right_description),
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                    AccountMenuSection(
                        sectionLabel = stringResource(R.string.user_configuration_selected_language),
                        sectionText = stringResource(viewModel.getLanguageLabel(viewModel.accountConfig.value?.selectedLanguage.orDash())),
                        onClick = { showLanguageBottomSheet = true }
                    )
                    AccountMenuSection(
                        sectionLabel = stringResource(R.string.user_configuration_selected_currency),
                        sectionText = viewModel.accountConfig.value?.selectedCurrency.orDash(),
                        onClick = { showCurrencyBottomSheet = true }
                    )
                }
                if (viewModel.accountConfig.value?.isUserLoggedIn == false) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Text(
                            stringResource(R.string.user_configuration_ask_login_title),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            stringResource(R.string.user_configuration_ask_login_message),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        TextButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                navController.navigate(LumenScreens.LOG_IN_SCREEN.name)
                            }) {
                            Text(
                                stringResource(R.string.login_label),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                textDecoration = TextDecoration.Underline
                            )
                        }
                        TextButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                navController.navigate(LumenScreens.SIGN_UP_SCREEN.name)
                            }) {
                            Text(
                                stringResource(R.string.sign_up_label),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        TextButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.accountConfig.value?.let { config ->
                                        authViewModel.signOut(
                                            config
                                        )
                                    }
                                    viewModel.getAccountConfig()
                                }
                            }) {
                            Text(
                                stringResource(R.string.sign_out_label),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                textDecoration = TextDecoration.Underline
                            )
                        }
                        TextButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                isShowingExcludeAccountAlert = true
                            }) {
                            Text(
                                stringResource(R.string.user_configuration_exclude_account),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    }
                }
            }
        }
        LaunchedEffect(signInState) {
            when (authViewModel.signInState.value) {
                is SignInState.Loading -> isLoading = true
                is SignInState.Success -> {
                    isLoading = false
                }
                is SignInState.Error -> {
                    isLoading = false
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = (signInState as SignInState.Error).message
                        )
                    }
                }
                else -> {
                    viewModel.getAccountConfig()
                    isLoading = false
                }
            }
        }

        if (isLoading) LumenCircularProgressIndicator()

        if (isShowingExcludeAccountAlert) {
            LumenBasicAlertDialog(
                onDismissRequest = { isShowingExcludeAccountAlert = false },
                title = stringResource(R.string.user_configuration_exclude_account_modal_title),
                message = stringResource(R.string.user_configuration_exclude_account_modal_message),
                confirmButtonLabel = stringResource(R.string.user_configuration_exclude_account_modal_confirm_button),
                onClickConfirmButton = {
                    viewModel.accountConfig.value?.let { config ->
                        authViewModel.deleteAllUserData(
                            context,
                            config
                        )
                    }
                    isShowingExcludeAccountAlert = false
                },
                cancelButtonLabel = stringResource(R.string.user_configuration_exclude_account_modal_cancel_button),
                onClickCancelButton = {
                    isShowingExcludeAccountAlert = false
                }
            )
        }
    }
}

