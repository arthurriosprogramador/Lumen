package com.arthurriosribeiro.lumen.components

import android.icu.text.NumberFormat
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import java.lang.Double
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Unit

@Composable
fun LumenTextField(
    modifier: Modifier,
    value: MutableState<String> = mutableStateOf(""),
    valueString: String? = null,
    placeHolder: @Composable () -> Unit,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null,
    isSingleLine: Boolean = true,
    minLines: Int = 1,
    prefix: String = "",
    currencyLocale: Locale = Locale.US
) {

    val decimalSeparator = remember(currencyLocale) {
        DecimalFormatSymbols.getInstance(currencyLocale).decimalSeparator
    }

    TextField(
        modifier = modifier,
        value = valueString ?: value.value,
        onValueChange = {
            if (keyboardType == KeyboardType.Number || keyboardType == KeyboardType.Decimal) {
                value.value = when {
                    it.isEmpty() ->  it
                    it == "." ->  "0."
                    it.count { char -> char == decimalSeparator  } <= 1 && it.matches(Regex("^\\d*\\.?\\d*$")) ->  it
                    else -> value.value
                }
            } else {
                value.value = it
            }
        },
        placeholder = placeHolder,
        trailingIcon = trailingIcon,
        prefix = { Text(prefix, fontWeight = FontWeight.Bold) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary
        ),
        visualTransformation = visualTransformation,
        isError = isError,
        supportingText = supportingText,
        maxLines = Int.MAX_VALUE,
        singleLine = isSingleLine,
        minLines = minLines
    )
}