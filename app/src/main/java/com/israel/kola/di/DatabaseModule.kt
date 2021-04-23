package com.israel.kola.di

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.israel.kola.data.local.LocalDatabase
import com.israel.kola.data.local.Transaction
import com.israel.kola.data.local.TransactionDao
import com.israel.kola.models.TransactionState
import com.israel.kola.utils.doAsync
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.regex.Pattern
import javax.inject.Inject
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