package com.arthurriosribeiro.lumen.screens.addtransaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.components.LumenDropdownMenu
import com.arthurriosribeiro.lumen.components.LumenRadioButton
import com.arthurriosribeiro.lumen.components.LumenTextField
import com.arthurriosribeiro.lumen.components.LumenTopAppBar
import com.arthurriosribeiro.lumen.model.Currencies
import com.arthurriosribeiro.lumen.model.TransactionCategory
import com.arthurriosribeiro.lumen.model.TransactionType
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel

@Composable
fun AddTransactionsScreen(
    navController: NavController,
    viewModel: MainViewModel
) {

    val scrollState = rememberScrollState()

    val transaction = rememberSaveable {
        mutableStateOf("")
    }
    val description = rememberSaveable {
        mutableStateOf("")
    }
    val value = rememberSaveable {
        mutableStateOf("")
    }
    val (selectedTransactionType, onSelectTransactionType) = rememberSaveable {
        mutableStateOf(TransactionType.EXPENSES)
    }

    var isDropdownMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var selectedDropdownMenuOption by rememberSaveable {
        mutableStateOf(TransactionCategory.OTHER_EXPENSE)
    }

    var isTransactionError by remember {
        mutableStateOf(false)
    }
    var isValueError by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = Modifier
            .imePadding(),
        topBar = {
            LumenTopAppBar(
                title = stringResource(R.string.add_transactions_title),
                actions = {
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
                }
            )
        }
    ) { innerPadding ->
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState)
            ) {
                LumenTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    value = transaction,
                    placeHolder = {
                        Text(stringResource(R.string.add_transactions_transaction_label))
                    },
                    isError = isTransactionError,
                    supportingText = {
                        Text(
                            if (isTransactionError) stringResource(R.string.add_transactions_empty_field_error)
                            else ""
                        )
                    }
                )
                LumenTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    value = value,
                    placeHolder = { Text(stringResource(R.string.add_transactions_transaction_value_label)) },
                    keyboardType = KeyboardType.Number,
                    prefix = viewModel.getPrefixByCurrency(
                        viewModel.accountConfig.value?.selectedCurrency ?: ""
                    ),
                    currencyLocale = viewModel.getLocaleByCurrency(
                        viewModel.accountConfig.value?.selectedCurrency ?: Currencies.USD.name
                    ),
                    isError = isValueError,
                    supportingText = {
                        Text(
                            if (isValueError) stringResource(R.string.add_transactions_empty_field_error)
                            else ""
                        )
                    }
                )
                LumenTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    value = description,
                    placeHolder = {
                        Text(stringResource(R.string.add_transactions_transaction_description))
                    },
                    isSingleLine = false,
                    minLines = 5
                )
                LumenRadioButton(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    options = TransactionType.entries.toList(),
                    selectedOption = selectedTransactionType,
                    onOptionSelected = onSelectTransactionType,
                    isColumn = false
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    LumenDropdownMenu(
                        modifier = Modifier
                            .padding(top = 16.dp),
                        menuOptions = TransactionCategory.entries.toList(),
                        isExpanded = isDropdownMenuExpanded,
                        onIsExpandedChanged = { isDropdownMenuExpanded = !isDropdownMenuExpanded },
                        selectedOption = selectedDropdownMenuOption,
                        onOptionSelected = { selectedDropdownMenuOption = it },
                        onDismissRequest = { isDropdownMenuExpanded = false }
                    )
                    Icon(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        imageVector = selectedDropdownMenuOption.icon,
                        contentDescription = stringResource(selectedDropdownMenuOption.label)
                    )
                }
                ElevatedButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 36.dp),
                    onClick = {
                        if (transaction.value.isBlank()) {
                            isTransactionError = true
                        } else if (value.value.isBlank()) {
                            isValueError = true
                        } else {
                            isTransactionError = false
                            isValueError = false
                        }
                    }
                ) {
                    Text(
                        stringResource(R.string.save_label),
                        modifier = Modifier.padding(horizontal = 24.dp))
                }
            }
        }
    }
}