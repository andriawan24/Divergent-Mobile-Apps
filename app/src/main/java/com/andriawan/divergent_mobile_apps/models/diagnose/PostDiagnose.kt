package com.andriawan.divergent_mobile_apps.models.diagnose

data class PostDiagnose(
    var name: String = "",
    var age: Int = 0,
    var symptoms: List<Int> = emptyList()
)