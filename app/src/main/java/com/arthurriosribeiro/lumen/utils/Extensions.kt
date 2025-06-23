package com.arthurriosribeiro.lumen.utils

import android.icu.text.DateFormat
import java.util.Date
import java.util.Locale

fun Date.formatDate(locale: Locale = Locale.US) : String {
    val formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, locale)
    return formatter.format(this)
}