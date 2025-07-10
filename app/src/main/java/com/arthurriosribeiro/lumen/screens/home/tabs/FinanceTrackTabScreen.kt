package com.arthurriosribeiro.lumen.screens.home.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.components.LumenCircularProgressIndicator
import com.arthurriosribeiro.lumen.components.LumenDropdownMenu
import com.arthurriosribeiro.lumen.components.LumenSnackbarHost
import com.arthurriosribeiro.lumen.components.SnackbarType
import com.arthurriosribeiro.lumen.model.RequestState
import com.arthurriosribeiro.lumen.model.UserTransaction
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun FinanceTrackTabScreen(viewModel: MainViewModel) {

    val coroutineScope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }
    val snackbarType = remember { mutableStateOf<SnackbarType?>(null) }

    val transactionsState by viewModel.transactions.collectAsState()

    var isLoading by remember {
        mutableStateOf(false)
    }

    var transactions by rememberSaveable {
        mutableStateOf<List<UserTransaction>?>(null)
    }

    val transactionFilterOptions = listOf(
        stringResource(R.string.finance_tracker_filter_type),
        stringResource(R.string.finance_tracker_filter_Category)
    )

    val transactionFilterByTime = listOf(
        stringResource(R.string.finance_tracker_filter_current_month),
        stringResource(R.string.finance_tracker_filter_last_month),
        stringResource(R.string.finance_tracker_filter_last_three_months),
        stringResource(R.string.finance_tracker_filter_last_six_months),
        stringResource(R.string.finance_tracker_filter_last_twelve_months),
        stringResource(R.string.finance_tracker_filter_all_transactions)
    )

    var isTransactionFilterExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var selectedTransactionFilterOption by rememberSaveable {
        mutableStateOf(transactionFilterOptions.first())
    }

    var isTransactionFilterByTimeExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var selectedTransactionFilterByTimeOption by rememberSaveable {
        mutableStateOf(transactionFilterByTime.first())
    }

    Scaffold(
        snackbarHost = {
            LumenSnackbarHost(snackBarHostState, snackbarType)
        }
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
                        transactions = state.data
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

            if (transactionsState is RequestState.Success) {
                if (transactions.isNullOrEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(R.string.home_empty_info_message),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        item {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1.5F),
                                shape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 0.dp,
                                    bottomStart = 24.dp,
                                    bottomEnd = 24.dp),
                                elevation = CardDefaults.elevatedCardElevation(
                                    defaultElevation = 8.dp
                                ),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        LumenDropdownMenu(
                                            modifier = Modifier
                                                .weight(1F)
                                                .padding(end = 24.dp),
                                            menuOptions = transactionFilterOptions,
                                            isExpanded = isTransactionFilterExpanded,
                                            onIsExpandedChanged = {
                                                isTransactionFilterExpanded = !isTransactionFilterExpanded
                                            },
                                            selectedOption = selectedTransactionFilterOption,
                                            onOptionSelected = { selectedTransactionFilterOption = it },
                                            onDismissRequest = { isTransactionFilterExpanded = false }
                                        )
                                        LumenDropdownMenu(
                                            modifier = Modifier
                                                .weight(1F),
                                            menuOptions = transactionFilterByTime,
                                            isExpanded = isTransactionFilterByTimeExpanded,
                                            onIsExpandedChanged = {
                                                isTransactionFilterByTimeExpanded = !isTransactionFilterByTimeExpanded
                                            },
                                            selectedOption = selectedTransactionFilterByTimeOption,
                                            onOptionSelected = { selectedTransactionFilterByTimeOption = it },
                                            onDismissRequest = { isTransactionFilterByTimeExpanded = false }
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