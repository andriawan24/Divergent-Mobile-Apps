package com.andriawan.divergent_mobile_apps.models.history_diagnose

data class Consultation(
    val age: String,
    val created_at: String,
    val deleted_at: Any,
    val id: Int,
    val name: String,
    val results: List<Result>,
    val updated_at: String,
    val users_id: String
)