package com.arthurriosribeiro.lumen.utils

import android.icu.text.DateFormat
import android.icu.text.NumberFormat
import com.arthurriosribeiro.lumen.model.TransactionType
import com.arthurriosribeiro.lumen.model.UserTransaction
import com.google.firebase.firestore.DocumentSnapshot
import java.text.DecimalFormatSymbols
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Long.toSystemZoneMillis(): Long {
    val utcCalendar = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
    utcCalendar.timeInMillis = this

    val localCalendar = Calendar.getInstance()
    localCalendar.set(
        utcCalendar.get(Calendar.YEAR),
        utcCalendar.get(Calendar.MONTH),
        utcCalendar.get(Calendar.DAY_OF_MONTH),
        12, 0, 0
    )
    localCalendar.set(Calendar.MILLISECOND, 0)

    return localCalendar.timeInMillis
}

fun Date.formatDate(locale: Locale = Locale.US) : String {
    val formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, locale)
    return formatter.format(this)
}

fun List<DocumentSnapshot>.convertFirestoreDocumentToUserTransactionList() : List<UserTransaction> {
    val transactions = mutableListOf<UserTransaction>()

    this.forEach {
        val transaction = UserTransaction(
            uniqueId = it.getString(FirestoreCollectionUtils.TRANSACTIONS_UNIQUE_ID).orEmpty(),
            title = it.getString(FirestoreCollectionUtils.TRANSACTION_TITLE),
            description = it.getString(FirestoreCollectionUtils.TRANSACTION_DESCRIPTION),
            value = it.getDouble(FirestoreCollectionUtils.TRANSACTION_VALUE),
            timestamp = it.getLong(FirestoreCollectionUtils.TRANSACTION_TIMESTAMP),
            type = it.getString(FirestoreCollectionUtils.TRANSACTION_TYPE) ?: TransactionType.EXPENSES.name,
            categoryName = it.getString(FirestoreCollectionUtils.TRANSACTION_CATEGORY_NAME)
        )

        transactions.add(transaction)
    }

    return transactions
}

fun String?.orDash() : String {
    return if (this.isNullOrBlank()) "-" else this
}

fun Double.formatDoubleAsCurrency(locale: Locale, prefix: String) : String {
    return  "$prefix ${NumberFormatProvider.getNumberFormat(locale).format(this)}"
}

fun Float.roundToTwoDecimals(): Float =
    toBigDecimal().setScale(2, java.math.RoundingMode.HALF_UP).toFloat()

object NumberFormatProvider {
    fun getNumberFormat(locale: Locale): NumberFormat = NumberFormat.getNumberInstance(locale).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
        isGroupingUsed = true
    }

    fun getDecimalSeparator(locale: Locale) = DecimalFormatSymbols.getInstance(locale).decimalSeparator
}

fun Calendar.clearToMonthStart(): Calendar {
    set(Calendar.DAY_OF_MONTH, 1)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    return this
}

fun String.normalizeCategoryLabel() = this.lowercase().replaceFirstChar { char -> char.titlecase() }.replace("_", " ")