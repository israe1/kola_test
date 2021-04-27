package com.israel.kola.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.israel.kola.R
import com.israel.kola.data.remote.User
import com.israel.kola.ui.home.HomeActivity
import com.israel.kola.ui.login.LoginActivity
import com.israel.kola.ui.set_profile.SetProfileActivity
import com.israel.kola.utils.SingletonStore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var fireStore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            var userIsSet = false
            fireStore.collection("users").document(firebaseUser.uid)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val result = task.result
                        result?.let {
                            if (it.exists()) {
                                SingletonStore.user = User(
                                    it.getString("id"),
                                    it.getString("name"),
                                    it.getString("imagePath"),
                                )
                                userIsSet = true
                            }
                        }

                    }

                    if (userIsSet) {
                        Handler().postDelayed({
                            startActivity(Intent(this, HomeActivity::class.java))
                        }, 3000)
                    } else {
                        Handler().postDelayed({
                            startActivity(Intent(this, SetProfileActivity::class.java))
                        }, 3000)
                    }
                }

        } else {
            Handler().postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
            }, 3000)
        }
    }
}