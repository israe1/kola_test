package com.israel.kola.ui.set_profile

import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.israel.kola.data.remote.User
import com.israel.kola.ui.home.HomeActivity
import com.israel.kola.utils.SingletonStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetProfileViewModel @Inject constructor(
    private var fireStore: FirebaseFirestore,
    private var auth: FirebaseAuth,
    private var storage: FirebaseStorage
) : ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val imageUrl = MutableLiveData<String?>()

    fun saveToFirebase(imageUri: Uri?, name: String, activity: SetProfileActivity) {
        loading.value = true
        if (imageUri != null) {
            if(!imageUri.toString().contains("http")){
                val storageReference =
                    storage.reference.child("ImagesProfile/" + System.currentTimeMillis() + "." + getExt(imageUri, activity))
                storageReference.putFile(imageUri)
                    .addOnSuccessListener {
                        val taskUrl = it.storage.downloadUrl
                        while (!taskUrl.isComplete){ }
                        val downloadUrl = taskUrl.result.toString()
                        setUserInfo(name, downloadUrl, activity)
                    }
                    .addOnFailureListener { e ->
                        loading.value = false
                        Toast.makeText(activity, "Upload Failed", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
            } else {
                TODO("Manage user reconnection")
            }
        } else {
            setUserInfo(name, null, activity)
        }
    }

    private fun setUserInfo(name: String, image: String?, activity: SetProfileActivity){
        val user = User(
            auth.currentUser?.uid,
            name,
            image
        )
        fireStore.collection("users").document(auth.currentUser!!.uid)
            .set(user)
            .addOnSuccessListener {
                loading.value = false
                SingletonStore.user = user
                activity.startActivity(Intent(activity, HomeActivity::class.java))
                activity.finish()
            }
            .addOnFailureListener { e ->
                loading.value = false
                e.printStackTrace()
            }
    }

    private fun getExt(uri: Uri, activity: SetProfileActivity): String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }

    fun checkIfUserInfoIsSet(activity: SetProfileActivity){
        loading.value = true
        fireStore.collection("users").document(auth.currentUser!!.uid)
            .get().addOnCompleteListener { task ->
                loading.value = false
                if (task.isSuccessful) {
                    val result = task.result
                    result?.let {
                        if (it.exists()) {
                            val url = it.getString("imagePath")
                            imageUrl.value = url
                            Glide.with(activity).load(url).into(activity.binding.avatar)
                            activity.binding.editUserName.setText(it.getString("name"))
                        }
                    }

                }
            }
    }
}