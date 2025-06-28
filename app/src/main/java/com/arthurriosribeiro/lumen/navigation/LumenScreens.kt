package com.arthurriosribeiro.lumen.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.ui.graphics.vector.ImageVector

enum class LumenScreens(val icon: ImageVector? = null) {
    SPLASH_SCREEN,
    HOME_SCREEN,
    ADD_TRANSACTIONS_SCREEN,
    OVERVIEW_SCREEN(Icons.Rounded.Analytics),
    TRANSACTIONS_SCREEN(Icons.Rounded.Receipt),
    USER_CONFIGURATION_SCREEN(Icons.Rounded.Person),
    SIGN_UP_SCREEN,
    LOG_IN_SCREEN,
    FILTER_SCREEN
}