package com.israel.kola.ui.home.goal

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.israel.kola.data.remote.Goal
import com.israel.kola.databinding.ItemGroupBinding
import com.israel.kola.ui.goal_detail.GoalDetailActivity
import com.israel.kola.utils.getProgressDrawable
import com.israel.kola.utils.loadImage

class GoalAdapter(var goals: ArrayList<Goal>): RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {
    class GoalViewHolder(val binding: ItemGroupBinding): RecyclerView.ViewHolder(binding.root), View.OnClickListener{
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val intent = Intent(v?.context, GoalDetailActivity::class.java)
            intent.putExtra("goal", binding.goal)
            v?.context?.startActivity(intent)
        }

    }

    fun updateGoals(newGoals: List<Goal>){
        goals.clear()
        goals.addAll(newGoals)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GoalViewHolder(
        ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val progressDrawable = getProgressDrawable(holder.itemView.context)
        val goal = goals[position]
        holder.binding.goal = goal
        if (goal.imagePath != null){
            holder.binding.groupImage.loadImage(goal.imagePath, progressDrawable)
        }
    }

    override fun getItemCount() = goals.size
}