package com.israel.kola.ui.home.money

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.israel.kola.data.local.Transaction
import com.israel.kola.models.TransactionState

class MoneyViewModel: ViewModel() {
    val transactions = MutableLiveData<List<Transaction>>()
    val loading = MutableLiveData<Boolean>()

}