package com.andriawan.divergent_mobile_apps.ui.onboard

import android.app.Application
import androidx.lifecycle.*
import com.andriawan.divergent_mobile_apps.models.OnboardModel
import com.andriawan.divergent_mobile_apps.ui.onboard.Data.getOnboardData
import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.PREFERENCE_IS_FIRST_INSTALLED
import com.andriawan.divergent_mobile_apps.utils.SharedPreferenceHelper
import com.andriawan.divergent_mobile_apps.utils.SingleEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardViewModel @Inject constructor(
    application: Application,
    private val sharedPreferenceHelper: SharedPreferenceHelper
): AndroidViewModel(application), OnboardListener {

    private val _needToUpdateRecycler = MutableLiveData<SingleEvents<List<OnboardModel>>>()
    val needToUpdateRecycler: LiveData<SingleEvents<List<OnboardModel>>> = _needToUpdateRecycler

    private val _goToLoginPage = MutableLiveData<SingleEvents<Boolean>>()
    val goToLoginPage: LiveData<SingleEvents<Boolean>> = _goToLoginPage

    private val _goToRegisterPage = MutableLiveData<SingleEvents<Boolean>>()
    val goToRegisterPage: LiveData<SingleEvents<Boolean>> = _goToRegisterPage

    private val _nextClicked = MutableLiveData<SingleEvents<Boolean>>()
    val nextClicked: LiveData<SingleEvents<Boolean>> = _nextClicked

    private val _skipClicked = MutableLiveData<SingleEvents<Boolean>>()
    val skipClicked: LiveData<SingleEvents<Boolean>> = _skipClicked

    private val _prevClicked = MutableLiveData<SingleEvents<Boolean>>()
    val prevClicked: LiveData<SingleEvents<Boolean>> = _prevClicked

    init {
        _needToUpdateRecycler.value = SingleEvents(getOnboardData())

        val isFirstTime = sharedPreferenceHelper.getBoolean(PREFERENCE_IS_FIRST_INSTALLED)
        if (isFirstTime) {
            _goToLoginPage.value = SingleEvents(true)
        }
    }

    private fun updateFirstTimeInstalled() {
        sharedPreferenceHelper.saveBoolean(PREFERENCE_IS_FIRST_INSTALLED, true)
    }

    override fun onLoginClicked() {
        updateFirstTimeInstalled()
        _goToLoginPage.value = SingleEvents(true)
    }

    override fun onRegisterClicked() {
        updateFirstTimeInstalled()
        _goToRegisterPage.value = SingleEvents(true)
    }

    override fun onSkipClicked() {
        updateFirstTimeInstalled()
        _skipClicked.value = SingleEvents(true)
    }

    override fun onNextClicked() {
        _nextClicked.value = SingleEvents(true)
    }

    override fun onPrevClicked() {
        _prevClicked.value = SingleEvents(true)
    }
}