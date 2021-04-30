package com.israel.kola.di

import android.content.Context
import androidx.room.Room
import com.israel.kola.data.local.LocalDatabase
import com.israel.kola.data.local.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule{
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): LocalDatabase{
        return Room.databaseBuilder(
            appContext,
            LocalDatabase::class.java,
            "kola.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideTransactionDao(database: LocalDatabase): TransactionDao{
        return database.transactionDao()
    }
}