package com.arthurriosribeiro.lumen.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class LumenScreens(val icon: ImageVector? = null) {
    SPLASH_SCREEN,
    HOME_SCREEN,
    ADD_TRANSACTION_SCREEN(Icons.Rounded.Add),
    FINANCE_SCREEN(Icons.Rounded.Analytics),
    USER_CONFIGURATION_SCREEN(Icons.Rounded.Person),
    SIGN_UP_SCREEN,
    LOG_IN_SCREEN
}