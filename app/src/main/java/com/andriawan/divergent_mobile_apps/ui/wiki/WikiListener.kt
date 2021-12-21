package com.andriawan.divergent_mobile_apps.ui.wiki

import com.andriawan.divergent_mobile_apps.models.symptoms.response.Symptom

interface WikiListener {
    fun onSearchSubmitted(search: String)
    fun onCardClicked(wiki: Symptom)
}