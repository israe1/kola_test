package com.israel.kola.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class], version = 2)
abstract class LocalDatabase: RoomDatabase(){
    abstract fun transactionDao(): TransactionDao
}