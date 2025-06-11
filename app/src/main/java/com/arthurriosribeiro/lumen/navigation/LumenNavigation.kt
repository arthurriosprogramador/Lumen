package com.arthurriosribeiro.lumen.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import com.arthurriosribeiro.lumen.screens.home.HomeScreen
import com.arthurriosribeiro.lumen.screens.home.tabs.UserConfigurationScreen
import com.arthurriosribeiro.lumen.screens.signup.SignUpScreen
import com.arthurriosribeiro.lumen.screens.splash.SplashScreen
import com.arthurriosribeiro.lumen.screens.viewmodel.AuthViewModel

@Composable
fun LumenNavigation() {
    val navController = rememberNavController()
    val mainViewModel = hiltViewModel<MainViewModel>()
    val authViewModel = hiltViewModel<AuthViewModel>()

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
    }
}