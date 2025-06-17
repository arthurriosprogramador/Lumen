package com.arthurriosribeiro.lumen.screens.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.navigation.NavController
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.model.Currencies
import com.arthurriosribeiro.lumen.model.Languages
import com.arthurriosribeiro.lumen.navigation.LumenScreens
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import com.arthurriosribeiro.lumen.utils.animation.shimmerAnimation
import kotlinx.coroutines.delay
import java.util.Locale


@Composable
fun SplashScreen(navController: NavController, viewModel: MainViewModel) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary
    )

    LaunchedEffect(Unit) {
        delay(1800)
        navController.navigate(LumenScreens.HOME_SCREEN.name) {
            popUpTo(LumenScreens.SPLASH_SCREEN.name) { inclusive = true }
        }
    }

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(
                    R.string.app_name
                ).uppercase(),
                style = MaterialTheme.typography.displayLarge.copy(
                    brush = shimmerAnimation(shimmerColors)
                ),
                textDecoration = TextDecoration.Underline
            )
            Text(
                stringResource(
                    R.string.app_description
                ).uppercase(),
                style = MaterialTheme.typography.displaySmall.copy(
                    brush = shimmerAnimation(shimmerColors)
                ),
            )
        }
    }
}