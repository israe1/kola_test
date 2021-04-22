package com.israel.kola.ui.all_transactions

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.israel.kola.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllTransactionsActivity : AppCompatActivity() {
    private val viewModel: TransactionsViewModel by viewModels()
    private var transactionAdapter: TransactionAdapter = TransactionAdapter(arrayListOf())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_transactions)


        findViewById<RecyclerView>(R.id.transactionsList).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = transactionAdapter
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.transactions.observe(this, Observer { transactions ->
            transactions?.let{
                transactionAdapter.updateTransactions(it)
            }
        })
    }

}