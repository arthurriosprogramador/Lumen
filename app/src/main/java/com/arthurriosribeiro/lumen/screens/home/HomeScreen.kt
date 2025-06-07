package com.arthurriosribeiro.lumen.screens.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.arthurriosribeiro.lumen.components.LumenBottomNavigationBar
import com.arthurriosribeiro.lumen.navigation.LumenScreens
import com.arthurriosribeiro.lumen.screens.MainViewModel
import com.arthurriosribeiro.lumen.screens.home.tabs.AddTransactionTabScreen
import com.arthurriosribeiro.lumen.screens.home.tabs.FinanceTrackTabScreen
import com.arthurriosribeiro.lumen.screens.home.tabs.UserConfigurationScreen

@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel) {
    val bottomNavigationItems = listOf(
        LumenScreens.FINANCE_SCREEN,
        LumenScreens.ADD_TRANSACTION_SCREEN,
        LumenScreens.USER_CONFIGURATION_SCREEN
    )
    val pagerState = rememberPagerState(
        pageCount = { bottomNavigationItems.size }
    )

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
                2 -> UserConfigurationScreen(navController, viewModel)
            }
        }
    }
}