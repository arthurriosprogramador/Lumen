package com.arthurriosribeiro.lumen.model

import java.util.Date

data class TransactionFilter(
    val timestampRange: LongRange? = null,
    val valueRange: ClosedFloatingPointRange<Double>? = null,
    val transactionType: TransactionType? = null,
    val transactionCategory: List<TransactionCategory>? = null
)