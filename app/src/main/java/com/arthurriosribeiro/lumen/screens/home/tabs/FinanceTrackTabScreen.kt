package com.arthurriosribeiro.lumen.screens.home.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.components.LumenCircularProgressIndicator
import com.arthurriosribeiro.lumen.components.LumenDropdownMenu
import com.arthurriosribeiro.lumen.components.LumenSnackbarHost
import com.arthurriosribeiro.lumen.components.SnackbarType
import com.arthurriosribeiro.lumen.model.RequestState
import com.arthurriosribeiro.lumen.model.TransactionType
import com.arthurriosribeiro.lumen.model.UserTransaction
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import com.arthurriosribeiro.lumen.utils.clearToMonthStart
import kotlinx.coroutines.launch
import java.util.Calendar

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

    var labelSelected by rememberSaveable {
        mutableStateOf("")
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
                                    .aspectRatio(0.6F),
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
                                        modifier = Modifier.fillMaxWidth(),
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
                                    val pieData = getChartData(
                                        transactionFilterOptions,
                                        selectedTransactionFilterOption,
                                        transactionFilterByTime,
                                        selectedTransactionFilterByTimeOption,
                                        transactions
                                    )

                                    DonutPieChart(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .background(MaterialTheme.colorScheme.background)
                                            .padding(top = 16.dp),
                                        pieChartData = PieChartData(
                                            slices = pieData,
                                            plotType = PlotType.Donut
                                        ),
                                        pieChartConfig = PieChartConfig(
                                            strokeWidth = 80f,
                                            activeSliceAlpha = 0.9f,
                                            isAnimationEnable = true,
                                            labelVisible = true,
                                            labelType = PieChartConfig.LabelType.PERCENTAGE,
                                            labelColor = MaterialTheme.colorScheme.onSurface,
                                            labelFontSize = 16.sp,
                                            showSliceLabels = true,
                                            sliceLabelTextSize = 16.sp,
                                            sliceLabelTextColor = MaterialTheme.colorScheme.onSurface,
                                            backgroundColor = Color.Transparent,
                                        ),
                                        onSliceClick = {
                                            labelSelected = it.label
                                        }
                                    )
                                    LazyHorizontalGrid(
                                        rows = GridCells.Fixed(6),
                                    ) {
                                        items(items = pieData) {
                                            val isLabelSelected = labelSelected == it.label
                                            Row(
                                                modifier = Modifier.padding(top = 8.dp, start = 16.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(if (isLabelSelected) 40.dp else 30.dp)
                                                        .background(it.color)
                                                        .align(Alignment.CenterVertically)
                                                )
                                                Text(
                                                    text = it.label.lowercase().replaceFirstChar { char -> char.titlecase() }.replace("_", " "),
                                                    modifier = Modifier
                                                        .align(Alignment.CenterVertically)
                                                        .padding(start = 8.dp),
                                                    style = if (isLabelSelected) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyLarge,
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

            if (isLoading) LumenCircularProgressIndicator()
        }
    }
}

@Composable
private fun getChartData(
    filterOptions: List<String>,
    selectedFilterOption: String,
    filterByTimeOptions: List<String>,
    selectedFilterByTime: String,
    transactions: List<UserTransaction>?
) : List<PieChartData.Slice> {
    var filteredList = listOf<UserTransaction>()

    if (!transactions.isNullOrEmpty()) {
        filteredList = when (selectedFilterByTime) {
            filterByTimeOptions.first() -> transactions.filter {
                val now = Calendar.getInstance().clearToMonthStart()

                val transactionDate = Calendar.getInstance().apply {
                    timeInMillis = it.timestamp ?: 0L
                }.clearToMonthStart()

                transactionDate.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                        transactionDate.get(Calendar.YEAR) == now.get(Calendar.YEAR)
            }

            filterByTimeOptions[1] -> transactions.filter {
                val lastMonth = Calendar.getInstance().apply {
                    add(Calendar.MONTH, -1)
                }.clearToMonthStart()

                val transactionDate = Calendar.getInstance().apply {
                    timeInMillis = it.timestamp ?: 0L
                }.clearToMonthStart()

                transactionDate.get(Calendar.MONTH) == lastMonth.get(Calendar.MONTH) &&
                        transactionDate.get(Calendar.YEAR) == lastMonth.get(Calendar.YEAR)
            }

            filterByTimeOptions[2] -> transactions.filter {
                val threeMonthsAgo = Calendar.getInstance().apply {
                    add(Calendar.MONTH, -3)
                }.clearToMonthStart()

                val transactionDate = Calendar.getInstance().apply {
                    timeInMillis = it.timestamp ?: 0L
                }.clearToMonthStart()

                transactionDate.after(threeMonthsAgo)
            }

            filterByTimeOptions[3] -> transactions.filter {
                val sixMonthsAgo = Calendar.getInstance().apply {
                    add(Calendar.MONTH, -6)
                }.clearToMonthStart()

                val transactionDate = Calendar.getInstance().apply {
                    timeInMillis = it.timestamp ?: 0L
                }.clearToMonthStart()

                transactionDate.after(sixMonthsAgo)
            }

            filterByTimeOptions[4] -> transactions.filter {
                val twelveMonthsAgo = Calendar.getInstance().apply {
                    add(Calendar.YEAR, -1)
                }.clearToMonthStart()

                val transactionDate = Calendar.getInstance().apply {
                    timeInMillis = it.timestamp ?: 0L
                }.clearToMonthStart()

                transactionDate.after(twelveMonthsAgo)
            }
            else -> transactions
        }
    }

    val entries = if (selectedFilterOption == filterOptions.first()) {
        val expensesSum = filteredList
            .filter { it.type == TransactionType.EXPENSES.name }
            .sumOf { it.value ?: 0.0}

        val incomeSum = filteredList
            .filter { it.type == TransactionType.INCOME.name }
            .sumOf { it.value ?: 0.0}

        listOfNotNull(
            if (expensesSum > 0) PieChartData.Slice(stringResource(R.string.finance_tracker_expenses), expensesSum.toFloat(), Color.Red) else null,
            if (incomeSum > 0) PieChartData.Slice(stringResource(R.string.finance_tracker_incomes), incomeSum.toFloat(), Color.Green) else null
        )
    } else {
        val categoriesColor = listOf(
            Color(0xFF4285F4),
            Color(0xFFEA4335),
            Color(0xFFFBBC05),
            Color(0xFF34A853),
            Color(0xFF673AB7),
            Color(0xFFFF9800),
            Color(0xFFE91E63),
            Color(0xFF00BCD4),
            Color(0xFF8BC34A),
            Color(0xFF795548),
            Color(0xFF9E9E9E)
        )
        filteredList
            .groupBy { it.categoryName }
            .entries
            .mapIndexed { index, (category, transactionsInCategory) ->
                val total = transactionsInCategory.sumOf {
                    it.value ?: 0.0
                }
                PieChartData.Slice(category.orEmpty(), total.toBigDecimal().toFloat(), categoriesColor[index])
            }
    }

    return entries
}