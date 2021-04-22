package com.israel.kola.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.israel.kola.models.TransactionState

@Entity(tableName = "transaction")
data class Transaction (
    @PrimaryKey val id: String,
    @ColumnInfo val state: String?,
    @ColumnInfo val amount: Int
)