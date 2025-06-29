package com.arthurriosribeiro.lumen.screens.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.components.LumenRadioButton
import com.arthurriosribeiro.lumen.components.LumenTopAppBar
import com.arthurriosribeiro.lumen.components.TransactionCategoriesCheckboxGrid
import com.arthurriosribeiro.lumen.model.TransactionCategory
import com.arthurriosribeiro.lumen.model.TransactionType

@Composable
fun FilterScreen(
    navController: NavController,
    startValue: Float,
    endValue: Float,
    startDate: Long,
    endDate: Long) {

    val (selectedType, onTypeSelected) = remember {
        mutableStateOf(TransactionType.ALL)
    }
    val selectedCategory = remember {
        mutableStateListOf<TransactionCategory>()
    }

    val selectedRange = remember {
        mutableStateOf(startValue..startValue)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            LumenTopAppBar(title = stringResource(R.string.filter_screen_title), actions = {
                IconButton(
                    modifier = Modifier.padding(end = 24.dp),
                    onClick = {
                        navController.popBackStack()
                    }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.close_icon_description)
                    )
                }

            })
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            item {
                Column {
                    Text(
                        stringResource(R.string.filter_transaction_type),
                        modifier = Modifier.padding(top = 24.dp),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    LumenRadioButton(
                        modifier = Modifier,
                        options = TransactionType.entries.toList(),
                        selectedOption = selectedType,
                        onOptionSelected = onTypeSelected,
                    )
                }
            }
            item {
                Column {
                    Text(
                        stringResource(R.string.filter_transaction_category),
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    TransactionCategoriesCheckboxGrid(
                        modifier = Modifier.padding(top = 16.dp),
                        items = TransactionCategory.entries.toList(),
                        selectedItems = selectedCategory,
                        onSelectionChange = {
                            if (it in selectedCategory) selectedCategory.remove(it) else selectedCategory.add(it)
                        }
                    )
                }
            }
            item {
                Column {
                    Text(
                        stringResource(R.string.filter_transaction_value_range),
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    RangeSlider(
                        value = selectedRange.value,
                        onValueChange = { selectedRange.value = it },
                        valueRange = startValue..endValue,
                        steps = 0
                    )
                    Row {

                    }
                }
            }
        }
    }
}