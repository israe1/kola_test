package com.israel.kola.ui.all_transactions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.israel.kola.models.Transaction
import com.israel.kola.models.TransactionState

class AllTransactionsViewModel: ViewModel() {
    val transactions = MutableLiveData<List<Transaction>>()
    val loading = MutableLiveData<Boolean>()

    fun fetchTransactions(){
        loading.value = true
        val mockData = listOf(
            Transaction(1, TransactionState.IN, 20000),
            Transaction(2, TransactionState.IN, 1500),
            Transaction(3, TransactionState.OUT, 10000),
            Transaction(4, TransactionState.OUT, 2500),
            Transaction(5, TransactionState.IN, 30000),
            Transaction(6, TransactionState.IN, 4000),
            Transaction(7, TransactionState.OUT, 25000),
            Transaction(8, TransactionState.IN, 5000),
            Transaction(9, TransactionState.OUT, 10000),
            Transaction(10, TransactionState.IN, 13000),
        )
        loading.value = false
        transactions.value = mockData
    }
}