package com.israel.kola.ui.goal_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.israel.kola.data.remote.Contribution
import com.israel.kola.databinding.ItemContributionBinding
import com.israel.kola.utils.loadCircularImage

class ContributionAdapter(private val contributions: ArrayList<Contribution>) : RecyclerView.Adapter<ContributionAdapter.ContributionViewHolder>() {

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
        val contribution = contributions[position]
        holder.binding.contribution = contribution
        holder.binding.member = contribution.user
        holder.binding.contributorImage.loadCircularImage(contribution.user?.imagePath)
    }

    override fun getItemCount() = contributions.size

    companion object{
        @JvmStatic
        fun newInstance(
            list: ArrayList<Contribution>
        ) = ContributionAdapter(list)
    }
}