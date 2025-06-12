package com.arthurriosribeiro.lumen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arthurriosribeiro.lumen.R
import kotlinx.coroutines.launch

@Composable
fun GoogleButton(modifier: Modifier, onClick: () -> Unit) {
    val isSystemInDarkTheme = isSystemInDarkTheme()

    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Image(
            if (isSystemInDarkTheme) painterResource(R.drawable.google_dark) else painterResource(
                R.drawable.google_light
            ), contentDescription = "Google logo"
        )
    }
}