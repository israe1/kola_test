package com.israel.kola.ui.home.goal

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.israel.kola.data.remote.Goal
import com.israel.kola.utils.SingletonStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(private var firestore: FirebaseFirestore, private var auth: FirebaseAuth): ViewModel() {
    val goals = MutableLiveData<List<Goal>>()
    val loading = MutableLiveData<Boolean>()
    val numberOfGroups = MutableLiveData<Int>()

    fun fetchUserGoals(context: Context){
        loading.value = true
        val firebaseUser = auth.currentUser ?: return
        val documentQuery = firestore.collection(SingletonStore.USER_GOAL_TABLE).whereEqualTo("userId", firebaseUser.uid)
        documentQuery.addSnapshotListener{ values, e ->
            loading.value = false
            if (e != null){
                Toast.makeText(context, "An Error Occurred", Toast.LENGTH_LONG).show()
                e.printStackTrace()
                return@addSnapshotListener
            }
            numberOfGroups.value = values?.size()
            for(doc in values!!){
                doc?.let {
                    goals.value = arrayListOf()
                    doc.getString("goalId")?.let {
                        fetchGoalsInfo(it)
                    }
                }
            }
        }
    }

    private fun fetchGoalsInfo(goalId: String){
        loading.value = true
        firestore.collection(SingletonStore.GOAL_TABLE).document(goalId)
            .get().addOnSuccessListener { document ->
                loading.value = false
                document?.data?.let {
                    val goal = Goal(
                        it["id"].toString(),
                        it["name"].toString(),
                        it["amount"].toString(),
                        it["date"].toString(),
                        it["description"].toString(),
                        it["imagePath"].toString(),
                        it["numberOfMembers"].toString()
                    )
                    val goalList : ArrayList<Goal> = goals.value as ArrayList<Goal>
                    goalList.add(goal)
                    goals.value = goalList
                    Log.e("Goal", goal.toString())
                }
            } . addOnFailureListener {
                loading.value = false
                it.printStackTrace()
            }
    }
}