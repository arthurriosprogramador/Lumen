package com.arthurriosribeiro.lumen.screens.home.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SettingsInputComponent
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.components.LumenCircularProgressIndicator
import com.arthurriosribeiro.lumen.components.LumenInfoRow
import com.arthurriosribeiro.lumen.components.LumenSnackbarHost
import com.arthurriosribeiro.lumen.components.SnackbarType
import com.arthurriosribeiro.lumen.model.RequestState
import com.arthurriosribeiro.lumen.model.TransactionType
import com.arthurriosribeiro.lumen.model.UserTransaction
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import com.arthurriosribeiro.lumen.utils.formatDate
import com.arthurriosribeiro.lumen.utils.formatDoubleAsCurrency
import com.arthurriosribeiro.lumen.utils.orDash
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun TransactionsTabScreen(viewModel: MainViewModel) {

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

            if (transactionsState is RequestState.Success) {
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
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        IconButton(
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(end = 24.dp),
                            onClick = {}
                        ) {
                            Icon(
                                Icons.Rounded.SettingsInputComponent,
                                stringResource(R.string.filter_icon_description))
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp)
                        ) {
                            items(items = transactions!!) {
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
                                                it.type.lowercase().replaceFirstChar {
                                                    it.titlecase()
                                                },
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onSecondary,
                                                ),
                                                modifier = Modifier.padding(5.dp)
                                            )
                                        }
                                        LumenInfoRow(
                                            modifier = Modifier
                                                .padding(top = 16.dp),
                                            label = stringResource(R.string.transactions_date_label),
                                            infoText = Date(
                                                it.timestamp ?: 0
                                            ).formatDate(viewModel.getLocaleByLanguage()),
                                            isDividerToggled = true
                                        )
                                        LumenInfoRow(
                                            modifier = Modifier
                                                .padding(top = 5.dp),
                                            label = stringResource(R.string.transactions_category_label),
                                            infoText = it.categoryName?.lowercase()
                                                ?.replaceFirstChar {
                                                    it.titlecase()
                                                }?.replace("_", " ").orDash(),
                                            isDividerToggled = true
                                        )
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