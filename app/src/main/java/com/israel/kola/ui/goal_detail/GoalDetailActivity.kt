package com.israel.kola.ui.goal_detail

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.israel.kola.R
import com.israel.kola.data.remote.Contribution
import com.israel.kola.data.remote.Goal
import com.israel.kola.data.remote.User
import com.israel.kola.databinding.ActivityGoalDetailBinding
import com.israel.kola.ui.goal_detail.add_contribution.AddContributionDialog
import com.israel.kola.ui.goal_detail.add_contribution.ContributionInterface
import com.israel.kola.utils.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_goal_detail.*

@AndroidEntryPoint
class GoalDetailActivity : AppCompatActivity(), ContributionInterface {
    private val viewModel: GoalDetailViewModel by viewModels()
    private lateinit var binding: ActivityGoalDetailBinding
    private val loadingDialog = LoadingDialog()
    var goal: Goal? = null
    private lateinit var dialog: AddContributionDialog
    private var goalContributions = arrayListOf<Contribution>()
    private var goalMembers = arrayListOf<User>()
    private var amountPerUser: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_goal_detail)
        dialog = AddContributionDialog(this)
        dialog.initDialog(this)
        goal = intent.getParcelableExtra<Goal>("goal")
        binding.goal = goal

        amountPerUser = goal?.amount?.toInt()?.div(goal?.numberOfMembers?.toInt()!!) as Int

        viewModel.getContributions(goal?.id!!)
        viewModel.getMembers(goal?.id!!)


        toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.contributionList.apply {
            layoutManager = LinearLayoutManager(context)
        }


        binding.remainingList.apply {
            layoutManager = LinearLayoutManager(context)
        }

        binding.buttonContribute.setOnClickListener{
            dialog.show()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.contributions.observe(this, Observer {contributions ->
            contributions?.let {
                goalContributions.clear()
                goalContributions.addAll(it)
                val contributionAdapter = ContributionAdapter(it as ArrayList<Contribution>)
                binding.contributionList.adapter = contributionAdapter
                setAmount(it , binding.goalContributedAmount)
                getRemainingList()
            }
        })

        viewModel.loading.observe(this, Observer {
            it?.let {
                if (it){
                    loadingDialog.show(supportFragmentManager, loadingDialog.tag)
                }else{
                    loadingDialog.dismiss()
                }
            }
        })

        viewModel.goalMembers.observe(this, Observer {
            it?.let{
                goalMembers = it as ArrayList<User>
                getRemainingList()
            }
        })
    }

    private fun getRemainingList(){
        if (goalMembers.isEmpty() || goalContributions.isEmpty())return
        val remainingContributions = arrayListOf<Contribution>()
        for (member in goalMembers){
            for (c in goalContributions){
                if (c.user?.id == member.id){
                    if (c.amount?.toInt()!! < amountPerUser!!){
                        c.amount = (amountPerUser!! - c.amount?.toInt()!!).toString()
                        remainingContributions.add(c)
                    }
                }
            }
        }
        setAmount(remainingContributions, binding.goalRemainingAmount)
        val remainingAdapter = ContributionAdapter(remainingContributions)
        binding.remainingList.adapter = remainingAdapter
    }

    private fun setAmount(list: ArrayList<Contribution>, textView: TextView){
        var remainingAmount = 0
        for (r in list){
            remainingAmount += r.amount?.toInt()!!
        }
        textView.text = remainingAmount.toString()
    }

    override fun addContribution(amount: String) {
        viewModel.getUserGoalId(amount, goal?.id!!, this)
        dialog.dismiss()
    }
}