package com.israel.kola.ui.all_transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.israel.kola.databinding.ItemTransactionBinding
import com.israel.kola.data.local.Transaction

class TransactionAdapter(var transactions: ArrayList<Transaction>): RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val binding: ItemTransactionBinding): RecyclerView.ViewHolder(binding.root)

    fun updateTransactions(newTransactions: List<Transaction>){
        transactions.clear()
        transactions.addAll(newTransactions)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TransactionViewHolder(
        ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.binding.transaction = transactions[position]
    }

    override fun getItemCount() = transactions.size
}