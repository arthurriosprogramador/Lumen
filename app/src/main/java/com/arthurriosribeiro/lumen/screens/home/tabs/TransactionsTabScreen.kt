package com.arthurriosribeiro.lumen.screens.home.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.components.LumenCircularProgressIndicator
import com.arthurriosribeiro.lumen.components.LumenSnackbarHost
import com.arthurriosribeiro.lumen.components.SnackbarType
import com.arthurriosribeiro.lumen.model.RequestState
import com.arthurriosribeiro.lumen.model.UserTransaction
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import com.arthurriosribeiro.lumen.utils.NetworkUtils
import kotlinx.coroutines.launch

@Composable
fun TransactionsTabScreen(viewModel: MainViewModel) {
    val lostConnectionMessage = stringResource(R.string.lost_connection_message)

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    val networkMonitor = remember { NetworkUtils(context) }
    val isConnected by networkMonitor.isConnected.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }
    val snackbarType = remember { mutableStateOf<SnackbarType?>(null) }

    val transactionsState by viewModel.transactions.collectAsState()

    var transactions by rememberSaveable {
        mutableStateOf<List<UserTransaction>?>(null)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }


    LaunchedEffect(isConnected) {
        if (viewModel.accountConfig.value?.isUserLoggedIn == true && !isConnected) {
            snackbarType.value = SnackbarType.ERROR
            snackBarHostState.showSnackbar(
                message = lostConnectionMessage
            )
        }
    }

    LaunchedEffect(Unit) {
        if (viewModel.accountConfig.value?.isUserLoggedIn == false || !isConnected) {
            viewModel.getAllTransactionsFromSql(context)
        } else {
            viewModel.getAllTransactionsFromFirestore(context)
        }
    }

    Scaffold(
        snackbarHost = {
            LumenSnackbarHost(snackBarHostState, snackbarType)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
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
                    LazyColumn {
                        items(items = transactions!!) {
                            Text("Item ${it.title}")
                        }
                    }
                }
            }

            if (isLoading) LumenCircularProgressIndicator()
        }
    }

}