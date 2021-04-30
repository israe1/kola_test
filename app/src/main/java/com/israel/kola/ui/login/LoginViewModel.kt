package com.israel.kola.ui.login

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.israel.kola.ui.set_profile.SetProfileActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private var auth: FirebaseAuth) : ViewModel() {
    val error = MutableLiveData<String>()
    val verificationId = MutableLiveData<String>()
    val credential = MutableLiveData<PhoneAuthCredential>()
    val loading = MutableLiveData<Boolean>()

    private var callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(authCredential: PhoneAuthCredential) {
            credential.value = authCredential
            loading.value = false
        }

        override fun onVerificationFailed(e: FirebaseException) {
            error.value = "Echec survenu lors de la vérification"
        }

        override fun onCodeSent(verifyPhoneNumberId: String, token: PhoneAuthProvider.ForceResendingToken) {
            verificationId.value = verifyPhoneNumberId
            loading.value = false
        }

    }

    fun startPhoneVerification(phone: String, activity: LoginActivity){
        loading.value = true
        val option = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, activity: LoginActivity){
        loading.value = true
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful){
                    val intent = Intent(activity, SetProfileActivity::class.java)
                    activity.startActivity(intent)
                    activity.finish()
                    loading.value = false
                } else {
                    error.value = "Echec d'authentification"
                    loading.value = false
                }
            }
    }
}