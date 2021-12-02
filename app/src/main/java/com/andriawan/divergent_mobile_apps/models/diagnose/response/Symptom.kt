package com.andriawan.divergent_mobile_apps.models.diagnose.response

data class Symptom(
    val created_at: String,
    val deleted_at: Any,
    val diseases_id: String,
    val id: Int,
    val mb: String,
    val md: String,
    val symptom: SymptomDisease,
    val symptoms_id: String,
    val updated_at: String
)