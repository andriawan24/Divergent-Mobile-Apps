package com.andriawan.divergent_mobile_apps.models.diagnose.response

data class Disease(
    val code: String,
    val created_at: String,
    val deleted_at: Any,
    val description: String,
    val id: Int,
    val name: String,
    val symptoms: List<Symptom>,
    val updated_at: String
)