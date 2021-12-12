package com.andriawan.divergent_mobile_apps.ui.articles

import com.andriawan.divergent_mobile_apps.models.articles.Article

interface NewsListener {
    fun onNewsClicked(article: Article)
}