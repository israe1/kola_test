package com.israel.kola.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao{
    @Query("SELECT * FROM `transaction`")
    fun getAll(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg transaction: Transaction)
}