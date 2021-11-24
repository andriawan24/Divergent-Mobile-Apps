package com.andriawan.divergent_mobile_apps.models.auth.response

data class Data(
    val access_token: String,
    val token_type: String,
    val user: User
)