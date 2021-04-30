package com.israel.kola.ui.goal_detail

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.israel.kola.data.remote.Contribution
import com.israel.kola.data.remote.User
import com.israel.kola.utils.SingletonStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GoalDetailViewModel @Inject constructor(
    private var auth: FirebaseAuth,
    private var fireStore: FirebaseFirestore
): ViewModel() {
    val contributions = MutableLiveData<List<Contribution>>()
    val loading = MutableLiveData<Boolean>()
    val goalMembers = MutableLiveData<List<User>>()

    fun getMembers(goalId: String){
        val users = arrayListOf<User>()
        fireStore.collection(SingletonStore.USER_GOAL_TABLE).whereEqualTo("goalId", goalId)
            .addSnapshotListener{ values, e ->
                if (e != null){
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                for (doc in values!!){
                    doc?.let { userGoalDoc ->
                        fireStore.collection(SingletonStore.USER_TABLE).document(userGoalDoc.getString("userId")!!)
                            .get().addOnSuccessListener { userDoc ->
                                userDoc?.data?.let {
                                    val user = User(
                                        it["id"].toString(),
                                        it["name"].toString(),
                                        it["imagePath"].toString(),
                                    )
                                    users.add(user)
                                }
                                goalMembers.value = users
                            }
                            .addOnFailureListener {
                                Log.e("ERROR", "GET_USER")
                            }
                    }
                }
            }
    }

    fun getContributions(goalId: String){
        val contributionList = arrayListOf<Contribution>()
        loading.value = true
        fireStore.collection(SingletonStore.CONTRIBUTION_TABLE)
            .addSnapshotListener{ values, e ->
                if (e != null){
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                for (doc in values!!){
                    doc?.let { contributionSnapshot ->
                        fireStore.collection(SingletonStore.USER_GOAL_TABLE)
                            .document(contributionSnapshot.getString("userGoalId")!!)
                            .get().addOnSuccessListener { userGoalDoc ->
                                userGoalDoc?.data?.let {
                                    if (it["goalId"] == goalId){
                                        fireStore.collection(SingletonStore.USER_TABLE).document(it["userId"].toString())
                                            .get().addOnSuccessListener { userDoc ->
                                                val contribution = Contribution(
                                                    contributionSnapshot.getString("id"),
                                                    contributionSnapshot.getString("userGoalId"),
                                                    contributionSnapshot.getString("amount"),
                                                    contributionSnapshot.getString("date"),
                                                    User(
                                                        userDoc["id"].toString(),
                                                        userDoc["name"].toString(),
                                                        userDoc["imagePath"].toString(),
                                                    )
                                                )
                                                contributionList.add(contribution)
                                                loading.value = false
                                                contributions.value = contributionList
                                            }
                                            .addOnFailureListener {
                                                Log.e("ERROR", "GET_USER_FROM_CONTRIBUTION")
                                            }
                                    }
                                }
                            }
                            .addOnFailureListener {
                                loading.value = false
                            }
                    }
                }
            }
    }

    fun getUserGoalId(amount: String, goalId: String, activity: GoalDetailActivity){
        loading.value = true
        val firebaseUser = auth.currentUser ?: return
        val query = fireStore.collection(SingletonStore.USER_GOAL_TABLE).whereEqualTo("goalId", goalId)
        query.whereEqualTo("userId", firebaseUser.uid).get()
            .addOnSuccessListener { snapShots ->
                for (doc in snapShots){
                    doc?.let { document ->
                        document.getString("id")?.let {
                            addContribution(it, amount, activity)
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e("ERROR", "GET_USER_GOAL_ID")
            }
    }


    private fun addContribution(userGoalId: String, amount: String, activity: GoalDetailActivity){
        val contributionId = fireStore.collection(SingletonStore.CONTRIBUTION_TABLE).document().id
        val contribution = Contribution(
            contributionId,
            userGoalId,
            amount,
            System.currentTimeMillis().toString(),
            null
        )

        fireStore.collection(SingletonStore.CONTRIBUTION_TABLE).document(contributionId)
            .set(contribution).addOnSuccessListener {
                Toast.makeText(activity, "Contribution added successfully", Toast.LENGTH_LONG).show()
                loading.value = false
            }
            .addOnFailureListener {
                Toast.makeText(activity, "An error occurred", Toast.LENGTH_LONG).show()
                loading.value = false
            }
    }
}