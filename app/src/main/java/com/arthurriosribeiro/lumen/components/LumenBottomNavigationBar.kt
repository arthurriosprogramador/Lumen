package com.arthurriosribeiro.lumen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.arthurriosribeiro.lumen.navigation.LumenScreens
import kotlinx.coroutines.launch

@Composable
fun LumenBottomNavigationBar(
    navController: NavController,
    tabItems: List<LumenScreens>,
    pagerState: PagerState
) {
    val coroutineScope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = pagerState.pageCount
    ) {
        tabItems.forEachIndexed { index, lumenScreen ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }) {
                lumenScreen.icon?.let {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .background(
                                color = if (pagerState.currentPage == index)
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.15F)
                                else Color.Transparent,
                                shape = CircleShape
                            )
                            .size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.padding(vertical = 16.dp).size(32.dp),
                            imageVector = it,
                            contentDescription = lumenScreen.name
                        )
                    }
                }
            }
        }
    }
}