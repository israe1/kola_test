package com.israel.kola.ui.goal_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.israel.kola.databinding.ItemContributionBinding
import com.israel.kola.models.Contribution
import com.israel.kola.models.Member

class ContributionAdapter(var contributions: ArrayList<Contribution>): RecyclerView.Adapter<ContributionAdapter.ContributionViewHolder>() {

    class ContributionViewHolder(val binding: ItemContributionBinding): RecyclerView.ViewHolder(binding.root)

    fun updateContributions(newContributions: List<Contribution>){
        contributions.clear()
        contributions.addAll(newContributions)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ContributionViewHolder(
        ItemContributionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ContributionViewHolder, position: Int) {
        holder.binding.contribution = contributions[position]
        holder.binding.member = Member("Israel MEKOMOU")
    }

    override fun getItemCount() = contributions.size
}