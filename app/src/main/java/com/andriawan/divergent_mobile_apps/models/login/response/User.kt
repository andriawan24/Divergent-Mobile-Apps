package com.andriawan.divergent_mobile_apps.models.login.response

data class User(
    val created_at: Any,
    val current_team_id: Any,
    val email: String,
    val email_verified_at: Any,
    val id: Int,
    val name: String,
    val phone_number: String,
    val profile_photo_path: Any,
    val profile_photo_url: String,
    val updated_at: Any
)