package com.arthurriosribeiro.lumen.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.model.Currencies
import com.arthurriosribeiro.lumen.model.Languages
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import com.arthurriosribeiro.lumen.screens.home.HomeScreen
import com.arthurriosribeiro.lumen.screens.home.tabs.UserConfigurationScreen
import com.arthurriosribeiro.lumen.screens.login.LogInScreen
import com.arthurriosribeiro.lumen.screens.signup.SignUpScreen
import com.arthurriosribeiro.lumen.screens.splash.SplashScreen
import com.arthurriosribeiro.lumen.screens.viewmodel.AuthViewModel
import com.arthurriosribeiro.lumen.utils.LocalActivity
import com.arthurriosribeiro.lumen.utils.languages.LocaleUtils
import java.util.Locale

@Composable
fun LumenNavigation() {
    val navController = rememberNavController()
    val mainViewModel = hiltViewModel<MainViewModel>()
    val authViewModel = hiltViewModel<AuthViewModel>()

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]

    val language = when (locale.language) {
        "en" -> Languages.EN
        "pt" -> Languages.PT
        "es" -> Languages.ES
        else -> Languages.EN
    }

    val currency = when (locale.country) {
        "US" -> Currencies.USD
        "BR" -> Currencies.BRL
        "ES", "PT" -> Currencies.EUR
        else -> Currencies.USD
    }

    val userName = stringResource(R.string.user_logged_off)

    LaunchedEffect(Unit) {
        val config = mainViewModel.getAccountConfig()

        if (config == null) {
            mainViewModel.createAccountConfig(
                name = userName,
                selectedLanguage = language,
                selectedCurrency = currency
            )
        } else {
            configuration.setLocale(Locale(config.selectedLanguage.lowercase()))
        }
    }

    val updatedContext = remember(mainViewModel.accountConfig.value?.selectedLanguage) {
        LocaleUtils.updateLocale(context, mainViewModel.accountConfig.value?.selectedLanguage ?: locale.language)
    }

    val activity = (context as ComponentActivity)

    CompositionLocalProvider(
        LocalContext provides updatedContext,
        LocalActivity provides activity,
        LocalActivityResultRegistryOwner provides activity
    ) {
        NavHost(navController = navController, startDestination = LumenScreens.SPLASH_SCREEN.name) {
            composable(LumenScreens.SPLASH_SCREEN.name) {
                SplashScreen(navController, mainViewModel)
            }

            composable(LumenScreens.HOME_SCREEN.name) {
                HomeScreen(navController, mainViewModel, authViewModel)
            }

            composable(
                LumenScreens.SIGN_UP_SCREEN.name,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 1000)
                    )
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(durationMillis = 1000)
                    )
                }
            ) {
                SignUpScreen(navController, authViewModel, mainViewModel.accountConfig)
            }

            composable(
                LumenScreens.LOG_IN_SCREEN.name,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 1000)
                    )
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(durationMillis = 1000)
                    )
                }
            ) {
                LogInScreen(navController, authViewModel, mainViewModel.accountConfig)
            }
        }
    }
}