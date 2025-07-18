package com.arthurriosribeiro.lumen.screens.home.tabs

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.SettingsInputComponent
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.components.LumenBottomSheet
import com.arthurriosribeiro.lumen.components.LumenCircularProgressIndicator
import com.arthurriosribeiro.lumen.components.LumenInfoRow
import com.arthurriosribeiro.lumen.components.LumenSnackbarHost
import com.arthurriosribeiro.lumen.components.LumenTextField
import com.arthurriosribeiro.lumen.components.SnackbarType
import com.arthurriosribeiro.lumen.model.RequestState
import com.arthurriosribeiro.lumen.model.TransactionCategory
import com.arthurriosribeiro.lumen.model.TransactionType
import com.arthurriosribeiro.lumen.model.UserTransaction
import com.arthurriosribeiro.lumen.navigation.LumenScreens
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import com.arthurriosribeiro.lumen.utils.NetworkUtils
import com.arthurriosribeiro.lumen.utils.formatDate
import com.arthurriosribeiro.lumen.utils.formatDoubleAsCurrency
import com.arthurriosribeiro.lumen.utils.orDash
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsTabScreen(navController: NavController, viewModel: MainViewModel) {

    val transactionSucessMessage = stringResource(R.string.transactions_delete_success_message)

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val networkMonitor = remember { NetworkUtils(context) }
    val isConnected by networkMonitor.isConnected.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }
    val snackbarType = remember { mutableStateOf<SnackbarType?>(null) }

    val transactionsState by viewModel.transactions.collectAsState()
    val deleteTransactionState by viewModel.deleteTransaction.collectAsState()

    var transactions by rememberSaveable {
        mutableStateOf<List<UserTransaction>?>(null)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var descriptionOverflowMap by rememberSaveable {
        mutableStateOf(mapOf<String, Boolean>())
    }

    var isDescriptionBottomSheetOpened by rememberSaveable {
        mutableStateOf(false)
    }

    val bottomSheetState = rememberModalBottomSheetState()
    var bottomSheetText by rememberSaveable {
        mutableStateOf("")
    }

    val searchQuery = rememberSaveable { mutableStateOf("") }

    val minValue by remember(transactions) {
        mutableStateOf(transactions?.minByOrNull { it.value ?: 0.0 }?.value?.toFloat())
    }
    val maxValue by remember(transactions) {
        mutableStateOf(transactions?.maxByOrNull { it.value ?: 0.0 }?.value?.toFloat())
    }
    val minDate by remember(transactions) {
        mutableStateOf(transactions?.minByOrNull { it.timestamp ?: 0L }?.timestamp)
    }
    val maxDate by remember(transactions) {
        mutableStateOf(transactions?.maxByOrNull { it.timestamp ?: 0L }?.timestamp)
    }

    val selectedFilterValue = viewModel.selectedFilter.collectAsState().value

    val transactionFiltered = remember(transactions, searchQuery.value, selectedFilterValue) {
        transactions?.filter {
            val matchesSearch = it.title?.lowercase()?.contains(searchQuery.value.lowercase(), ignoreCase = false) == true
                    || it.description?.lowercase()?.contains(searchQuery.value.lowercase(), ignoreCase = false) == true

            val matchesType = selectedFilterValue?.transactionType == null ||
                    (if (selectedFilterValue.transactionType == TransactionType.ALL)
                        TransactionType.valueOf(it.type) == TransactionType.INCOME || TransactionType.valueOf(it.type) == TransactionType.EXPENSES
            else TransactionType.valueOf(it.type) == selectedFilterValue.transactionType)

            val matchesValueRange = selectedFilterValue?.valueRange == null || (it.value ?: 0.0) in selectedFilterValue.valueRange

            val matchesDateRange = selectedFilterValue?.timestampRange == null || (it.timestamp ?: 0L) in selectedFilterValue.timestampRange

            val matchesCategory = selectedFilterValue?.transactionCategory.isNullOrEmpty() || it.categoryName?.let {  selectedFilterValue?.transactionCategory?.any { category -> category == TransactionCategory.valueOf(it) }  } == true

            matchesSearch && matchesType && matchesValueRange && matchesDateRange && matchesCategory
        }
    }

    Scaffold(
        snackbarHost = {
            LumenSnackbarHost(snackBarHostState, snackbarType)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LaunchedEffect(transactionsState) {
                when (val state = transactionsState) {
                    is RequestState.Loading -> isLoading = true
                    is RequestState.Success -> {
                        isLoading = false
                        transactions = state.data.sortedBy { it.timestamp }
                    }

                    is RequestState.Error -> {
                        isLoading = false
                        snackbarType.value = SnackbarType.ERROR
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(
                                message = state.message
                            )
                        }
                    }

                    else -> {}
                }
            }

            LaunchedEffect(deleteTransactionState) {
                when (val state = deleteTransactionState) {
                    is RequestState.Loading -> isLoading = true
                    is RequestState.Success -> {
                        isLoading = false
                        snackbarType.value = SnackbarType.SUCCESS
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(
                                message = transactionSucessMessage
                            )
                        }
                        if (viewModel.accountConfig.value?.isUserLoggedIn == true && isConnected) viewModel.getAllTransactionsFromFirestore(context)
                        else viewModel.getAllTransactionsFromSql(context)
                        viewModel.clearDeleteTransactionState()
                    }
                    is RequestState.Error -> {
                        isLoading = false
                        snackbarType.value = SnackbarType.ERROR
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(
                                message = state.message
                            )
                        }
                        viewModel.clearDeleteTransactionState()
                    }
                    else -> {}
                }
            }

            if (isDescriptionBottomSheetOpened) {
                LumenBottomSheet(
                    onDismissRequest = { isDescriptionBottomSheetOpened = false },
                    sheetState = bottomSheetState,
                    title = stringResource(R.string.transactions_description_label),
                    content = {
                        Text(
                            bottomSheetText,
                            modifier = Modifier.padding(top = 16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
            }

            if (transactionsState is RequestState.Success) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LumenTextField(
                            modifier = Modifier,
                            value = searchQuery,
                            placeHolder = {
                                Text(stringResource(R.string.transactions_search_label))
                            },
                            shape = RoundedCornerShape(24.dp),
                            isIndicatorVisible = false,
                            trailingIcon =  {
                                if (searchQuery.value.isNotEmpty()) IconButton(
                                    onClick = {
                                        searchQuery.value = ""
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = stringResource(R.string.delete_searched_term_icon_description)
                                    )
                                }
                            }
                        )
                        IconButton(
                            onClick = {
                                navController.navigate(
                                    "${LumenScreens.FILTER_SCREEN.name}/$minValue/$maxValue/$minDate/$maxDate")
                            }
                        ) {
                            Icon(
                                Icons.Rounded.SettingsInputComponent,
                                stringResource(R.string.filter_icon_description)
                            )
                        }
                    }
                    if (selectedFilterValue != null) {
                        Box(
                            modifier = Modifier
                                .padding(top = 16.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 24.dp)
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.linearGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.secondary
                                            )
                                        ),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.clearFilter()
                                        }
                                ) {
                                    Text(stringResource(R.string.filter_transaction_filters_applied))
                                    Icon(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.CenterVertically),
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = stringResource(R.string.filter_transaction_clear_filter)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .offset(x = 6.dp, y = (-8).dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondary,
                                        shape = CircleShape,
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    viewModel.countFilter().toString(),
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                    if (transactions.isNullOrEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                stringResource(R.string.transactions_empty_list),
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    } else if (transactionFiltered.isNullOrEmpty() && selectedFilterValue != null) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                stringResource(R.string.transactions_filtered_empty_list),
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp)
                        ) {
                            val listToShow =
                                if (searchQuery.value.isBlank() && selectedFilterValue == null) transactions else transactionFiltered

                            items(items = listToShow ?: emptyList()) { it ->
                                val isDescriptionOverflowed =
                                    descriptionOverflowMap[it.uniqueId] == true
                                ElevatedCard(
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                        .fillMaxWidth(),
                                    elevation = CardDefaults.elevatedCardElevation(
                                        defaultElevation = 8.dp
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(24.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(
                                                    it.title.orEmpty(),
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Surface(
                                                    modifier = Modifier.align(Alignment.Start),
                                                    color = if (it.type == TransactionType.INCOME.name) MaterialTheme.colorScheme.secondary
                                                    else MaterialTheme.colorScheme.error,
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Text(
                                                        it.type.lowercase().replaceFirstChar { char ->
                                                            char.titlecase()
                                                        },
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = MaterialTheme.colorScheme.onSecondary,
                                                        ),
                                                        modifier = Modifier.padding(5.dp)
                                                    )
                                                }
                                            }
                                            it.categoryName?.let { category ->
                                                Surface(
                                                    shape = RoundedCornerShape(24.dp),
                                                    shadowElevation = 5.dp
                                                ) {
                                                    Icon(
                                                        modifier = Modifier.padding(8.dp),
                                                        imageVector = TransactionCategory.valueOf(
                                                            category
                                                        ).icon,
                                                        contentDescription = ""
                                                    )
                                                }
                                            }
                                        }
                                        if (!it.description.isNullOrEmpty()) {
                                            Text(
                                                stringResource(R.string.transactions_description_label),
                                                modifier = Modifier.padding(top = 16.dp),
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                it.description,
                                                style = MaterialTheme.typography.bodyLarge,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                onTextLayout = { textLayoutResult ->
                                                    descriptionOverflowMap =
                                                        descriptionOverflowMap.toMutableMap()
                                                            .apply {
                                                                this[it.uniqueId] =
                                                                    textLayoutResult.hasVisualOverflow
                                                            }
                                                }
                                            )
                                            if (isDescriptionOverflowed) {
                                                TextButton(
                                                    modifier = Modifier.align(Alignment.Start),
                                                    contentPadding = PaddingValues(0.dp),
                                                    onClick = {
                                                        isDescriptionBottomSheetOpened = true
                                                        bottomSheetText = it.description
                                                        coroutineScope.launch {
                                                            bottomSheetState.show()
                                                        }
                                                    }
                                                ) {
                                                    Row {
                                                        Text(
                                                            stringResource(R.string.transactions_description_see_more_button_label)
                                                        )
                                                        Icon(
                                                            imageVector = Icons.Rounded.ChevronRight,
                                                            contentDescription = ""
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        LumenInfoRow(
                                            modifier = Modifier
                                                .padding(top = 12.dp),
                                            label = stringResource(R.string.transactions_date_label),
                                            infoText = Date(
                                                it.timestamp ?: 0
                                            ).formatDate(viewModel.getLocaleByLanguage()),
                                            isDividerToggled = true
                                        )
                                        it.categoryName?.let { category ->
                                            LumenInfoRow(
                                                modifier = Modifier
                                                    .padding(top = 5.dp),
                                                label = stringResource(R.string.transactions_category_label),
                                                infoText = stringResource(TransactionCategory.valueOf(category).label),
                                                isDividerToggled = true
                                            )
                                        }
                                        LumenInfoRow(
                                            modifier = Modifier
                                                .padding(top = 5.dp),
                                            label = stringResource(R.string.transactions_value_label),
                                            infoText = it.value?.formatDoubleAsCurrency(
                                                viewModel.getLocaleByCurrency(),
                                                viewModel.getPrefixByCurrency()
                                            ).orDash(),
                                            isDividerToggled = false
                                        )
                                        Row {
                                            TextButton(
                                                onClick = {
                                                    val transactionToAdd = UserTransaction(
                                                        uniqueId = it.uniqueId,
                                                        title = it.title,
                                                        description = it.description,
                                                        value = it.value,
                                                        timestamp = it.timestamp,
                                                        type = it.type,
                                                        categoryName = it.categoryName
                                                    )
                                                    navController.navigate(
                                                        "${LumenScreens.ADD_TRANSACTIONS_SCREEN}/" +
                                                                "${
                                                                    URLEncoder.encode(
                                                                        Json.encodeToString(
                                                                            transactionToAdd
                                                                        ), "UTF-8"
                                                                    )
                                                                }/"
                                                                + true
                                                    )
                                                },
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Row {
                                                    Text(stringResource(R.string.transactions_edit_label))
                                                    Icon(
                                                        modifier = Modifier
                                                            .size(20.dp)
                                                            .align(Alignment.CenterVertically),
                                                        imageVector = Icons.Rounded.Edit,
                                                        contentDescription = stringResource(R.string.transactions_edit_label)
                                                    )
                                                }
                                            }
                                            TextButton(
                                                modifier = Modifier
                                                    .padding(start = 16.dp),
                                                onClick = {
                                                    if (viewModel.accountConfig.value?.isUserLoggedIn == true && isConnected) {
                                                        viewModel.deleteTransactionFromFirestore(it.uniqueId, context)
                                                    } else {
                                                        viewModel.deleteTransactionFromSql(it.uniqueId, context)
                                                    }
                                                },
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Row {
                                                    Text(

                                                        stringResource(R.string.transactions_delete),
                                                        color = MaterialTheme.colorScheme.error
                                                    )
                                                    Icon(
                                                        modifier = Modifier
                                                            .size(20.dp)
                                                            .align(Alignment.CenterVertically),
                                                        imageVector = Icons.Rounded.Delete,
                                                        contentDescription = stringResource(R.string.transactions_delete),
                                                        tint = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isLoading) LumenCircularProgressIndicator()
        }
    }
}
