package com.arthurriosribeiro.lumen.screens.home.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.components.AccountMenuSection
import com.arthurriosribeiro.lumen.components.CircleAvatar
import com.arthurriosribeiro.lumen.model.Languages
import com.arthurriosribeiro.lumen.navigation.LumenScreens
import com.arthurriosribeiro.lumen.screens.viewmodel.AuthViewModel
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import com.arthurriosribeiro.lumen.utils.animation.orDash
import kotlinx.coroutines.launch

@Composable
fun UserConfigurationScreen(
    navController: NavController,
    viewModel: MainViewModel,
    authViewModel: AuthViewModel) {

    val coroutineScope = rememberCoroutineScope()

    Scaffold {
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
                        .padding(horizontal = 24.dp, vertical = 8.dp),
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
                    viewModel = viewModel,
                    sectionLabel = stringResource(R.string.user_configuration_selected_language),
                    sectionText = stringResource(getLanguageLabel(viewModel.accountConfig.value?.selectedLanguage.orDash()))
                )
                AccountMenuSection(
                    viewModel = viewModel,
                    sectionLabel = stringResource(R.string.user_configuration_selected_currency),
                    sectionText = viewModel.accountConfig.value?.selectedCurrency.orDash()
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
                        onClick = {}) {
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
                                viewModel.accountConfig.value?.let { config -> authViewModel.signOut(config) }
                                viewModel.getAccountConfig()
                            }
                        }) {
                        Text(
                            stringResource(R.string.sign_out_label),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            }
        }
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