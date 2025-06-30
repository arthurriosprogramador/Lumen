package com.arthurriosribeiro.lumen.components

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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.arthurriosribeiro.lumen.utils.NumberFormatProvider
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.util.Locale

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
    currencyLocale: Locale = Locale.US,
    shape: Shape = TextFieldDefaults.shape,
    isIndicatorVisible: Boolean = true
) {

    val decimalSeparator = remember(currencyLocale) {
        NumberFormatProvider.getDecimalSeparator(currencyLocale)
    }

    TextField(
        modifier = modifier.onFocusChanged { focusState ->
            if (keyboardType.isKeyboardNumber()) {
                if (focusState.isFocused) {
                    value.value = value.value
                        .replace(Regex("[^\\d$decimalSeparator]"), "")
                } else {
                    try {
                        val parsedNumber = NumberFormatProvider.getNumberFormat(currencyLocale).parse(value.value)
                        if (parsedNumber != null) {
                            value.value = NumberFormatProvider.getNumberFormat(currencyLocale).format(parsedNumber)
                        }
                    } catch (_: ParseException) {}
                }
            }
        },
        value = valueString ?: value.value,
        onValueChange = {
            if (keyboardType.isKeyboardNumber()) {
                value.value = if (it.matches(Regex("^\\d*\\$decimalSeparator?\\d{0,2}$"))) it else value.value
            } else {
                value.value = it
            }
        },
        placeholder = placeHolder,
        trailingIcon = trailingIcon,
        prefix = { Text(prefix, fontWeight = FontWeight.Bold) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
            capitalization = KeyboardCapitalization.Sentences
        ),
        keyboardActions = keyboardActions,
        colors = if (isIndicatorVisible) TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary
        ) else TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        visualTransformation = visualTransformation,
        isError = isError,
        supportingText = supportingText,
        maxLines = Int.MAX_VALUE,
        singleLine = isSingleLine,
        minLines = minLines,
        shape = shape
    )
}

private fun KeyboardType.isKeyboardNumber() = this == KeyboardType.Number || this == KeyboardType.Decimal