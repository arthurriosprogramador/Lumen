package com.arthurriosribeiro.lumen.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.arthurriosribeiro.lumen.model.AccountConfiguration
import com.arthurriosribeiro.lumen.model.UserTransaction

@Dao
interface LumenDao {
    //Transaction region
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(userTransaction: UserTransaction)

    @Query("SELECT * FROM user_transactions")
    suspend fun selectAllTransactions() : List<UserTransaction>

    @Query("SELECT * FROM user_transactions WHERE type = :type")
    suspend fun selectTransactionByType(type: String) : List<UserTransaction>

    @Query("SELECT * FROM user_transactions WHERE timestamp >= :selectedTimestamp")
    suspend fun selectTransactionByTimestamp(selectedTimestamp: Long) : List<UserTransaction>

    @Query("SELECT * FROM user_transactions WHERE timestamp >= :initialTimestamp AND timestamp <= :finalTimestamp")
    suspend fun selectTransactionByTimestampRange(initialTimestamp: Long, finalTimestamp: Long) : List<UserTransaction>

    @Delete
    suspend fun deleteTransaction(userTransaction: UserTransaction)
    //End of the region

    //Account configuration user
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccountConfiguration(accountConfiguration: AccountConfiguration)

    @Query("UPDATE account_configuration SET userImage = :imageUri WHERE id = :id")
    suspend fun updateUserImage(imageUri: String, id: Int)

    @Query("UPDATE account_configuration SET name = :name WHERE id = :id")
    suspend fun updateUserName(name: String, id: Int)

    @Query("UPDATE account_configuration SET isUserLoggedIn = :isUserLoggedIn WHERE id = :id")
    suspend fun updateUserLoggedIn(isUserLoggedIn: Boolean, id: Int)

    @Query("UPDATE account_configuration SET selectedLanguage = :language WHERE id = :id")
    suspend fun updateUserLanguage(language: String, id: Int)

    @Query("UPDATE account_configuration SET selectedCurrency = :currency WHERE id = :id")
    suspend fun updateUserCurrency(currency: String, id: Int)

    @Query("SELECT * FROM account_configuration LIMIT 1")
    suspend fun selectAccountConfiguration() : AccountConfiguration?
    //End of the region
}