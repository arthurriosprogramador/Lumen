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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.model.Currencies
import com.arthurriosribeiro.lumen.model.Languages
import com.arthurriosribeiro.lumen.model.UserTransaction
import com.arthurriosribeiro.lumen.screens.addtransaction.AddTransactionsScreen
import com.arthurriosribeiro.lumen.screens.filter.FilterScreen
import com.arthurriosribeiro.lumen.screens.home.HomeScreen
import com.arthurriosribeiro.lumen.screens.login.LogInScreen
import com.arthurriosribeiro.lumen.screens.signup.SignUpScreen
import com.arthurriosribeiro.lumen.screens.splash.SplashScreen
import com.arthurriosribeiro.lumen.screens.viewmodel.AuthViewModel
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import com.arthurriosribeiro.lumen.utils.LocalActivity
import com.arthurriosribeiro.lumen.utils.languages.LocaleUtils
import kotlinx.serialization.json.Json
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

    val verticalEnterTransition = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 1000)
        )

    val verticalExitTransition = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 1000)
        )


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
                enterTransition = { verticalEnterTransition },
                exitTransition = { verticalExitTransition }
            ) {
                SignUpScreen(navController, authViewModel, mainViewModel.accountConfig)
            }

            composable(
                LumenScreens.LOG_IN_SCREEN.name,
                enterTransition = { verticalEnterTransition },
                exitTransition = { verticalExitTransition }
            ) {
                LogInScreen(navController, authViewModel, mainViewModel.accountConfig)
            }

            composable(
                "${LumenScreens.ADD_TRANSACTIONS_SCREEN.name}/" +
                        "{${LumenArguments.USER_TRANSACTION}}/" +
                        "{${LumenArguments.IS_EDIT_SCREEN}}",
                enterTransition = { verticalEnterTransition },
                exitTransition = { verticalExitTransition },
                arguments = listOf(
                    navArgument(name = LumenArguments.USER_TRANSACTION) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument(name = LumenArguments.IS_EDIT_SCREEN) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) {
                val userTransactionJson = it.arguments?.getString(LumenArguments.USER_TRANSACTION)
                val userTransaction = if (userTransactionJson == null) null else Json.decodeFromString<UserTransaction>(userTransactionJson)
                AddTransactionsScreen(
                    navController,
                    mainViewModel,
                    isEditScreen = it.arguments?.getBoolean(LumenArguments.IS_EDIT_SCREEN) ?: false,
                    userTransaction = userTransaction
                )
            }

            composable(
                "${LumenScreens.FILTER_SCREEN.name}/" +
                        "{${LumenArguments.START_VALUE}}/" +
                        "{${LumenArguments.END_VALUE}}/" +
                        "{${LumenArguments.START_DATE}}/" +
                        "{${LumenArguments.END_DATE}}",
                arguments = listOf(
                    navArgument(name = LumenArguments.START_VALUE) {
                        type = NavType.FloatType
                    },
                    navArgument(name = LumenArguments.END_VALUE) {
                        type = NavType.FloatType
                    },
                    navArgument(name = LumenArguments.START_DATE) {
                        type = NavType.LongType
                    },
                    navArgument(name = LumenArguments.END_DATE) {
                        type = NavType.LongType
                    },
                ),
                enterTransition = { verticalEnterTransition },
                exitTransition = { verticalExitTransition }
            ) {
                val startValue = it.arguments?.getFloat(LumenArguments.START_VALUE)
                val endValue = it.arguments?.getFloat(LumenArguments.END_VALUE)
                val startDate = it.arguments?.getLong(LumenArguments.START_DATE)
                val endDate = it.arguments?.getLong(LumenArguments.END_DATE)
                FilterScreen(
                    navController,
                    mainViewModel,
                    startValue ?: 0F,
                    endValue ?: 0F,
                    startDate ?: 0L,
                    endDate ?: 0L
                )
            }
        }
    }
}

object LumenArguments {
    const val START_VALUE = "startValue"
    const val END_VALUE = "endValue"
    const val START_DATE = "startDate"
    const val END_DATE = "endDate"
    const val USER_TRANSACTION = "userTransaction"
    const val IS_EDIT_SCREEN = "isEditScreen"
}