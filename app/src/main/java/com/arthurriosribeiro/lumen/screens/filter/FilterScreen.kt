package com.arthurriosribeiro.lumen.screens.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.components.LumenRadioButton
import com.arthurriosribeiro.lumen.components.LumenTextField
import com.arthurriosribeiro.lumen.components.LumenTopAppBar
import com.arthurriosribeiro.lumen.components.TransactionCategoriesCheckboxGrid
import com.arthurriosribeiro.lumen.model.TransactionCategory
import com.arthurriosribeiro.lumen.model.TransactionType
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import com.arthurriosribeiro.lumen.utils.NumberFormatProvider
import com.arthurriosribeiro.lumen.utils.formatDate
import com.arthurriosribeiro.lumen.utils.roundToTwoDecimals
import com.arthurriosribeiro.lumen.utils.toSystemZoneMillis
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    navController: NavController,
    viewModel: MainViewModel,
    startValue: Float,
    endValue: Float,
    startDate: Long,
    endDate: Long
) {

    val numberFormat = NumberFormatProvider.getNumberFormat(
        viewModel.getLocaleByCurrency()
    )

    val (selectedType, onTypeSelected) = remember {
        mutableStateOf(viewModel.selectedFilter.value?.transactionType ?: TransactionType.ALL)
    }
    val selectedCategory = remember {
        viewModel.selectedFilter.value?.transactionCategory?.toMutableStateList() ?: mutableStateListOf()
    }

    val selectedRange = remember {
        mutableStateOf(
                viewModel.selectedFilter.value?.valueRange?.let {
                    it.start.toBigDecimal().toFloat()..it.endInclusive.toBigDecimal().toFloat()
                }
            ?: startValue..endValue
        )
    }

    val minText = remember { mutableStateOf(numberFormat.format(selectedRange.value.start)) }
    val maxText = remember { mutableStateOf(numberFormat.format(selectedRange.value.endInclusive)) }

    val datePickerState = rememberDatePickerState()
    var isInitialDatePickerDialogOpened by rememberSaveable {
        mutableStateOf(false)
    }
    val initialTimestamp = rememberSaveable {
        mutableLongStateOf(viewModel.selectedFilter.value?.timestampRange?.first ?: startDate)
    }

    var isFinalDatePickerDialogOpened by rememberSaveable {
        mutableStateOf(false)
    }
    val finalTimestamp = rememberSaveable {
        mutableLongStateOf(viewModel.selectedFilter.value?.timestampRange?.endInclusive ?: endDate)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            LumenTopAppBar(title = stringResource(R.string.filter_screen_title), actions = {
                IconButton(
                    modifier = Modifier.padding(end = 24.dp),
                    onClick = {
                        navController.popBackStack()
                    }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.close_icon_description)
                    )
                }

            })
        },
        bottomBar = {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = WindowInsets.systemBars.asPaddingValues()
                                .calculateBottomPadding()
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 16.dp)
                            .clip(RectangleShape),
                        shape = RectangleShape,
                        contentPadding = PaddingValues(16.dp),
                        onClick = {
                            val timestampRange =
                                if (initialTimestamp.longValue == startDate && finalTimestamp.longValue == endDate) null
                                else initialTimestamp.longValue..finalTimestamp.longValue
                            val valueRange =
                                if (selectedRange.value.start == startValue && selectedRange.value.endInclusive == endValue) null
                                else selectedRange.value.start.toString()
                                    .toDouble()..selectedRange.value.endInclusive.toString()
                                    .toDouble()
                            viewModel.applyFilter(
                                timestampRange = timestampRange,
                                valueRange = valueRange,
                                transactionType = selectedType,
                                transactionCategory = selectedCategory
                            )
                            navController.popBackStack()
                        }
                    ) {
                        Text(stringResource(R.string.filter_transaction_apply_filter))
                    }
                    TextButton(
                        modifier = Modifier
                            .padding(vertical = 8.dp),
                        onClick = {
                            viewModel.clearFilter()
                            onTypeSelected(TransactionType.ALL)
                            selectedCategory.clear()
                            selectedRange.value = startValue..endValue
                            initialTimestamp.longValue = startDate
                            finalTimestamp.longValue = endDate
                        }
                    ) {
                        Text(stringResource(R.string.filter_transaction_clear_filter))
                    }
                }
            }
        }
    ) { innerPadding ->
            Box {
                if (isInitialDatePickerDialogOpened) {
                    DatePickerDialog(
                        onDismissRequest = { isInitialDatePickerDialogOpened = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    datePickerState.selectedDateMillis?.let {
                                        initialTimestamp.longValue = it.toSystemZoneMillis()
                                    }
                                    isInitialDatePickerDialogOpened = false
                                }
                            ) {
                                Text(stringResource(R.string.confirm_button_label))
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { isInitialDatePickerDialogOpened = false }
                            ) {
                                Text(stringResource(R.string.cancel_button_label))
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                if (isFinalDatePickerDialogOpened) {
                    DatePickerDialog(
                        onDismissRequest = { isFinalDatePickerDialogOpened = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    datePickerState.selectedDateMillis?.let {
                                        finalTimestamp.longValue = it.toSystemZoneMillis()
                                    }
                                    isFinalDatePickerDialogOpened = false
                                }
                            ) {
                                Text(stringResource(R.string.confirm_button_label))
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { isFinalDatePickerDialogOpened = false }
                            ) {
                                Text(stringResource(R.string.cancel_button_label))
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp)
                        .fillMaxSize()
                ) {
                    item {
                        Column {
                            Text(
                                stringResource(R.string.filter_transaction_type),
                                modifier = Modifier.padding(top = 24.dp),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            LumenRadioButton(
                                modifier = Modifier,
                                options = TransactionType.entries.toList(),
                                selectedOption = selectedType,
                                onOptionSelected = onTypeSelected,
                            )
                        }
                    }
                    item {
                        Column {
                            Text(
                                stringResource(R.string.filter_transaction_category),
                                modifier = Modifier.padding(top = 24.dp),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            TransactionCategoriesCheckboxGrid(
                                modifier = Modifier.padding(top = 16.dp),
                                items = TransactionCategory.entries.toList(),
                                selectedItems = selectedCategory,
                                onSelectionChange = {
                                    if (it in selectedCategory) selectedCategory.remove(it) else selectedCategory.add(
                                        it
                                    )
                                }
                            )
                        }
                    }
                    item {
                        Column {
                            Text(
                                stringResource(R.string.filter_transaction_value_range),
                                modifier = Modifier.padding(top = 24.dp),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            RangeSlider(
                                modifier = Modifier.padding(top = 16.dp),
                                value = selectedRange.value,
                                onValueChange = {
                                    selectedRange.value = it
                                    minText.value =
                                        numberFormat.format(it.start.roundToTwoDecimals())
                                    maxText.value =
                                        numberFormat.format(it.endInclusive.roundToTwoDecimals())
                                },
                                valueRange = startValue..endValue,
                                steps = 0,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                LumenTextField(
                                    modifier = Modifier
                                        .weight(1F),
                                    value = minText,
                                    placeHolder = { Text("Min") },
                                    prefix = viewModel.getPrefixByCurrency(),
                                    keyboardType = KeyboardType.Number,
                                    currencyLocale = viewModel.getLocaleByCurrency()
                                )
                                LumenTextField(
                                    modifier = Modifier
                                        .weight(1F),
                                    value = maxText,
                                    placeHolder = { Text("Max") },
                                    prefix = viewModel.getPrefixByCurrency(),
                                    keyboardType = KeyboardType.Number,
                                    currencyLocale = viewModel.getLocaleByCurrency()
                                )
                            }
                        }
                    }

                    item {
                        Column {
                            Text(
                                stringResource(R.string.filter_transaction_initial_date),
                                modifier = Modifier.padding(top = 24.dp),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Row(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Button(
                                    onClick = {
                                        isInitialDatePickerDialogOpened = true
                                    }
                                ) {
                                    Text(stringResource(R.string.add_transactions_transaction_select_date_label))
                                }
                                if (initialTimestamp.longValue > 0) {
                                    Text(
                                        stringResource(
                                            R.string.add_transactions_transaction_selected_date_label,
                                            Date(initialTimestamp.longValue).formatDate(
                                                viewModel.getLocaleByLanguage()
                                            )
                                        ),
                                        modifier = Modifier
                                            .padding(start = 16.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Column(
                            Modifier.padding(vertical = 24.dp)
                        ) {
                            Text(
                                stringResource(R.string.filter_transaction_final_date),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Row(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Button(
                                    onClick = {
                                        isFinalDatePickerDialogOpened = true
                                    }
                                ) {
                                    Text(stringResource(R.string.add_transactions_transaction_select_date_label))
                                }
                                if (finalTimestamp.longValue > 0) {
                                    Text(
                                        stringResource(
                                            R.string.add_transactions_transaction_selected_date_label,
                                            Date(finalTimestamp.longValue).formatDate(
                                                viewModel.getLocaleByLanguage()
                                            )
                                        ),
                                        modifier = Modifier
                                            .padding(start = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
    }
}