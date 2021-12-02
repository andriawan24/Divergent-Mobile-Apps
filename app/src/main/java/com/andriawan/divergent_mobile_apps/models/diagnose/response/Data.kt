package com.andriawan.divergent_mobile_apps.models.diagnose.response

data class Data(
    val age: Int,
    val name: String,
    val diseases_possibilities: List<DiseasesPossibility>
)