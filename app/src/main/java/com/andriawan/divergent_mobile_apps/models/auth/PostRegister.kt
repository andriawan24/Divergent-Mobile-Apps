package com.andriawan.divergent_mobile_apps.models.auth

data class PostRegister(
    val name: String,
    val email: String,
    val phone_number: String,
    val password: String,
    val password_confirmation: String,
)