package com.arthurriosribeiro.lumen.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

@Composable
fun LumenSnackbarHost(snackbarHostState: SnackbarHostState, snackbarTypeState: State<SnackbarType?>) {
    val snackbarType = snackbarTypeState.value ?: SnackbarType.MESSAGE

    SnackbarHost(hostState = snackbarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = when (snackbarType) {
                SnackbarType.ERROR -> MaterialTheme.colorScheme.error
                SnackbarType.SUCCESS -> MaterialTheme.colorScheme.secondary
                SnackbarType.MESSAGE -> MaterialTheme.colorScheme.background
            },
            contentColor = when (snackbarType) {
                SnackbarType.ERROR -> MaterialTheme.colorScheme.onError
                SnackbarType.SUCCESS -> MaterialTheme.colorScheme.onSecondary
                SnackbarType.MESSAGE -> MaterialTheme.colorScheme.onBackground
            }
        )
    }
}

enum class SnackbarType {
    SUCCESS,
    ERROR,
    MESSAGE
}