package com.israel.kola.utils

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.israel.kola.R

class LoadingDialog: AppCompatDialogFragment() {
    lateinit var v: View
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        v = requireActivity().layoutInflater.inflate(R.layout.dialog_loading, null)
        builder.setView(v)
        return builder.create()
    }

    fun setLoadingMessage(message: String){
        v.findViewById<TextView>(R.id.loadingMessage).text = message
    }
}