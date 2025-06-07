package com.arthurriosribeiro.lumen.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import javax.annotation.Nonnull

@Entity(tableName = "account_configuration")
@Parcelize
data class AccountConfiguration(
    @Nonnull
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String? = null,
    val selectedLanguage: String = Languages.EN.name,
    val selectedCurrency: String = Currencies.USD.name,
    val isUserLoggedIn: Boolean = false,
    val userImage: String? = null
) : Parcelable

enum class Languages {
    EN,
    PT,
    ES
}

enum class Currencies {
    USD,
    BRL,
    EUR
}
