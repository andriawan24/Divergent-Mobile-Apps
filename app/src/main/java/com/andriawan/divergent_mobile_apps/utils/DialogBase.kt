package com.andriawan.divergent_mobile_apps.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.databinding.CustomDialogBaseBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogBase(context: Context, layoutInflater: LayoutInflater) {

    private val binding: CustomDialogBaseBinding = CustomDialogBaseBinding.inflate(layoutInflater)
    private var alertDialog: AlertDialog =
        MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_rounded)
            .setView(binding.root)
            .create()

    lateinit var onConfirmClick: () -> Unit
    lateinit var dialogState: String

    init {
        binding.btnConfirm.setOnClickListener {
            if (this::onConfirmClick.isInitialized) {
                onConfirmClick.invoke()
            } else {
                dismiss()
            }
        }
    }

    fun setOnConfirmClicked(onConfirmClicked: () -> Unit) {
        this.onConfirmClick = onConfirmClicked
    }

    private fun showLoading() {
        binding.desc = "Please Wait"
        binding.showProgress = true
        alertDialog.show()
    }

    private fun showSuccess(successMessage: String) {
        binding.title = "Success"
        binding.desc = successMessage
        binding.showProgress = false
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    private fun showError(errorMessage: String) {
        binding.title = "Error"
        binding.desc = errorMessage
        binding.showProgress = false
        if (!alertDialog.isShowing)
            alertDialog.show()
    }

    fun updateState(pair : Pair<String, String?>){
        this.dialogState = pair.first
        when (pair.first) {
            SUCCESS -> {
                showSuccess(pair.second + "")
            }

            ERROR -> {
                showError(pair.second + "")
            }

            LOADING -> {
                showLoading()
            }

            else -> {
                dismiss()
            }
        }
    }

    fun dismiss() {
        if (alertDialog.isShowing) alertDialog.dismiss()
    }

    fun setButtonText(s: String) {
        binding.btnConfirm.text = s
    }

    companion object{
        const val SUCCESS = "SUCCESS"
        const val ERROR = "ERROR"
        const val LOADING = "LOADING"
    }
}