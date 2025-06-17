package com.arthurriosribeiro.lumen.utils

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalActivity = staticCompositionLocalOf<ComponentActivity> {
    error("No Activity provided")
}

@Composable
fun provideActivityResultRegistry() = LocalActivityResultRegistryOwner.current