package com.israel.kola.ui.create_goal

import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.israel.kola.data.remote.Goal
import javax.inject.Inject

class CreateGoalViewModel @Inject constructor(
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

    private fun saveGoal(name: String, image: String?, amount: String, date: String, description: String, activity: CreateGoalActivity){
        val goalId = fireStore.collection("goals").document().id
        val goal = Goal(
            goalId,
            name,
            amount,
            date,
            description,
            image
        )
        fireStore.collection("goals").document(goalId)
            .set(goal)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }

    private fun getExt(uri: Uri, activity: CreateGoalActivity): String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }
}