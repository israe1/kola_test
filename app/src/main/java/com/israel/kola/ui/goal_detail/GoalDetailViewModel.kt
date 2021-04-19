package com.israel.kola.ui.goal_detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.israel.kola.models.Contribution

class GoalDetailViewModel: ViewModel() {
    val contributions = MutableLiveData<List<Contribution>>()

    fun fetchContributions(){
        val mockData = listOf(
            Contribution(1, 25400, 1),
            Contribution(2, 2000, 1),
            Contribution(3, 48350, 1),
        )

        contributions.value = mockData
    }
}