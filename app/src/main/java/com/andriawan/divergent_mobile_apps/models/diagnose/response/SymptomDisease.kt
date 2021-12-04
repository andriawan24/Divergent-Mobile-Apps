package com.andriawan.divergent_mobile_apps.models.diagnose.response

data class SymptomDisease(
    val code: String,
    val created_at: String,
    val deleted_at: String,
    val description: String,
    val id: Int,
    val name: String,
    val question: String,
    val updated_at: String
)