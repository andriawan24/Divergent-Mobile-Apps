package com.andriawan.divergent_mobile_apps.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.databinding.CustomDialogConfirmBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ConfirmDialog(context: Context, layoutInflater: LayoutInflater) {

    private val binding: CustomDialogConfirmBinding = CustomDialogConfirmBinding.inflate(layoutInflater)
    private var alertDialog: AlertDialog =
        MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_rounded)
            .setView(binding.root)
            .create()

    lateinit var onYesClicked: () -> Unit

    init {
        binding.yesButton.setOnClickListener {
            if (this::onYesClicked.isInitialized) {
                onYesClicked.invoke()
            } else {
                dismiss()
            }
        }

        binding.noButton.setOnClickListener {
            dismiss()
        }
    }

    fun setOnConfirmClicked(onConfirmClicked: () -> Unit) {
        this.onYesClicked = onConfirmClicked
    }

    fun showDialog(title: String, message: String) {
        binding.title = title
        binding.desc = message
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    fun dismiss() {
        if (alertDialog.isShowing) alertDialog.dismiss()
    }
}
