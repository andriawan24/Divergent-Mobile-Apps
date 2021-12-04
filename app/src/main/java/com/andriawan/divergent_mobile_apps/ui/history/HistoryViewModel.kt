package com.andriawan.divergent_mobile_apps.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.andriawan.divergent_mobile_apps.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    application: Application,
    private val repository: Repository
): AndroidViewModel(application) {


}