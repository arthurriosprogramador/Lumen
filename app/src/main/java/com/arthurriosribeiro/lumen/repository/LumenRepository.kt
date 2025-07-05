package com.arthurriosribeiro.lumen.repository

import com.arthurriosribeiro.lumen.data.LumenDao
import com.arthurriosribeiro.lumen.model.AccountConfiguration
import com.arthurriosribeiro.lumen.model.UserTransaction
import com.arthurriosribeiro.lumen.model.TransactionType
import javax.inject.Inject

class LumenRepository @Inject constructor(private val lumenDao: LumenDao) {

    //Transaction region
    suspend fun insertTransaction(userTransaction: UserTransaction) = lumenDao.insertTransaction(userTransaction)

    suspend fun selectAllTransactions() : List<UserTransaction> = lumenDao.selectAllTransactions()

    suspend fun deleteTransaction(userTransaction: UserTransaction) = lumenDao.deleteTransaction(userTransaction)

    suspend fun updateTransaction(
        uniqueId: String,
        title: String,
        description: String,
        value: Double,
        timestamp: Long,
        type: String,
        categoryName: String,
        isSyncedWithFirebase: Boolean) = lumenDao.updateTransaction(
        uniqueId,
        title,
        description,
        value,
        timestamp,
        type,
        categoryName,
        isSyncedWithFirebase
        )

    suspend fun updateIsSyncedWithFirebase(isSyncedWithFirebase: Boolean, uniqueId: String) = lumenDao.updateIsSyncedWithFirebase(isSyncedWithFirebase, uniqueId)

    suspend fun deleteAllTransactions() = lumenDao.deleteAllUserTransactions()
    //End of the region

    //Account configuration user
    suspend fun insertAccountConfiguration(accountConfiguration: AccountConfiguration) = lumenDao.insertAccountConfiguration(accountConfiguration)

    suspend fun updateUserImage(userImage: String, id: Int) = lumenDao.updateUserImage(userImage, id)

    suspend fun updateUserName(name: String, id: Int) = lumenDao.updateUserName(name, id)

    suspend fun updateUserLoggedIn(isUserLoggedIn: Boolean, id: Int) = lumenDao.updateUserLoggedIn(isUserLoggedIn, id)

    suspend fun updateUserLanguage(language: String, id: Int) = lumenDao.updateUserLanguage(language, id)

    suspend fun updateUserCurrency(currency: String, id: Int) = lumenDao.updateUserCurrency(currency, id)

    suspend fun selectAccountConfiguration() : AccountConfiguration? = lumenDao.selectAccountConfiguration()

    suspend fun deleteAllAccountConfiguration() = lumenDao.deleteAllAccountConfiguration()
    //End of the region
}