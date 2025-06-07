package com.arthurriosribeiro.lumen.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BlueNavy,
    onPrimary = Color.White,
    secondary = EmeraldGreen,
    onSecondary = Color.Black,
    background = DarkGrey,
    onBackground = Color.White,
    surface = NightBlack,
    onSurface = Color.White,
    error = CoralRed,
    onError = Color.Black

)

private val LightColorScheme = lightColorScheme(
    primary = BlueNavy,
    onPrimary = Color.White,
    secondary = EmeraldGreen,
    onSecondary = Color.White,
    background = LightGray,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = CoralRed,
    onError = Color.White,
)

@Composable
fun LumenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}