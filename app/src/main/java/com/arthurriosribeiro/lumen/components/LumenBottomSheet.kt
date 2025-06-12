package com.arthurriosribeiro.lumen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arthurriosribeiro.lumen.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LumenBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    title: String,
    content: @Composable ColumnScope.() -> Unit,
    isEditBottomSheet: Boolean = false,
    onEditButtonClick: (() -> Unit) = {}
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.3F)
        ) {
            IconButton(
                modifier = Modifier
                    .padding(end = 24.dp)
                    .align(Alignment.End),
                onClick = onDismissRequest) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.close_icon_description)
                )
            }
            Text(
                title,
                modifier = Modifier
                    .padding(top = 24.dp),
                style = MaterialTheme.typography.headlineSmall
            )
            Column(content = content)

            if (isEditBottomSheet) {
                ElevatedButton(
                    modifier = Modifier
                        .padding(top = 48.dp),
                    onClick = onEditButtonClick
                ) {
                    Text(stringResource(R.string.save_label))
                }
            }
        }
    }
}