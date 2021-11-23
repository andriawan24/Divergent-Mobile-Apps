package com.andriawan.divergent_mobile_apps.ui.on_boarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andriawan.divergent_mobile_apps.models.OnboardModel
import com.andriawan.divergent_mobile_apps.ui.on_boarding.Data.getOnboardData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    private val _models = MutableLiveData<List<OnboardModel>>()
    val models: LiveData<List<OnboardModel>> = _models

    private val _needToUpdateRecycler = MutableLiveData(false)
    val needToUpdateRecycler: LiveData<Boolean> = _needToUpdateRecycler

    init {
        _models.value = getOnboardData()
        _needToUpdateRecycler.value = true
    }
}