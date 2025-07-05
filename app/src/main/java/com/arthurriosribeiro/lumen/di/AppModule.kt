package com.arthurriosribeiro.lumen.di

import android.content.Context
import androidx.room.Room
import com.arthurriosribeiro.lumen.data.LumenDao
import com.arthurriosribeiro.lumen.data.LumenDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideLumenDao(database: LumenDatabase): LumenDao = database.lumenDao()

    @Singleton
    @Provides
    fun provideLumenDatabase(@ApplicationContext context: Context): LumenDatabase =
        Room.databaseBuilder(context, LumenDatabase::class.java, "lumen_database")
            .fallbackToDestructiveMigration(true).build()

    @Singleton
    @Provides
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()
}