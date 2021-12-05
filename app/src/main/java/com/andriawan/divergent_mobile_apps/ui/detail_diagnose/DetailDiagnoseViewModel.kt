package com.andriawan.divergent_mobile_apps.ui.detail_diagnose

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andriawan.divergent_mobile_apps.models.history_diagnose.Consultation
import javax.inject.Inject

class DetailDiagnoseViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    private val _diagnose = MutableLiveData<Consultation>()
    val diagnose: LiveData<Consultation> = _diagnose

    fun setDiagnose(consultation: Consultation) {
        _diagnose.value = consultation
    }
}