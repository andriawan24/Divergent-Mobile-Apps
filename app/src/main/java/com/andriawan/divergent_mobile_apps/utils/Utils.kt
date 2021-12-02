package com.andriawan.divergent_mobile_apps.utils

import android.graphics.Bitmap
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import java.io.ByteArrayOutputStream

object Utils {

    fun getImageString(bitmap: Bitmap): String? {
        val encodedImage: String?
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val imageBytes: ByteArray = baos.toByteArray()
        encodedImage = encodeToString(imageBytes, DEFAULT)
        return encodedImage
    }
}