package com.israel.kola.ui.goal_detail.add_contribution

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import android.view.WindowManager
import com.israel.kola.R
import com.israel.kola.ui.goal_detail.GoalDetailActivity
import com.israel.kola.utils.SingletonStore
import com.israel.kola.utils.loadCircularImage
import kotlinx.android.synthetic.main.dialog_add_contribution.*

class AddContributionDialog(context: Context): Dialog(context) {
    lateinit var contributionInterface: ContributionInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_add_contribution)
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        contributorImage.apply {
            clipToOutline = true
            loadCircularImage(SingletonStore.user?.imagePath)
        }

        contributorName.text = SingletonStore.user?.name

        buttonCancel.setOnClickListener {
            dismiss()
        }

        buttonConfirm.setOnClickListener {
            if (!TextUtils.isEmpty(editContribution.text)){
                contributionInterface.addContribution(editContribution.text.toString())
            }
        }
    }

    fun initDialog(activity: GoalDetailActivity){
        this.contributionInterface = activity
    }
}