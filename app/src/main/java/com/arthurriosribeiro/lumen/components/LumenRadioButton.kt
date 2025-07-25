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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.model.Languages
import com.arthurriosribeiro.lumen.model.TransactionType
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import java.util.Locale

@Composable
fun <T>LumenRadioButton(
    modifier: Modifier,
    options: List<T>? = null,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    isColumn: Boolean = true,
    viewModel: MainViewModel? = null) {
    if (isColumn) {
        Column(
            modifier = modifier.selectableGroup()
        ) {
            if (!options.isNullOrEmpty()) {
                options.forEach {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 16.dp)
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
                            text = when (it) {
                                is Languages -> {
                                    stringResource(viewModel?.getLanguageLabel(it.name) ?: 0)
                                }

                                is TransactionType -> {
                                    when (it) {
                                        TransactionType.EXPENSES -> stringResource(R.string.finance_tracker_expenses)
                                        TransactionType.INCOME -> stringResource(R.string.finance_tracker_incomes)
                                        TransactionType.ALL -> stringResource(R.string.finance_tracker_all_transactions)
                                    }
                                }

                                else -> {
                                    it as String
                                }
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    } else {
        Row(
            modifier = modifier.selectableGroup()
        ) {
            if (!options.isNullOrEmpty()) {
                options.forEach {
                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp)
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
                            text = when (it) {
                                is Languages -> {
                                    stringResource(viewModel?.getLanguageLabel(it.name) ?: 0)
                                }

                                is TransactionType -> {
                                    it.name.lowercase().replaceFirstChar { char -> char.uppercaseChar() }
                                }

                                else -> {
                                    it as String
                                }
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
}