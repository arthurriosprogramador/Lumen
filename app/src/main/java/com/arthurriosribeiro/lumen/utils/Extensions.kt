package com.arthurriosribeiro.lumen.utils

import android.icu.text.DateFormat
import android.icu.text.NumberFormat
import com.arthurriosribeiro.lumen.model.TransactionType
import com.arthurriosribeiro.lumen.model.UserTransaction
import com.google.firebase.firestore.DocumentSnapshot
import java.text.ParseException
import java.util.Date
import java.util.Locale

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

fun Double.formatDoubleAsCurrency(locale: Locale, prefix: String) : String{
    val numberFormat = NumberFormat.getNumberInstance(locale).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
        isGroupingUsed = true
    }

    return  "$prefix ${numberFormat.format(this)}"
}