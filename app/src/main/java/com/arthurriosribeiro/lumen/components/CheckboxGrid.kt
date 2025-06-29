package com.arthurriosribeiro.lumen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arthurriosribeiro.lumen.model.TransactionCategory

@Composable
fun TransactionCategoriesCheckboxGrid(
    modifier: Modifier,
    items: List<TransactionCategory>,
    selectedItems: List<TransactionCategory>,
    onSelectionChange: (TransactionCategory) -> Unit,
    columns: Int = 3
) {
    val rows = items.chunked(columns)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { rowItems ->
           Row(
               horizontalArrangement = Arrangement.spacedBy(8.dp),
               modifier = Modifier.fillMaxWidth()
           ) {
               rowItems.forEach {
                   val isSelected = it in selectedItems
                   Box(
                       modifier = Modifier
                           .clip(RoundedCornerShape(8.dp))
                           .background(
                               if (isSelected) MaterialTheme.colorScheme.secondary
                               else Color.Transparent
                           )
                           .border(
                               width = 1.dp,
                               shape = RoundedCornerShape(8.dp),
                               brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.onSurface, MaterialTheme.colorScheme.onSurface))
                           )
                           .padding(8.dp)
                           .clickable {
                               onSelectionChange(it)
                           },
                       contentAlignment = Alignment.Center
                   ) {
                       Text(
                           stringResource(it.label),
                           color = if (isSelected) MaterialTheme.colorScheme.onSecondary
                           else MaterialTheme.colorScheme.onSurface
                       )
                   }
               }

               repeat(columns - rowItems.size) {
                   Spacer(modifier = Modifier.weight(1F))
               }
           }
        }
    }
}