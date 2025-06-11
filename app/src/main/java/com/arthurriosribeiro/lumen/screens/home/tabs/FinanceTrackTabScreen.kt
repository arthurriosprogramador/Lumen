package com.arthurriosribeiro.lumen.screens.home.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel

@Composable
fun FinanceTrackTabScreen(viewModel: MainViewModel) {
    Column(
        Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.home_empty_info_message),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(24.dp)
        )
        Text(
            viewModel.accountConfig.value?.name.orEmpty(),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(24.dp)
        )
        Text(
            viewModel.accountConfig.value?.selectedLanguage.orEmpty(),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(24.dp)
        )
        Text(
            viewModel.accountConfig.value?.selectedCurrency.orEmpty(),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(24.dp)
        )
    }
}