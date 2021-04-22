package com.israel.kola.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Transaction::class), version = 1)
abstract class LocalDatabase: RoomDatabase(){
    abstract fun transactionDao(): TransactionDao
}