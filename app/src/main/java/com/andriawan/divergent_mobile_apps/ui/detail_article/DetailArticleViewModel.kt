package com.andriawan.divergent_mobile_apps.ui.detail_article

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andriawan.divergent_mobile_apps.models.articles.Article
import com.andriawan.divergent_mobile_apps.utils.SingleEvents
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailArticleViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    private val _article = MutableLiveData<SingleEvents<Article>>()
    val article: LiveData<SingleEvents<Article>> = _article

    fun setArticle(articleJson: String) {
        val article = Gson().fromJson(articleJson, Article::class.java)
        _article.value = SingleEvents(article)
    }
}