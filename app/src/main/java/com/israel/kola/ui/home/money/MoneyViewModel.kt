package com.israel.kola.ui.home.money

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.israel.kola.models.Transaction
import com.israel.kola.models.TransactionState

class MoneyViewModel: ViewModel() {
    val transactions = MutableLiveData<List<Transaction>>()
    val loading = MutableLiveData<Boolean>()

    fun fetchTransactions(){
        loading.value = true
        val mockData = listOf(
            Transaction("1", TransactionState.DEPOSIT, 20000),
            Transaction("2", TransactionState.DEPOSIT, 1500),
            Transaction("3", TransactionState.TRANSFER, 10000),
            Transaction("4", TransactionState.TRANSFER, 2500),
            Transaction("5", TransactionState.DEPOSIT, 30000),
            Transaction("6", TransactionState.DEPOSIT, 4000),
            Transaction("7", TransactionState.TRANSFER, 25000),
            Transaction("8", TransactionState.DEPOSIT, 5000),
            Transaction("9", TransactionState.TRANSFER, 10000),
            Transaction("10", TransactionState.DEPOSIT, 13000),
        )
        loading.value = false
        transactions.value = mockData
    }
}