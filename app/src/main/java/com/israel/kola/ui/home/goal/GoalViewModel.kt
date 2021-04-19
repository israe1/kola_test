package com.israel.kola.ui.home.goal

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.israel.kola.models.Goal

class GoalViewModel: ViewModel() {
    val goals = MutableLiveData<List<Goal>>()
    val loading = MutableLiveData<Boolean>()
    val GOAL_DESCRIPITON = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."

    fun fetchGoals(){
        loading.value = true
        val mockData = listOf(
            Goal(1, 200000, 10, "Holy Days party", "July 4, 2021", GOAL_DESCRIPITON, ""),
            Goal(2, 500000, 24, "Easter", "March 10, 2021", GOAL_DESCRIPITON, ""),
            Goal(3, 1500000, 3, "Just for fun", "September 12, 2021", GOAL_DESCRIPITON, ""),
            Goal(4, 800000, 8, "Gifts", "December 20, 2021", GOAL_DESCRIPITON, ""),
            Goal(5, 50000, 10, "School fee", "July 4, 2021", GOAL_DESCRIPITON, ""),
            Goal(6, 100000, 2, "Holy Days party", "August 11, 2021", GOAL_DESCRIPITON, ""),
        )
        loading.value = false
        goals.value = mockData
    }
}