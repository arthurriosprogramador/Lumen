package com.arthurriosribeiro.lumen.screens.home.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
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
import com.arthurriosribeiro.lumen.utils.formatDate
import com.arthurriosribeiro.lumen.utils.formatDoubleAsCurrency
import com.arthurriosribeiro.lumen.utils.orDash
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsTabScreen(navController: NavController, viewModel: MainViewModel) {

    val coroutineScope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }
    val snackbarType = remember { mutableStateOf<SnackbarType?>(null) }

    val transactionsState by viewModel.transactions.collectAsState()

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

    val transactionFiltered = remember(transactions, searchQuery.value) {
        transactions?.filter {
            it.title?.lowercase()
                ?.contains(searchQuery.value.lowercase(), ignoreCase = false) == true
                    || it.description?.lowercase()
                ?.contains(searchQuery.value.lowercase(), ignoreCase = false) == true
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
                when (viewModel.transactions.value) {
                    is RequestState.Loading -> isLoading = true
                    is RequestState.Success -> {
                        isLoading = false
                        transactions = (transactionsState as RequestState.Success).data
                    }

                    is RequestState.Error -> {
                        isLoading = false
                        snackbarType.value = SnackbarType.ERROR
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(
                                message = (transactionsState as RequestState.Error).message
                            )
                        }
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
                                val minValue = transactions?.minBy { it.value ?: 0.0 }?.value?.toFloat()
                                val maxValue = transactions?.maxBy { it.value ?: 0.0 }?.value?.toFloat()
                                val minDate = transactions?.minBy { it.timestamp ?: 0L }?.timestamp
                                val maxDate = transactions?.maxBy { it.timestamp ?: 0L }?.timestamp
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
                    if (transactions.isNullOrEmpty() || transactionFiltered.isNullOrEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                stringResource(if (transactions.isNullOrEmpty()) R.string.transactions_empty_list else R.string.transactions_filtered_empty_list),
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
                                if (searchQuery.value.isBlank()) transactions else transactionFiltered

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