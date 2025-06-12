package com.arthurriosribeiro.lumen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
fun AccountMenuSection(sectionLabel: String, sectionText: String, onClick: (() -> Unit)? = null ) {
    Column(
        modifier = Modifier.padding(top = 16.dp)
            .clickable {
                onClick?.invoke()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    sectionLabel,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    sectionText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = stringResource(R.string.chevron_right_description),
            )
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}