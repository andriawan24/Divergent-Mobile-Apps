package com.andriawan.divergent_mobile_apps.utils

import android.content.res.Resources

fun Int.toDp(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()