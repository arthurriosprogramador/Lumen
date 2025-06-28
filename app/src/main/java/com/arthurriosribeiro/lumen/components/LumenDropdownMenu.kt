package com.arthurriosribeiro.lumen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.model.TransactionCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> LumenDropdownMenu(
    modifier: Modifier,
    menuOptions: List<T>,
    isExpanded: Boolean,
    onIsExpandedChanged: (Boolean) -> Unit,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    onDismissRequest: () -> Unit
) {
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180F else 0F)

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = isExpanded,
        onExpandedChange = onIsExpandedChanged
    ) {
        LumenTextField(
            modifier = Modifier
                .menuAnchor(
                    MenuAnchorType.PrimaryNotEditable,
                    true
                ),
            valueString = if (selectedOption is TransactionCategory) stringResource(selectedOption.label)
            else selectedOption as String,
            trailingIcon = {
                Icon(
                    modifier = Modifier
                        .rotate(rotationAngle),
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = if (isExpanded) stringResource(R.string.add_transactions_close_category_menu)
                    else stringResource(R.string.add_transactions_open_category_menu)
                )
            },
            placeHolder = {}
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = onDismissRequest
        ) {
            menuOptions.forEach { option ->
                DropdownMenuItem(
                    text = {
                        if (option is TransactionCategory) Text(stringResource(option.label))
                        else Text(option as String)
                    },
                    onClick = {
                        onOptionSelected(option)
                        onDismissRequest.invoke()
                    }
                )
            }
        }
    }
}