package com.andriawan.divergent_mobile_apps.ui.login

interface LoginListener {
    fun onSubmitClicked(email: String, password: String)
    fun toRegisterClicked()
}