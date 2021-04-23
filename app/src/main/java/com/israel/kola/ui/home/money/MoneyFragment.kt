package com.israel.kola.ui.home.money

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.israel.kola.R
import com.israel.kola.data.local.Transaction
import com.israel.kola.data.local.TransactionDataSource
import com.israel.kola.databinding.FragmentMoneyBinding
import com.israel.kola.models.TransactionState
import com.israel.kola.ui.all_transactions.AllTransactionsActivity
import com.israel.kola.ui.all_transactions.TransactionAdapter
import com.israel.kola.ui.all_transactions.TransactionsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MoneyFragment: Fragment() {
    private val viewModel: TransactionsViewModel by viewModels()
    lateinit var binding: FragmentMoneyBinding
    private var transactionAdapter: TransactionAdapter = TransactionAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_money, container, false)

        binding.transactionsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }

        binding.viewAllTransactions.setOnClickListener {
            val intent = Intent(context, AllTransactionsActivity::class.java)
            startActivity(intent)
        }

        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.transactions.observe(viewLifecycleOwner, Observer {transactions ->
            transactions?.let{
                if (it.size > 4){
                    transactionAdapter.updateTransactions(it.subList(0, 4))
                }else{
                    transactionAdapter.updateTransactions(it)
                }
            }
        })

        viewModel.balance.observe(viewLifecycleOwner, Observer {
            binding.balanceAmount.text = it.toString()
        })

        viewModel.deposit.observe(viewLifecycleOwner, Observer {
            binding.depositText.text = it.toString()
        })

        viewModel.credit.observe(viewLifecycleOwner, Observer {
            binding.creditText.text = it.toString()
        })

        viewModel.transfer.observe(viewLifecycleOwner, Observer {
            binding.transferText.text = it.toString()
        })

        viewModel.withdraw.observe(viewLifecycleOwner, Observer {
            binding.withdrawText.text = it.toString()
        })
    }
}