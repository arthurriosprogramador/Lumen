package com.arthurriosribeiro.lumen.screens.home.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.MoneyOff
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import com.arthurriosribeiro.lumen.model.TransactionCategory
import com.arthurriosribeiro.lumen.model.TransactionType
import com.arthurriosribeiro.lumen.model.UserTransaction
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import com.arthurriosribeiro.lumen.utils.clearToMonthStart
import com.arthurriosribeiro.lumen.utils.formatDoubleAsCurrency
import com.arthurriosribeiro.lumen.utils.normalizeCategoryLabel
import com.arthurriosribeiro.lumen.utils.orDash
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun FinanceTrackTabScreen(viewModel: MainViewModel) {

    val expensesLabel = stringResource(R.string.finance_tracker_expenses)
    val incomesLabel = stringResource(R.string.finance_tracker_incomes)

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

    val pieData = remember(
        selectedTransactionFilterOption,
        selectedTransactionFilterByTimeOption,
        transactions
    ) {
        getChartData(
            incomesLabel,
            expensesLabel,
            transactionFilterOptions,
            selectedTransactionFilterOption,
            transactionFilterByTime,
            selectedTransactionFilterByTimeOption,
            transactions
        )
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
                                    key(pieData) {
                                        labelSelected = ""
                                        DonutPieChart(
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .background(MaterialTheme.colorScheme.background)
                                                .padding(top = 16.dp)
                                                .height(400.dp),
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
                                                labelSelected = if (labelSelected.isEmpty() || labelSelected != it.label) it.label
                                                else ""

                                            }
                                        )
                                        LazyVerticalGrid(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(if (pieData.size > 1) 50.dp * (pieData.size / 2) else 50.dp),
                                            columns = GridCells.Fixed(2),
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
                                                        text = it.label.normalizeCategoryLabel(),
                                                        modifier = Modifier
                                                            .align(Alignment.CenterVertically)
                                                            .padding(start = 8.dp),
                                                        style = if (isLabelSelected) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        item {
                            val currentMonthTransactions = transactions?.filterByPeriod(
                                transactionFilterByTime,
                                selectedTransactionFilterByTimeOption
                            )
                            val expenses = currentMonthTransactions
                                ?.filter { it.type == TransactionType.EXPENSES.name }
                                ?.sumOf { it.value ?: 0.0 }
                            val incomes = currentMonthTransactions
                                ?.filter { it.type == TransactionType.INCOME.name }
                                ?.sumOf { it.value ?: 0.0 }
                            val balance = incomes?.minus(expenses ?: 0.0)
                            val isNegative = (balance ?: 0.0) < 0
                            Column(
                                modifier = Modifier
                                    .padding(top = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    stringResource(
                                        R.string.finance_tracker_summary,
                                        selectedTransactionFilterByTimeOption),
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                        .padding(start = 24.dp),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 24.dp)
                                        .padding(top = 16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1F),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.MoneyOff,
                                            contentDescription = stringResource(R.string.finance_tracker_expenses)
                                        )
                                        Text(
                                            expenses
                                                ?.formatDoubleAsCurrency(
                                                    viewModel.getLocaleByCurrency(),
                                                    viewModel.getPrefixByCurrency()
                                                ).orDash(),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    Column(
                                        modifier = Modifier
                                            .weight(1F),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AttachMoney,
                                            contentDescription = stringResource(R.string.finance_tracker_incomes)
                                        )
                                        Text(
                                            incomes
                                                ?.formatDoubleAsCurrency(
                                                    viewModel.getLocaleByCurrency(),
                                                    viewModel.getPrefixByCurrency()
                                                ).orDash(),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    Column(
                                        modifier = Modifier
                                            .weight(1F),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AccountBalanceWallet,
                                            contentDescription = stringResource(R.string.finance_tracker_expenses)
                                        )
                                        Text(
                                            "${if (isNegative) "- " else ""}${balance?.formatDoubleAsCurrency(
                                                viewModel.getLocaleByCurrency(),  
                                                viewModel.getPrefixByCurrency()
                                        ).orDash()}",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                color = if (isNegative) Color.Red else Color.Green
                                            )
                                        )
                                    }
                                }
                                HorizontalDivider(
                                    thickness = 2.dp
                                )
                            }
                        }
                        items(items = transactions
                            ?.filterByPeriod(
                                transactionFilterByTime,
                                selectedTransactionFilterByTimeOption)
                            ?.groupBy { Pair(it.categoryName, it.type) }
                            ?.entries
                            ?.toList()
                            .orEmpty()) {
                            val (categoryName, type) = it.key
                            val category = TransactionCategory.valueOf(categoryName ?: TransactionCategory.OTHER_EXPENSE.name)
                            val isExpense = type == TransactionType.EXPENSES.name
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                        .padding(top = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .border(
                                                width = 1.dp,
                                                shape = RoundedCornerShape(8.dp),
                                                brush = Brush.linearGradient(
                                                    listOf(
                                                        MaterialTheme.colorScheme.onSurface,
                                                        MaterialTheme.colorScheme.onSurface
                                                    )
                                                )
                                            )
                                            .shadow(
                                                8.dp,
                                                RoundedCornerShape(8.dp),
                                                clip = false
                                            )
                                            .background(
                                                MaterialTheme.colorScheme.surface,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(16.dp)
                                    ) {
                                        Icon(
                                            modifier = Modifier
                                                .size(48.dp),
                                            imageVector = category.icon,
                                            contentDescription = stringResource(category.label)
                                        )
                                    }
                                    Text(
                                        stringResource(category.label),
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier
                                    )
                                    Text(
                                        "${
                                            if (isExpense) "- "
                                            else ""
                                        }${
                                            it.value.sumOf { item -> item.value ?: 0.0 }
                                                .formatDoubleAsCurrency(
                                                    viewModel.getLocaleByCurrency(),
                                                    viewModel.getPrefixByCurrency()
                                                ).orDash()
                                        }",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = if (isExpense) Color.Red else MaterialTheme.colorScheme.onBackground
                                        ),
                                        modifier = Modifier
                                    )
                                }
                                HorizontalDivider(
                                    modifier = Modifier
                                        .padding(horizontal = 24.dp)
                                        .padding(top = 8.dp),
                                    thickness = 2.dp
                                )
                            }
                        }
                    }
                }
            }

            if (isLoading) LumenCircularProgressIndicator()
        }
    }
}


