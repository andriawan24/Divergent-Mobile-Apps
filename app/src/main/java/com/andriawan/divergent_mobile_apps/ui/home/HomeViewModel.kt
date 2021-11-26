package com.andriawan.divergent_mobile_apps.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andriawan.divergent_mobile_apps.models.auth.response.User
import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.PREFERENCE_USER_PROFILE
import com.andriawan.divergent_mobile_apps.utils.SharedPreferenceHelper
import com.andriawan.divergent_mobile_apps.utils.SingleEvents
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    sharedPreferenceHelper: SharedPreferenceHelper
) : AndroidViewModel(application) {

    private val _users = MutableLiveData<SingleEvents<User>>()
    val users: LiveData<SingleEvents<User>> = _users

    init {
        _users.value = SingleEvents(
            Gson().fromJson(sharedPreferenceHelper.getString(PREFERENCE_USER_PROFILE), User::class.java)
        )
    }
}