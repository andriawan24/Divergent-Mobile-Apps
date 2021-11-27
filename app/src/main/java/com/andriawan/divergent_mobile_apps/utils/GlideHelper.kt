package com.andriawan.divergent_mobile_apps.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.andriawan.divergent_mobile_apps.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object GlideHelper {
    fun showThumbnail(url: String, imageView: ImageView, context: Context) {
        val options = RequestOptions().centerCrop()

        val requestBuilder = Glide.with(context)
            .load(url)

        requestBuilder
            .apply(options)
            .into(imageView)
    }

    fun showThumbnail(uri: Uri, imageView: ImageView, context: Context) {
        val options = RequestOptions().centerCrop()

        val requestBuilder = Glide.with(context)
            .load(uri)

        requestBuilder
            .apply(options)
            .into(imageView)
    }

    fun showImageProfile(url: String?, imageView: ImageView, context: Context) {
        val options = RequestOptions().centerCrop().circleCrop()
            .placeholder(R.drawable.ic_profile_default)
            .error(R.drawable.ic_profile_default)

        val requestBuilder = Glide.with(context)
            .load(url)

        requestBuilder
            .apply(options)
            .into(imageView)
    }
}