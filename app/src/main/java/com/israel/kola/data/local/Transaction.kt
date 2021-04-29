package com.israel.kola.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction")
data class Transaction (
    @PrimaryKey val id: String,
    @ColumnInfo val state: String,
    @ColumnInfo val date: String,
    @ColumnInfo val amount: Int,
    @ColumnInfo val newBalance: String?
)