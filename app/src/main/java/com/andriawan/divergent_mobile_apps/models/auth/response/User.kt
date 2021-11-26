package com.andriawan.divergent_mobile_apps.models.auth.response

data class User(
    val created_at: String? = null,
    val current_team_id: Int? = null,
    val email: String,
    val email_verified_at: String? = null,
    val id: Int? = null,
    val name: String,
    val phone_number: String,
    val profile_photo_path: String? = null,
    val profile_photo_url: String,
    val updated_at: String? = null
)