package com.israel.kola.ui.home.money

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.israel.kola.R
import com.israel.kola.databinding.FragmentMoneyBinding
import com.israel.kola.ui.all_transactions.AllTransactionsActivity
import com.israel.kola.ui.all_transactions.TransactionAdapter
import com.israel.kola.ui.all_transactions.TransactionsViewModel

class MoneyFragment: Fragment() {
    lateinit var viewModel: TransactionsViewModel
    lateinit var binding: FragmentMoneyBinding
    private var transactionAdapter: TransactionAdapter = TransactionAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProviders.of(this).get(TransactionsViewModel::class.java)
        viewModel.fetchTransactions(requireActivity())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_money, container, false)

        binding.transactionsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }

        binding.viewAllTransactions.setOnClickListener {
            val intent = Intent(context, AllTransactionsActivity::class.java)
            startActivity(intent)
        }

        binding.balanceAmount.text = "200000"
        binding.creditText.text = "3000"
        binding.depositText.text = "542000"
        binding.transferText.text = "128000"
        binding.internetText.text = "15350"

        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.transactions.observe(viewLifecycleOwner, Observer {transactions ->
            transactions?.let{
                transactionAdapter.updateTransactions(it)
            }
        })
    }
}