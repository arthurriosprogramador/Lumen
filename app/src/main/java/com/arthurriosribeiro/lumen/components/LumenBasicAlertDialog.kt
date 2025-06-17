package com.arthurriosribeiro.lumen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LumenBasicAlertDialog(
    onDismissRequest: () -> Unit,
    title: String,
    message: String,
    confirmButtonLabel: String,
    onClickConfirmButton: () -> Unit,
    cancelButtonLabel: String? = null,
    onClickCancelButton: (() -> Unit)? = null
) {
    BasicAlertDialog(onDismissRequest = onDismissRequest) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    title,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 24.dp),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    message,
                    modifier = Modifier
                        .padding(top = 24.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = if (!cancelButtonLabel.isNullOrBlank()) Arrangement.SpaceEvenly else Arrangement.Center
                ) {
                    ElevatedButton(
                        onClick = onClickConfirmButton
                    ) {
                        Text(confirmButtonLabel)
                    }
                    if (!cancelButtonLabel.isNullOrBlank()) onClickCancelButton?.let {
                        ElevatedButton(
                            onClick = it
                        ) {
                            Text(cancelButtonLabel)
                        }
                    }
                }
            }
        }
    }