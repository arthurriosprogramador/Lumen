package com.arthurriosribeiro.lumen.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.components.LumenBottomNavigationBar
import com.arthurriosribeiro.lumen.components.LumenSnackbarHost
import com.arthurriosribeiro.lumen.components.SnackbarType
import com.arthurriosribeiro.lumen.navigation.LumenScreens
import com.arthurriosribeiro.lumen.screens.home.tabs.FinanceTrackTabScreen
import com.arthurriosribeiro.lumen.screens.home.tabs.TransactionsTabScreen
import com.arthurriosribeiro.lumen.screens.home.tabs.UserConfigurationScreen
import com.arthurriosribeiro.lumen.screens.viewmodel.AuthViewModel
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import com.arthurriosribeiro.lumen.utils.NetworkUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel, authViewModel: AuthViewModel) {


    val lostConnectionMessage = stringResource(R.string.lost_connection_message)

    val context = LocalContext.current

    val networkMonitor = remember { NetworkUtils(context) }
    val isConnected by networkMonitor.isConnected.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }
    val snackbarType = remember { mutableStateOf<SnackbarType?>(null) }

    val bottomNavigationItems = listOf(
        LumenScreens.OVERVIEW_SCREEN,
        LumenScreens.TRANSACTIONS_SCREEN,
        LumenScreens.USER_CONFIGURATION_SCREEN
    )
    val pagerState = rememberPagerState(
        pageCount = { bottomNavigationItems.size }
    )

    val coroutineScope = rememberCoroutineScope()

    val navBackStackEntry by remember(navController) {
        derivedStateOf { navController.getBackStackEntry(LumenScreens.HOME_SCREEN.name) }
    }

    DisposableEffect(navBackStackEntry) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                coroutineScope.launch { viewModel.getAccountConfig() }
            }
        }

        navBackStackEntry.lifecycle.addObserver(observer)

        onDispose {
            navBackStackEntry.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(viewModel.firebaseUser, viewModel.accountConfig.value) {
        viewModel.updateUserLoggedIn(
            viewModel.firebaseUser != null,
            viewModel.accountConfig.value?.id ?: 0
        )
    }

    LaunchedEffect(isConnected) {
        if (viewModel.accountConfig.value?.isUserLoggedIn == true && !isConnected) {
            snackbarType.value = SnackbarType.ERROR
            snackBarHostState.showSnackbar(
                message = lostConnectionMessage
            )
        }
    }

    Scaffold(
        modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
        floatingActionButton = {
            AnimatedVisibility(
                visible = pagerState.currentPage != bottomNavigationItems.lastIndex
            ) {
                FloatingActionButton(
                    shape = CircleShape,
                    onClick = {
                        navController.navigate(
                            "${LumenScreens.ADD_TRANSACTIONS_SCREEN.name}/null/false")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.home_add_icon_description)
                    )
                }
            }
        },
        snackbarHost = {
            LumenSnackbarHost(snackBarHostState, snackbarType)
        },
        bottomBar = { LumenBottomNavigationBar(navController, bottomNavigationItems, pagerState) }) { innerPadding ->
        HorizontalPager(
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            beyondViewportPageCount = bottomNavigationItems.size,
            state = pagerState,
        ) {
            when (it) {
                0 -> FinanceTrackTabScreen(viewModel)
                1 -> TransactionsTabScreen(navController, viewModel)
                2 -> UserConfigurationScreen(navController, viewModel, authViewModel)
            }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collectLatest { page ->
                if (page != 2) {
                    if (viewModel.accountConfig.value?.isUserLoggedIn == false || !isConnected) {
                        viewModel.getAllTransactionsFromSql(context)
                    } else {
                        viewModel.getAllTransactionsFromFirestore(context)
                    }
                }
            }
    }
}