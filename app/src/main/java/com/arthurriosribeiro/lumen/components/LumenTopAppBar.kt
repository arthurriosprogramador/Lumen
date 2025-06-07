package com.arthurriosribeiro.lumen.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LumenTopAppBar(title: String, actions: @Composable (RowScope.() -> Unit) = {}) {
    TopAppBar(
        modifier = Modifier.shadow(5.dp),
        title = {
            Text(
                title,
                modifier = Modifier.padding(start = 24.dp)
            )
        },
        actions = actions,
        windowInsets = WindowInsets.statusBars
    )
}