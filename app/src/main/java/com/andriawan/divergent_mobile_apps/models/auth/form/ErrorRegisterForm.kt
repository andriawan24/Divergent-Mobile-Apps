package com.andriawan.divergent_mobile_apps.models.auth.form

data class ErrorRegisterForm(
    var name: String? = "",
    var email: String? = "",
    var phone: String? = "",
    var password: String? = "",
    var passwordConfirmation: String? = ""
)