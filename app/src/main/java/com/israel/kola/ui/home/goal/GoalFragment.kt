package com.israel.kola.ui.home.goal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.israel.kola.R
import com.israel.kola.databinding.FragmentGoalBinding
import com.israel.kola.ui.create_goal.CreateGoalActivity

class GoalFragment: Fragment() {
    lateinit var viewModel: GoalViewModel
    lateinit var binding: FragmentGoalBinding
    private var goalAdapter: GoalAdapter = GoalAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProviders.of(this).get(GoalViewModel::class.java)
        viewModel.fetchGoals()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_goal, container, false)

        binding.groupsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = goalAdapter
        }

        binding.fabAddGoal.setOnClickListener {
            val intent = Intent(context, CreateGoalActivity::class.java)
            startActivity(intent)
        }

        binding.numberOfGroups.text = "6"
        binding.paidText.text = "1542000"
        binding.toPayText.text = "254325"

        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.goals.observe(this, {goals ->
            goals?.let{
                goalAdapter.updateGoals(it)
            }
        })
    }
}