package com.andriawan.divergent_mobile_apps.models.articles

data class Article(
    val article_categories_id: String,
    val author: String,
    val created_at: String,
    val deleted_at: String,
    val description: String,
    val id: Int,
    val thumbnail_image: String? = null,
    val published_at: String,
    val publisher: String,
    val title: String,
    val updated_at: String
)