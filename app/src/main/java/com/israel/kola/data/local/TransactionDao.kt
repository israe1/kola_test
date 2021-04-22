package com.israel.kola.data.local

import androidx.room.*

@Dao
interface TransactionDao{
    @Query("SELECT * FROM `transaction`")
    fun getAll(): List<Transaction>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg transaction: Transaction)
}