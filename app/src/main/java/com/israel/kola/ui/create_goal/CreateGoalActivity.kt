package com.israel.kola.ui.create_goal

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.israel.kola.R
import com.israel.kola.databinding.ActivityCreateGoalBinding
import com.israel.kola.utils.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_create_goal.*
import java.util.*

@AndroidEntryPoint
class CreateGoalActivity : AppCompatActivity(), View.OnClickListener {
    private val viewModel: CreateGoalViewModel by viewModels()
    lateinit var binding: ActivityCreateGoalBinding
    private val loadingDialog = LoadingDialog()
    private var imageUri: Uri? = null
    private lateinit var datePickerDialog: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_goal)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.buttonConfirm.setOnClickListener(this)
        binding.uploadImage.setOnClickListener(this)
        binding.editGoalDueDate.setOnClickListener(this)
        initDatePicker()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.loading.observe(this, androidx.lifecycle.Observer {
            it?.let {
                if (it){
                    loadingDialog.show(supportFragmentManager, loadingDialog.tag)
                }else{
                    loadingDialog.dismiss()
                }
            }
        })
    }

    private fun initDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        datePickerDialog = DatePickerDialog(this, { _, y, m, d ->
                val date = viewModel.getStringMonth(m)+ " "  + d + ", " + y
            binding.editGoalDueDate.setText(date)
        }, year, month, day)
    }

    private fun validateForm() : Boolean{
        var validate = true
        if (TextUtils.isEmpty(binding.editGroupName.text)){
            validate = false
            binding.editGroupName.error = getString(R.string.field_mandatory)
        } else {
            binding.editGroupName.error = null
        }
        if (TextUtils.isEmpty(binding.editGoalDueDate.text)){
            validate = false
            binding.editGoalDueDate.error = getString(R.string.field_mandatory)
        } else {
            binding.editGoalDueDate.error = null
        }
        if (TextUtils.isEmpty(binding.editGoalAmount.text)){
            validate = false
            binding.editGoalAmount.error = getString(R.string.field_mandatory)
        } else {
            binding.editGoalAmount.error = null
        }
        if (TextUtils.isEmpty(binding.editGroupDescription.text)){
            validate = false
            binding.editGroupDescription.error = getString(R.string.field_mandatory)
        } else {
            binding.editGroupDescription.error = null
        }
        return validate
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.buttonConfirm.id -> {
                if (validateForm()){
                    viewModel.uploadImage(
                        imageUri,
                        binding.editGroupName.text.toString(),
                        binding.editGoalAmount.text.toString(),
                        binding.editGoalDueDate.text.toString(),
                        binding.editGroupDescription.text.toString(),
                        this
                    )
                }
            }
            binding.uploadImage.id -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_PICK
                startActivityForResult(Intent.createChooser(intent, "Select image"), 111)
            }
            binding.editGoalDueDate.id -> {
                datePickerDialog.show()
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