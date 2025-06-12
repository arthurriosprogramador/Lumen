package com.arthurriosribeiro.lumen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun LumenRadioButton(options: List<String>, isColumn: Boolean = true, currentSelectedOption: String) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(options.first { it == currentSelectedOption }) }
    if (isColumn) {
        Column(
            modifier = Modifier.selectableGroup()
        ) {
            options.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .selectable(
                            selected = it == selectedOption,
                            onClick = { onOptionSelected(it) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (it == selectedOption),
                        onClick = null
                    )
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}