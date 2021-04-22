package com.israel.kola.ui.all_transactions

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.israel.kola.R
import kotlinx.android.synthetic.main.activity_all_transactions.*

class AllTransactionsActivity : AppCompatActivity() {
    lateinit var viewModel: TransactionsViewModel
    private var transactionAdapter: TransactionAdapter = TransactionAdapter(arrayListOf())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_transactions)

        viewModel = ViewModelProviders.of(this).get(TransactionsViewModel::class.java)
        viewModel.fetchTransactions(this)

        transactionsList.apply {
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