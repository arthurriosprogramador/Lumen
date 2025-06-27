package com.arthurriosribeiro.lumen.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arthurriosribeiro.lumen.model.AccountConfiguration
import com.arthurriosribeiro.lumen.model.UserTransaction

@Database(entities = [UserTransaction::class, AccountConfiguration::class], version = 2, exportSchema = false)
abstract class LumenDatabase : RoomDatabase() {
    abstract fun lumenDao(): LumenDao
}