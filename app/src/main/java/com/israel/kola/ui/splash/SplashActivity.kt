package com.israel.kola.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.israel.kola.R
import com.israel.kola.ui.home.HomeActivity
import com.israel.kola.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    @Inject lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        if (auth.currentUser != null){
            Handler().postDelayed({
                startActivity(Intent(this, HomeActivity::class.java))
            }, 3000)
        } else {
            Handler().postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
            }, 3000)
        }
    }
}