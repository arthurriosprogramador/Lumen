package com.arthurriosribeiro.lumen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LumenInfoRow(
    modifier: Modifier = Modifier,
    label: String,
    infoText: String,
    isDividerToggled: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                infoText,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        if (isDividerToggled) HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
    }
}