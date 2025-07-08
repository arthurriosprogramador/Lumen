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

    @Query("""
        UPDATE user_transactions SET
            uid = :uid,
            title = :title,
            description = :description,
            value = :value,
            timestamp = :timestamp,
            type = :type,
            categoryName = :categoryName,
            isSyncedWithFirebase = :isSyncedWithFirebase
        WHERE uniqueId = :uniqueId
    """)
    suspend fun updateTransaction(
        uniqueId: String,
        uid: String,
        title: String,
        description: String,
        value: Double,
        timestamp: Long,
        type: String,
        categoryName: String,
        isSyncedWithFirebase: Boolean
    )

    @Query("UPDATE user_transactions SET isSyncedWithFirebase = :isSyncedWithFirebase WHERE uniqueId = :uniqueId")
    suspend fun updateIsSyncedWithFirebase(isSyncedWithFirebase: Boolean, uniqueId: String)

    @Query("DELETE FROM user_transactions")
    suspend fun deleteAllUserTransactions()

    @Query("DELETE FROM user_transactions WHERE uniqueId = :uniqueId")
    suspend fun deleteUserTransaction(uniqueId: String)
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

    @Query("DELETE FROM account_configuration")
    suspend fun deleteAllAccountConfiguration()
    //End of the region
}