package com.andriawan.divergent_mobile_apps.utils

import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

object BindingAdapter {

    @BindingAdapter("errorText")
    @JvmStatic
    fun TextInputLayout.bindErrorText(errorText: String?) {
        if (errorText?.isNotEmpty() == true) {
            isErrorEnabled = true
            error =  errorText
        } else {
            error =   null
        }
    }

    @BindingAdapter("errorText")
    @JvmStatic
    fun TextInputEditText.bindErrorText(errorText: String?) {
        error = if (errorText?.isNotEmpty() == true) {
            errorText
        } else {
            null
        }
    }
}