package com.andriawan.divergent_mobile_apps.utils

import android.graphics.drawable.Drawable
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.andriawan.divergent_mobile_apps.R
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

    @BindingAdapter("imageProfile")
    @JvmStatic
    fun ImageView.bindImageProfile(imageViewUrl: String?) {
        GlideHelper.showImageProfile(imageViewUrl, this, context)
    }

    @BindingAdapter("imageViewUrl")
    @JvmStatic
    fun ImageView.bindImageViewUrl(imageViewUrl: String?) {
        imageViewUrl?.let {
            GlideHelper.showThumbnail(it, this, context)
        }
    }

    @BindingAdapter("imageViewUri")
    @JvmStatic
    fun ImageView.bindImageViewUrl(uri: Uri?) {
        uri?.let {
            val resolver = context.contentResolver
            val mime = MimeTypeMap.getSingleton()
            when (mime.getExtensionFromMimeType(resolver.getType(uri))) {
                "jpg", "jpeg", "png" -> {
                    GlideHelper.showThumbnail(it, this, context)
                }
                else -> {
                    this.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.img_default))
                }
            }
        }
    }

    @BindingAdapter("imageViewResource")
    @JvmStatic
    fun ImageView.bindImageViewResource(imageViewResource: Drawable) {
        this.setImageDrawable(imageViewResource)
    }
}