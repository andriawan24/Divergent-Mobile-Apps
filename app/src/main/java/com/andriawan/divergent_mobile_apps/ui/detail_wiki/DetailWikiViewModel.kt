package com.andriawan.divergent_mobile_apps.ui.detail_wiki

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andriawan.divergent_mobile_apps.models.symptoms.response.Symptom
import com.andriawan.divergent_mobile_apps.utils.SingleEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailWikiViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    private val _wikiData = MutableLiveData<Symptom>()
    val wikiData: LiveData<Symptom> = _wikiData

    private val _updateWiki = MutableLiveData<SingleEvents<Symptom>>()
    val updateWiki: LiveData<SingleEvents<Symptom>> = _updateWiki

    fun setWikiData(wiki: Symptom?) {
        wiki?.let {
            _wikiData.value = it

            _updateWiki.value = SingleEvents(it)
        }
    }
}