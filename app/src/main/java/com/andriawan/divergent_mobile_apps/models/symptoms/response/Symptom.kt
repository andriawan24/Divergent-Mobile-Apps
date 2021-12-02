package com.andriawan.divergent_mobile_apps.models.symptoms.response

data class Symptom(
    val code: String,
    val created_at: String,
    val deleted_at: Any,
    val description: String,
    val id: Int,
    val name: String,
    val question: String,
    val updated_at: String
)