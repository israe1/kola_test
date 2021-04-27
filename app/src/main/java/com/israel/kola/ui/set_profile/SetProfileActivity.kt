package com.israel.kola.ui.set_profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.israel.kola.R
import com.israel.kola.databinding.ActivitySetProfileBinding
import com.israel.kola.utils.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetProfileActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivitySetProfileBinding
    private val loadingDialog = LoadingDialog()
    private val viewModel: SetProfileViewModel by viewModels()
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_profile)
        binding.uploadImage.setOnClickListener(this)
        binding.buttonSave.setOnClickListener(this)
        viewModel.checkIfUserInfoIsSet(this)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.loading.observe(this, Observer {
            it?.let {
                if (it){
                    loadingDialog.show(supportFragmentManager, loadingDialog.tag)
                }else{
                    loadingDialog.dismiss()
                }
            }
        })

        viewModel.imageUrl.observe(this, Observer {
            it?.let {
                imageUri = Uri.parse(it)
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.uploadImage.id -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_PICK
                startActivityForResult(Intent.createChooser(intent, "Select image"), 111)
            }

            binding.buttonSave.id -> {
                binding.editUserName.error = null
                if (!TextUtils.isEmpty(binding.editUserName.text)){
                    viewModel.saveToFirebase(imageUri, binding.editUserName.text.toString(), this)
                }else{
                    binding.editUserName.error = getString(R.string.field_mandatory)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK
            && data != null && data.data != null){
            imageUri = data.data
            Glide.with(this).load(imageUri).into(binding.avatar)
        }
    }
}