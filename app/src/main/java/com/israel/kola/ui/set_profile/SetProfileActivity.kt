package com.israel.kola.ui.set_profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.israel.kola.R
import com.israel.kola.databinding.ActivitySetProfileBinding

class SetProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivitySetProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_profile)

    }
}