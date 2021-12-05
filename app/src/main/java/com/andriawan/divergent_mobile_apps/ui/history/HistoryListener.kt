package com.andriawan.divergent_mobile_apps.ui.history

import android.view.View
import com.andriawan.divergent_mobile_apps.models.history_diagnose.Consultation

interface HistoryListener {
    fun onButtonClicked(view: View)
    fun onModelClicked(consultation: Consultation)
}