package com.andriawan.divergent_mobile_apps.utils

import android.content.Context
import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.SHARED_PREFERENCE_NAME
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferenceHelper(
    context: Context,
    private val gson: Gson
) {

    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String) = sharedPreferences.getString(key, "")

    fun saveInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String) = sharedPreferences.getInt(key, 0)

    fun saveBoolean(key: String, value: Boolean) =
        sharedPreferences.edit().putBoolean(key, value).apply()

    fun getBoolean(key: String) = sharedPreferences.getBoolean(key, false)

    fun remove(key: String) = sharedPreferences.edit().remove(key).apply()

    fun <T> saveObject(key: String, any: T) {
        sharedPreferences.edit().putString(key, gson.toJson(any)).apply()
    }

    fun <T> getObject(key: String, defaultObject: T? = null): T? {
        return try {
            val tt = object : TypeToken<T>() {}.type
            gson.fromJson<T>(sharedPreferences.getString(key, null), tt)
        } catch (exception: Exception) {
            defaultObject
        }
    }

    fun clear() = sharedPreferences.edit().clear().apply()
}