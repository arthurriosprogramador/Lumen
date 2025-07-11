package com.arthurriosribeiro.lumen.model

sealed class RequestState<out T> {
    data object SignedOut : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Success<out T>(val data: T) : RequestState<T>()
    data class Error(val message: String) : RequestState<Nothing>()
}