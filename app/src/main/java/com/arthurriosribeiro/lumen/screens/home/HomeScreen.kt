package com.arthurriosribeiro.lumen.screens.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.arthurriosribeiro.lumen.components.LumenBottomNavigationBar
import com.arthurriosribeiro.lumen.navigation.LumenScreens
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import com.arthurriosribeiro.lumen.screens.home.tabs.AddTransactionTabScreen
import com.arthurriosribeiro.lumen.screens.home.tabs.FinanceTrackTabScreen
import com.arthurriosribeiro.lumen.screens.home.tabs.UserConfigurationScreen
import com.arthurriosribeiro.lumen.screens.viewmodel.AuthViewModel
import androidx.lifecycle.compose.currentStateAsState
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel, authViewModel: AuthViewModel) {
    val bottomNavigationItems = listOf(
        LumenScreens.FINANCE_SCREEN,
        LumenScreens.ADD_TRANSACTION_SCREEN,
        LumenScreens.USER_CONFIGURATION_SCREEN
    )
    val pagerState = rememberPagerState(
        pageCount = { bottomNavigationItems.size }
    )

    val coroutineScope = rememberCoroutineScope()

    val navBackStackEntry by remember(navController) {
        derivedStateOf { navController.getBackStackEntry(LumenScreens.HOME_SCREEN.name) }
    }


    val lifecycle = navBackStackEntry.lifecycle

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

    Scaffold(
        modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
        bottomBar = { LumenBottomNavigationBar(navController, bottomNavigationItems, pagerState) }) { innerPadding ->
        HorizontalPager(
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            beyondViewportPageCount = bottomNavigationItems.size,
            state = pagerState,
        ) {
            when (it) {
                0 -> FinanceTrackTabScreen(viewModel)
                1 -> AddTransactionTabScreen()
                2 -> UserConfigurationScreen(navController, viewModel, authViewModel)
            }
        }
    }
}