package com.andriawan.divergent_mobile_apps.models.profile

data class PostProfile(
    val name: String? = null,
    val email: String? = null,
    val phone_number: String? = null,
    var password: String? = null,
    var image: String? = null
)