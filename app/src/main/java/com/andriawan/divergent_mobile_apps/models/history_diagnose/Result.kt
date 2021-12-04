package com.andriawan.divergent_mobile_apps.models.history_diagnose

data class Result(
    val consultants_id: String,
    val created_at: String,
    val disease: Disease,
    val diseases_id: String,
    val id: Int,
    val possibility: String,
    val updated_at: String
)