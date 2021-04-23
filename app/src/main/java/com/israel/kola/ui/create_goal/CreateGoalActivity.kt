package com.israel.kola.ui.create_goal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.israel.kola.R
import kotlinx.android.synthetic.main.activity_create_goal.*

class CreateGoalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_goal)

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}