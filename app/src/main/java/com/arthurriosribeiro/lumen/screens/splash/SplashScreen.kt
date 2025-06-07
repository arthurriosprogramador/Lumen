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
import com.arthurriosribeiro.lumen.screens.MainViewModel
import com.arthurriosribeiro.lumen.utils.animation.shimmerAnimation
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavController, viewModel: MainViewModel) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary
    )

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
        val config = viewModel.getAccountConfig()

        if (config == null) {
            viewModel.createAccountConfig(
                name = userName,
                selectedLanguage = language,
                selectedCurrency = currency
            )
        }

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
//            Box(modifier = Modifier.fillMaxSize()) {
//                Canvas(modifier = Modifier.fillMaxSize()) {
//                    val beamHeight = size.height * 0.2F
//                    val beamWidth = size.width * 0.9F
//                    val topY = center.y - beamHeight
//                    drawPath(
//                        path = Path().apply {
//                            moveTo(center.x, topY)
//                            lineTo(center.x - beamWidth / 2F, center.y + beamHeight)
//                            lineTo(center.x + beamWidth / 2F, center.y + beamHeight)
//                            close()
//                        },
//                        brush = Brush.radialGradient(
//                            colors = listOf(
//                                SoftGold.copy(alpha = 0.5F),
//                                Color.Transparent
//                            ),
//                            center = center,
//                            radius = beamHeight
//                        )
//                    )
//                }
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
//}