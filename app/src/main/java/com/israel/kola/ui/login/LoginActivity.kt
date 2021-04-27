package com.israel.kola.ui.login

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.israel.kola.R
import com.israel.kola.databinding.ActivityLoginBinding
import com.israel.kola.utils.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    @Inject lateinit var fireStore: FirebaseFirestore
    lateinit var binding: ActivityLoginBinding
    private val loadingDialog = LoadingDialog()
    private val viewModel: LoginViewModel by viewModels()
    private var verificationId: String = ""
    private var state = LoginState.PHONE_NUMBER
    private var phoneNumber = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.buttonLogin.setOnClickListener {
            if (state == LoginState.PHONE_NUMBER){
                getPhoneNumber()
            } else {
                verifyCode()
            }
        }

        binding.changePhoneNumber.setOnClickListener {
            switchState(LoginState.PHONE_NUMBER)
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.error.observe(this, Observer {
            it?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
        viewModel.verificationId.observe(this, Observer {
            it?.let {
                verificationId = it
                switchState(LoginState.CODE)
            }
        })
        viewModel.credential.observe(this, Observer {
            it?.let {
                viewModel.signInWithPhoneAuthCredential(it, this)
            }
        })
        viewModel.loading.observe(this, Observer {
            it?.let {
                if (it){
                    loadingDialog.show(supportFragmentManager, loadingDialog.tag)
                }else{
                    loadingDialog.dismiss()
                }
            }
        })
    }

    private fun switchState(state: LoginState) {
        this.state = state
        if (state == LoginState.CODE){
            binding.buttonLogin.text = getString(R.string.validate)
            binding.layoutPhoneNumber.visibility = View.GONE
            binding.layoutCode.visibility = View.VISIBLE
            binding.phoneNumber.text = phoneNumber
        } else {
            binding.buttonLogin.text = getString(R.string.log_in)
            binding.layoutPhoneNumber.visibility = View.VISIBLE
            binding.layoutCode.visibility = View.GONE
        }
    }

    private fun getPhoneNumber(){
        binding.editPhone.error = null
        if (!TextUtils.isEmpty(binding.editPhone.text)){
            phoneNumber = binding.countryCodePicker.textView_selectedCountry.text
                .toString() + binding.editPhone.text.toString()
            viewModel.startPhoneVerification(phoneNumber, this)
        }else{
            binding.editPhone.error = getString(R.string.field_mandatory)
        }
    }

    private fun verifyCode(){
        binding.editCode.error = null
        if (!TextUtils.isEmpty(binding.editCode.text)){
            val code = binding.editCode.text.toString()
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            viewModel.credential.value = credential
        } else {
            binding.editCode.error = getString(R.string.field_mandatory)
        }
    }
}