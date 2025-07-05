package com.arthurriosribeiro.lumen.model

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AllInclusive
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.MoneyOff
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.RequestPage
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.VideogameAsset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arthurriosribeiro.lumen.R
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import javax.annotation.Nonnull


@Serializable
@Parcelize
@Entity(tableName = "user_transactions")
data class UserTransaction(
    @Nonnull
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uniqueId: String,
    val firebaseId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val value: Double? = null,
    val timestamp: Long? = null,
    val type: String = TransactionType.EXPENSES.name,
    val categoryName: String? = null,
    var isSyncedWithFirebase: Boolean = false
) : Parcelable

enum class TransactionType {
    EXPENSES,
    INCOME,
    ALL
}

enum class TransactionCategory(val icon: ImageVector, @StringRes val label: Int) {
    CREDIT_CARD(Icons.Rounded.Payment, R.string.credit_card_label),
    FOOD(Icons.Rounded.Fastfood, R.string.food_label),
    CLOTHING(Icons.Rounded.Checkroom, R.string.clothing_label),
    SHOPPING(Icons.Rounded.ShoppingCart, R.string.shopping_label),
    BILLS(Icons.Rounded.RequestPage, R.string.bills_label),
    BEAUTY(Icons.Rounded.Spa, R.string.beauty_and_self_care_label),
    EDUCATION(Icons.Rounded.Book, R.string.education_label),
    ENTERTAINMENT(Icons.Rounded.VideogameAsset, R.string.entertainment_label),
    PAYCHECK(Icons.Rounded.AttachMoney, R.string.paycheck_label),
    OTHER_EXPENSE(Icons.Rounded.MoneyOff, R.string.other_expenses_label),
    OTHER_INCOME(Icons.Rounded.AttachMoney, R.string.other_incomes_label)
}
