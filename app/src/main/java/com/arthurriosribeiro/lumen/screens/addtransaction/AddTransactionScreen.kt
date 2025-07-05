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
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.components.LumenCircularProgressIndicator
import com.arthurriosribeiro.lumen.components.LumenDropdownMenu
import com.arthurriosribeiro.lumen.components.LumenRadioButton
import com.arthurriosribeiro.lumen.components.LumenSnackbarHost
import com.arthurriosribeiro.lumen.components.LumenTextField
import com.arthurriosribeiro.lumen.components.LumenTopAppBar
import com.arthurriosribeiro.lumen.components.SnackbarType
import com.arthurriosribeiro.lumen.model.RequestState
import com.arthurriosribeiro.lumen.model.TransactionCategory
import com.arthurriosribeiro.lumen.model.TransactionType
import com.arthurriosribeiro.lumen.model.UserTransaction
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import com.arthurriosribeiro.lumen.utils.NetworkUtils
import com.arthurriosribeiro.lumen.utils.NumberFormatProvider
import com.arthurriosribeiro.lumen.utils.formatDate
import com.arthurriosribeiro.lumen.utils.toSystemZoneMillis
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionsScreen(
    navController: NavController,
    viewModel: MainViewModel,
    isEditScreen: Boolean = false,
    userTransaction: UserTransaction? = null
) {
    val lostConnectionMessage = stringResource(R.string.lost_connection_message)
    val transactionSuccessfullyAddedMessage = stringResource(R.string.add_transactions_transaction_successfully_added)

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    val networkMonitor = remember { NetworkUtils(context) }
    val isConnected by networkMonitor.isConnected.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }
    val snackbarType = remember { mutableStateOf<SnackbarType?>(null) }

    val scrollState = rememberScrollState()

    val numberFormat = NumberFormatProvider.getNumberFormat(viewModel.getLocaleByLanguage())

    val transaction = rememberSaveable {
        mutableStateOf(userTransaction?.title ?: "")
    }
    val value = rememberSaveable {
        mutableStateOf((userTransaction?.value ?: 0).toString())
    }
    val timestamp = rememberSaveable {
        mutableLongStateOf(userTransaction?.timestamp ?: 0L)
    }
    val description = rememberSaveable {
        mutableStateOf(userTransaction?.description?.replace("+", " ") ?: "")
    }
    val (selectedTransactionType, onSelectTransactionType) = rememberSaveable {
        mutableStateOf(userTransaction?.type?.let { TransactionType.valueOf(it) } ?: TransactionType.EXPENSES)
        }

    val datePickerState = rememberDatePickerState()

    var isDatePickerDialogOpened by rememberSaveable {
        mutableStateOf(false)
    }

    var isCategoryDropdownMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var selectedCategoryDropdownMenuOption by rememberSaveable {
        mutableStateOf(userTransaction?.categoryName?.let { TransactionCategory.valueOf(it) }
            ?: TransactionCategory.OTHER_EXPENSE)
    }

    val isError = remember {
        mutableStateOf<List<AddTransactionError>>(listOf())
    }

    val addTransactionState by viewModel.addTransactionState.collectAsState()

    var isLoading by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        viewModel.clearAddTransactionState()
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
        },
        snackbarHost = {
            LumenSnackbarHost(snackBarHostState, snackbarType)
        }
    ) { innerPadding ->
        Box {

            LaunchedEffect(isConnected) {
                if (viewModel.accountConfig.value?.isUserLoggedIn == true && !isConnected) {
                    snackbarType.value = SnackbarType.ERROR
                    snackBarHostState.showSnackbar(
                        message = lostConnectionMessage
                    )
                }
            }

            if (isDatePickerDialogOpened) {
                DatePickerDialog(
                    onDismissRequest = { isDatePickerDialogOpened = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    timestamp.longValue = it.toSystemZoneMillis()
                                }
                                isDatePickerDialogOpened = false
                            }
                        ) {
                            Text(stringResource(R.string.confirm_button_label))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { isDatePickerDialogOpened = false }
                        ) {
                            Text(stringResource(R.string.cancel_button_label))
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
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
                    isError = isError.value.hasTransactionError(AddTransactionError.TRANSACTION_ERROR),
                    supportingText = {
                        Text(
                            if (isError.value.hasTransactionError(AddTransactionError.TRANSACTION_ERROR)) stringResource(
                                R.string.add_transactions_empty_field_error
                            )
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
                    prefix = viewModel.getPrefixByCurrency(),
                    currencyLocale = viewModel.getLocaleByCurrency(),
                    isError = isError.value.hasTransactionError(AddTransactionError.VALUE_ERROR),
                    supportingText = {
                        Text(
                            if (isError.value.hasTransactionError(AddTransactionError.VALUE_ERROR)) stringResource(
                                R.string.add_transactions_empty_field_error
                            )
                            else ""
                        )
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = {
                            isDatePickerDialogOpened = true
                        }
                    ) {
                        Text(stringResource(R.string.add_transactions_transaction_select_date_label))
                    }
                    if (timestamp.longValue > 0) {
                        Text(
                            stringResource(
                                R.string.add_transactions_transaction_selected_date_label,
                                Date(timestamp.longValue).formatDate(
                                    viewModel.getLocaleByLanguage()
                                )
                            ),
                            modifier = Modifier
                                .padding(start = 16.dp)
                        )
                    }

                    if (isError.value.hasTransactionError(AddTransactionError.TIMESTAMP_ERROR)) {
                        Text(
                            stringResource(
                                R.string.add_transactions_empty_field_error,
                            ),
                            modifier = Modifier
                                .padding(start = 16.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
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
                    options = TransactionType.entries.toList().dropLast(1),
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
                        menuOptions = TransactionCategory.entries.toList().dropLast(1),
                        isExpanded = isCategoryDropdownMenuExpanded,
                        onIsExpandedChanged = {
                            isCategoryDropdownMenuExpanded = !isCategoryDropdownMenuExpanded
                        },
                        selectedOption = selectedCategoryDropdownMenuOption,
                        onOptionSelected = { selectedCategoryDropdownMenuOption = it },
                        onDismissRequest = { isCategoryDropdownMenuExpanded = false }
                    )
                    Icon(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        imageVector = selectedCategoryDropdownMenuOption.icon,
                        contentDescription = stringResource(selectedCategoryDropdownMenuOption.label)
                    )
                }
                ElevatedButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 36.dp),
                    onClick = {
                        verifyAddTransactionError(transaction, value, timestamp, isError)

                        val doubleValue = NumberFormat.getInstance(
                            viewModel.getLocaleByCurrency()
                        )
                            .parse(value.value)
                            ?.toDouble()

                        val transactionToAdd = UserTransaction(
                            uniqueId = userTransaction?.uniqueId ?: UUID.randomUUID().toString(),
                            title = transaction.value,
                            description = description.value,
                            value = doubleValue,
                            timestamp = timestamp.longValue,
                            type = selectedTransactionType.name,
                            categoryName = selectedCategoryDropdownMenuOption.name
                        )

                        if (isError.value.isEmpty() && isConnected) {
                            if (isEditScreen) {
                                coroutineScope.launch {
                                    viewModel.editTransactionOnFirestore(
                                        transactionToAdd.copy(
                                            isSyncedWithFirebase = true
                                        ),
                                        context
                                    )
                                }
                            } else {
                                viewModel.addTransactionOnFirestore(
                                    transactionToAdd.copy(
                                        isSyncedWithFirebase = true
                                    ),
                                    context
                                )
                            }
                        } else {
                            if (isEditScreen) {
                                coroutineScope.launch {
                                    viewModel.editTransactionOnSql(
                                        transactionToAdd.copy(
                                            isSyncedWithFirebase = false
                                        ),
                                        context
                                    )
                                }
                            } else {
                                coroutineScope.launch {
                                    viewModel.addTransactionToSql(
                                        transactionToAdd.copy(
                                        isSyncedWithFirebase = false
                                    ),
                                        context
                                    )
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        stringResource(R.string.save_label),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }

        LaunchedEffect(addTransactionState) {
            when (viewModel.addTransactionState.value) {
                is RequestState.Loading -> isLoading = true
                is RequestState.Success -> {
                    isLoading = false
                    snackbarType.value = SnackbarType.SUCCESS
                    coroutineScope.launch {
                        val result = snackBarHostState.showSnackbar(
                            message = transactionSuccessfullyAddedMessage,
                            actionLabel = "OK",
                        )

                        if (result == SnackbarResult.Dismissed || result == SnackbarResult.ActionPerformed) {
                            navController.popBackStack()
                        }
                    }
                }
                is RequestState.Error -> {
                    isLoading = false
                    snackbarType.value = SnackbarType.ERROR
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = (addTransactionState as RequestState.Error).message
                        )
                    }
                }
                else -> {}
            }
        }

        if (isLoading) LumenCircularProgressIndicator()
    }
}

private enum class AddTransactionError {
    TRANSACTION_ERROR,
    VALUE_ERROR,
    TIMESTAMP_ERROR;
}

private fun List<AddTransactionError>.hasTransactionError(error: AddTransactionError) =
    this@hasTransactionError.any { it == error }

private fun verifyAddTransactionError(
    transaction: MutableState<String>,
    value: MutableState<String>,
    timestamp: MutableLongState,
    isError: MutableState<List<AddTransactionError>>
) {

    val errors = mutableListOf<AddTransactionError>()

    if (transaction.value.isBlank()) {
        errors.add(AddTransactionError.TRANSACTION_ERROR)
    } else if (value.value.isBlank()) {
        errors.add(AddTransactionError.VALUE_ERROR)
    } else if (timestamp.longValue <= 0) {
        errors.add(AddTransactionError.TIMESTAMP_ERROR)
    }

    isError.value = errors
}