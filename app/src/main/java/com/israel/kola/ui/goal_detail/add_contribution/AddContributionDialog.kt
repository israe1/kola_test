package com.israel.kola.ui.goal_detail.add_contribution

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.israel.kola.databinding.DialogAddContributionBinding

class AddContributionDialog: DialogFragment() {
    lateinit var bindng: DialogAddContributionBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        bindng = DialogAddContributionBinding.inflate(inflater)
        dialog.setView(bindng.root)

        return dialog.create()
    }

    companion object{
        fun newInstance(): AddContributionDialog{
            return AddContributionDialog()
        }
    }
}