package com.andriawan.divergent_mobile_apps.ui.onboard

import com.andriawan.divergent_mobile_apps.R
import com.andriawan.divergent_mobile_apps.models.OnboardModel

object Data {
    fun getOnboardData(): List<OnboardModel> {
        return listOf(
            OnboardModel(R.string.title_slider_1, R.string.subtitle_slider_1, R.drawable.slider_1),
            OnboardModel(R.string.title_slider_2, R.string.subtitle_slider_2, R.drawable.slider_2),
            OnboardModel(R.string.title_slider_3, R.string.subtitle_slider_3, R.drawable.slider_3),
        )
    }
}