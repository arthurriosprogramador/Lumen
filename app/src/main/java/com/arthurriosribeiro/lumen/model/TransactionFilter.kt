package com.arthurriosribeiro.lumen.model

import java.util.Date

data class TransactionFilter(
    val dateRange: Pair<Date, Date>? = null,
    val valueRange: Pair<Double, Double>? = null,
    val transactionType: TransactionType? = null,
    val transactionCategory: List<TransactionCategory>? = null
)