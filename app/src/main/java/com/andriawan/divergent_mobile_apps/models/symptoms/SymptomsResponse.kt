package com.andriawan.divergent_mobile_apps.models.symptoms

import com.andriawan.divergent_mobile_apps.models.symptoms.response.Data
import com.andriawan.divergent_mobile_apps.models.symptoms.response.Meta

data class SymptomsResponse(
    val `data`: Data,
    val meta: Meta
)