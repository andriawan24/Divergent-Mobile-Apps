package com.andriawan.divergent_mobile_apps.models.profile.response

import com.andriawan.divergent_mobile_apps.models.auth.response.Meta

data class UpdateProfileResponse(
    val `data`: Data,
    val meta: Meta
)