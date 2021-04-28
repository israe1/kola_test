package com.israel.kola.ui.home.goal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.israel.kola.R
import com.israel.kola.databinding.FragmentGoalBinding
import com.israel.kola.ui.create_goal.CreateGoalActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoalFragment: Fragment() {
    private val viewModel: GoalViewModel by viewModels()
    lateinit var binding: FragmentGoalBinding
    private var goalAdapter: GoalAdapter = GoalAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.fetchUserGoals(requireContext())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_goal, container, false)

        binding.groupsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = goalAdapter
        }

        binding.fabAddGoal.setOnClickListener {
            val intent = Intent(context, CreateGoalActivity::class.java)
            startActivity(intent)
        }


        binding.paidText.text = "1542000"
        binding.toPayText.text = "254325"

        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.goals.observe(viewLifecycleOwner, Observer {goals ->
            goals?.let{
                goalAdapter.updateGoals(it)
            }
        })

        viewModel.numberOfGroups.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.numberOfGroups.text = it.toString()
            }
        })
    }
}