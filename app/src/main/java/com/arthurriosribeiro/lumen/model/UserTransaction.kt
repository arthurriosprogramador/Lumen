package com.arthurriosribeiro.lumen.model

import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.MoneyOff
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.RequestPage
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import javax.annotation.Nonnull


@Parcelize
@Entity(tableName = "user_transactions")
data class UserTransaction(
    @Nonnull
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val firebaseId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val value: Double? = null,
    val timestamp: Long? = null,
    val type: String,
    val categoryName: String? = null,
) : Parcelable

enum class TransactionType {
    EXPENSES,
    INCOME
}

@Parcelize
data class TransactionCategory(
    val name: String,
    val icon: TransactionCategoryIcons
) : Parcelable

enum class TransactionCategoryIcons(val icon: ImageVector, val label: String) {
    CREDIT_CARD(Icons.Rounded.Payment, "Credit card"),
    FOOD(Icons.Rounded.Fastfood, "Food"),
    CLOTHING(Icons.Rounded.Checkroom, "Clothing"),
    SHOPPING(Icons.Rounded.ShoppingCart, "Shopping"),
    BILLS(Icons.Rounded.RequestPage, "Bills"),
    BEAUTY(Icons.Rounded.Spa, "Beauty and self care"),
    PAYCHECK(Icons.Rounded.AttachMoney, "Paycheck"),
    OTHER_EXPENSE(Icons.Rounded.MoneyOff, "Other expenses"),
    OTHER_INCOME(Icons.Rounded.AttachMoney, "Other Incomes")
}
