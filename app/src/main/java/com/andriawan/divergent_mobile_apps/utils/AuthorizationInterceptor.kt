package com.andriawan.divergent_mobile_apps.utils

import android.content.Context
import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.PREFERENCE_ACCESS_TOKEN
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(
    val context: Context,
    private val sharedPreferenceHelper: SharedPreferenceHelper
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = sharedPreferenceHelper.getString(PREFERENCE_ACCESS_TOKEN)
        val request = chain.request().newBuilder()
        request.addHeader("Accept", "application/json")

        if (accessToken != null && accessToken.isNotEmpty()) {
            request.addHeader("Authorization", accessToken)
        }

        return chain.proceed(request.build())
    }
}