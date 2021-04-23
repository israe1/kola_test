package com.israel.kola.data.local

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionDataSource @Inject constructor(private val transactionDao: TransactionDao){
    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    private val mainThreadHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    fun addTransaction(transaction: Transaction){
        executorService.execute{
            transactionDao.insert(transaction)
        }
    }

    fun getAllTransactions(callback: (Flow<List<Transaction>>) -> Unit){
        executorService.execute {
            val transactions = transactionDao.getAll()
            mainThreadHandler.post{ callback(transactions) }
        }
    }
}