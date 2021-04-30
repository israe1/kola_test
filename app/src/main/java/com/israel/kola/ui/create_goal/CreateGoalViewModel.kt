package com.israel.kola.ui.create_goal

import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.israel.kola.data.remote.Contribution
import com.israel.kola.data.remote.Goal
import com.israel.kola.data.remote.UserGoal
import com.israel.kola.ui.goal_detail.add_contribution.AddContributionDialog
import com.israel.kola.utils.SingletonStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateGoalViewModel @Inject constructor(
    private var auth: FirebaseAuth,
    private var fireStore: FirebaseFirestore,
    private var storage: FirebaseStorage
) : ViewModel() {
    val loading = MutableLiveData<Boolean>()

    fun uploadImage(imageUri: Uri?, name: String, amount: String, date: String, description: String, activity: CreateGoalActivity){
        loading.value = true
        if (imageUri != null) {
            val storageReference =
                storage.reference.child("GoalImages/" + System.currentTimeMillis() + "." + getExt(imageUri, activity))
            storageReference.putFile(imageUri)
                .addOnSuccessListener {
                    val taskUrl = it.storage.downloadUrl
                    while (!taskUrl.isComplete){ }
                    val downloadUrl = taskUrl.result.toString()
                    saveGoal(name, downloadUrl, amount, date, description, activity)
                }
                .addOnFailureListener { e ->
                    loading.value = false
                    Toast.makeText(activity, "Upload Failed", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
        } else {
            saveGoal(name, null, amount, date, description, activity)
        }
    }

    fun getStringMonth(index: Int): String{
        when(index){
            1 -> return "Jan"
            2 -> return "Feb"
            3 -> return "March"
            4 -> return "April"
            5 -> return "May"
            6 -> return "June"
            7 -> return "July"
            8 -> return "Aug"
            9 -> return "Sept"
            10 -> return "Oct"
            11 -> return "Nov"
            12 -> return "Dec"
        }
        return ""
    }

    private fun saveGoal(name: String, image: String?, amount: String, date: String, description: String, activity: CreateGoalActivity){
        val goalId = fireStore.collection(SingletonStore.GOAL_TABLE).document().id
        val goal = Goal(
            goalId,
            name,
            amount,
            date,
            description,
            image,
            "1"
        )
        fireStore.collection(SingletonStore.GOAL_TABLE).document(goalId)
            .set(goal)
            .addOnSuccessListener {
                loading.value = false
                addUserToGoal(goalId)
                Toast.makeText(activity, "Goal created successfully", Toast.LENGTH_LONG).show()
                activity.finish()
            }
            .addOnFailureListener {
                Toast.makeText(activity, "An error occurred", Toast.LENGTH_LONG).show()
                loading.value = false
            }
    }

    private fun addUserToGoal(goalId: String) {
        val firebaseUser = auth.currentUser ?: return
        val userGoalId = fireStore.collection(SingletonStore.USER_GOAL_TABLE).document().id
        val userGoal = UserGoal(
            userGoalId,
            firebaseUser.uid,
            goalId
        )
        fireStore.collection(SingletonStore.USER_GOAL_TABLE).document(userGoalId)
            .set(userGoal)
            .addOnSuccessListener {
                Log.e("CREATE", "USER added to Goal")
            }
            .addOnFailureListener {
                it.printStackTrace()
                Log.e("CREATE", "Error while adding user to goal")
            }
    }

    private fun getExt(uri: Uri, activity: CreateGoalActivity): String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }


}