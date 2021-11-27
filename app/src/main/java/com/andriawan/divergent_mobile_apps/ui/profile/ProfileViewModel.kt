package com.andriawan.divergent_mobile_apps.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andriawan.divergent_mobile_apps.models.auth.form.ErrorRegisterForm
import com.andriawan.divergent_mobile_apps.models.auth.response.User
import com.andriawan.divergent_mobile_apps.models.profile.form.ErrorProfileForm
import com.andriawan.divergent_mobile_apps.utils.Constants
import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.PREFERENCE_ACCESS_TOKEN
import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.PREFERENCE_IS_LOGGED_IN
import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.PREFERENCE_USER_PROFILE
import com.andriawan.divergent_mobile_apps.utils.SharedPreferenceHelper
import com.andriawan.divergent_mobile_apps.utils.SingleEvents
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val sharedPreferenceHelper: SharedPreferenceHelper,
): AndroidViewModel(application), ProfileListener {

    private val _errorForm = MutableLiveData<ErrorProfileForm>()
    val errorForm: LiveData<ErrorProfileForm> = _errorForm

    private val _users = MutableLiveData<SingleEvents<User>>()
    val users: LiveData<SingleEvents<User>> = _users

    private val _goToLoginPage = MutableLiveData<SingleEvents<Boolean>>()
    val goToLoginPage: LiveData<SingleEvents<Boolean>> = _goToLoginPage

    init {
        _users.value = SingleEvents(
            Gson().fromJson(sharedPreferenceHelper.getString(Constants.PREFERENCE_USER_PROFILE), User::class.java)
        )
    }

    override fun logoutClicked() {
        sharedPreferenceHelper.saveString(PREFERENCE_ACCESS_TOKEN, "")
        sharedPreferenceHelper.saveString(PREFERENCE_USER_PROFILE, "")
        sharedPreferenceHelper.saveBoolean(PREFERENCE_IS_LOGGED_IN, false)
        _goToLoginPage.postValue(SingleEvents(true))
    }
}