package com.israel.kola.ui.all_transactions

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.israel.kola.R
import com.israel.kola.databinding.ActivityAllTransactionsBinding
import com.israel.kola.utils.TransactionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_all_transactions.*
import kotlinx.android.synthetic.main.fragment_money.transactionsList

@AndroidEntryPoint
class AllTransactionsActivity : AppCompatActivity(), View.OnClickListener {
    private val viewModel: TransactionsViewModel by viewModels()
    private var transactionAdapter: TransactionAdapter = TransactionAdapter(arrayListOf())
    lateinit var binding: ActivityAllTransactionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_transactions)


        transactionsList.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = transactionAdapter
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.layoutDeposit.setOnClickListener(this)
        binding.layoutWithdraw.setOnClickListener(this)
        binding.layoutTransfer.setOnClickListener(this)
        binding.layoutCredit.setOnClickListener(this)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.transactions.observe(this, Observer {transactions ->
            transactions?.let{
                transactionAdapter.updateTransactions(it)
            }
        })

        viewModel.total.observe(this, Observer {
            binding.totalTransactionsAmount.text = it.toString()
        })

        viewModel.deposit.observe(this, Observer {
            binding.depositText.text = it.toString()
        })

        viewModel.credit.observe(this, Observer {
            binding.creditText.text = it.toString()
        })

        viewModel.transfer.observe(this, Observer {
            binding.transferText.text = it.toString()
        })

        viewModel.withdraw.observe(this, Observer {
            binding.withdrawText.text = it.toString()
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.layoutDeposit.id -> {
                handleStateFilter(TransactionState.DEPOSIT)
            }
            binding.layoutWithdraw.id -> {
                handleStateFilter(TransactionState.WITHDRAW)
            }
            binding.layoutTransfer.id -> {
                handleStateFilter(TransactionState.TRANSFER)
            }
            binding.layoutCredit.id -> {
                handleStateFilter(TransactionState.CREDIT)
            }
        }
    }

    private fun handleStateFilter(state: TransactionState){
        if(binding.state == state){
            binding.state = null
            transactionAdapter.updateTransactions(viewModel.sortTransactionsByState(state, false))
        }else{
            binding.state = state
            transactionAdapter.updateTransactions(viewModel.sortTransactionsByState(state, true))
        }
    }
}