private fun getChartData(
    incomeLabel: String,
    expenseLabel: String,
    filterOptions: List<String>,
    selectedFilterOption: String,
    filterByTimeOptions: List<String>,
    selectedFilterByTime: String,
    transactions: List<UserTransaction>?
) : List<PieChartData.Slice> {
    var filteredList = listOf<UserTransaction>()

    if (!transactions.isNullOrEmpty()) filteredList = transactions.filterByPeriod(filterByTimeOptions, selectedFilterByTime)

    val entries = if (selectedFilterOption == filterOptions.first()) {
        val expensesSum = filteredList
            .filter { it.type == TransactionType.EXPENSES.name }
            .sumOf { it.value ?: 0.0}

        val incomeSum = filteredList
            .filter { it.type == TransactionType.INCOME.name }
            .sumOf { it.value ?: 0.0}

        listOfNotNull(
            if (expensesSum > 0) PieChartData.Slice(expenseLabel, expensesSum.toFloat(), Color.Red) else null,
            if (incomeSum > 0) PieChartData.Slice(incomeLabel, incomeSum.toFloat(), Color.Green) else null
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

private fun List<UserTransaction>.filterByPeriod(
    filterByTimeOptions: List<String>,
    selectedFilterByTime: String,
) : List<UserTransaction> {
    return when (selectedFilterByTime) {
        filterByTimeOptions.first() -> this.filter {
            val now = Calendar.getInstance().clearToMonthStart()

            val transactionDate = Calendar.getInstance().apply {
                timeInMillis = it.timestamp ?: 0L
            }.clearToMonthStart()

            transactionDate.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                    transactionDate.get(Calendar.YEAR) == now.get(Calendar.YEAR)
        }

        filterByTimeOptions[1] -> this.filter {
            val lastMonth = Calendar.getInstance().apply {
                add(Calendar.MONTH, -1)
            }.clearToMonthStart()

            val transactionDate = Calendar.getInstance().apply {
                timeInMillis = it.timestamp ?: 0L
            }.clearToMonthStart()

            transactionDate.get(Calendar.MONTH) == lastMonth.get(Calendar.MONTH) &&
                    transactionDate.get(Calendar.YEAR) == lastMonth.get(Calendar.YEAR)
        }

        filterByTimeOptions[2] -> this.filter {
            val threeMonthsAgo = Calendar.getInstance().apply {
                add(Calendar.MONTH, -3)
            }.clearToMonthStart()

            val transactionDate = Calendar.getInstance().apply {
                timeInMillis = it.timestamp ?: 0L
            }.clearToMonthStart()

            transactionDate.after(threeMonthsAgo)
        }

        filterByTimeOptions[3] -> this.filter {
            val sixMonthsAgo = Calendar.getInstance().apply {
                add(Calendar.MONTH, -6)
            }.clearToMonthStart()

            val transactionDate = Calendar.getInstance().apply {
                timeInMillis = it.timestamp ?: 0L
            }.clearToMonthStart()

            transactionDate.after(sixMonthsAgo)
        }

        filterByTimeOptions[4] -> this.filter {
            val twelveMonthsAgo = Calendar.getInstance().apply {
                add(Calendar.YEAR, -1)
            }.clearToMonthStart()

            val transactionDate = Calendar.getInstance().apply {
                timeInMillis = it.timestamp ?: 0L
            }.clearToMonthStart()

            transactionDate.after(twelveMonthsAgo)
        }
        else -> this
    }
}