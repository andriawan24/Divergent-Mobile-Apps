package com.andriawan.divergent_mobile_apps.ui.articles

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.andriawan.divergent_mobile_apps.data.Repository
import com.andriawan.divergent_mobile_apps.models.articles.Article
import com.andriawan.divergent_mobile_apps.models.articles.ArticleCategory
import com.andriawan.divergent_mobile_apps.models.articles.ArticleResponse
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.andriawan.divergent_mobile_apps.utils.SingleEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    application: Application,
    private val repository: Repository
): AndroidViewModel(application), NewsListener {

    private val _articleResponse = MutableLiveData<NetworkResult<ArticleResponse>>()
    val articleResponse: LiveData<NetworkResult<ArticleResponse>> = _articleResponse

    private val _onClickNews = MutableLiveData<SingleEvents<Article>>()
    val onClickNews: LiveData<SingleEvents<Article>> = _onClickNews

    private val _showToast = MutableLiveData<SingleEvents<String>>()
    val showToast: LiveData<SingleEvents<String>> = _showToast

    private val _updateRecyclerView = MutableLiveData<SingleEvents<Pair<List<Article>, List<Article>>>>()
    val updateRecyclerView: LiveData<SingleEvents<Pair<List<Article>, List<Article>>>> = _updateRecyclerView

    private val _updateArticleCategories = MutableLiveData<SingleEvents<List<ArticleCategory>>>()
    val updateArticleCategories: LiveData<SingleEvents<List<ArticleCategory>>> = _updateArticleCategories

    init {
        getArticles()
    }

    fun getArticles(categories_id: String? = null) = viewModelScope.launch {
        val params = mutableMapOf(
            "latest" to "false"
        )

        categories_id?.let {
            if (it != "all") {
                params["categories_id"] = it
            }
        }

        getArticlesSafeCall(params)
    }

    private suspend fun getArticlesSafeCall(params: Map<String, String>) {
        val hasConnection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hasConnectedInternet()
            } else {
                true
            }

        if (hasConnection) {
            _articleResponse.value = NetworkResult.Loading()
            try {
                val response = repository.remote.getArticles(params)
                _articleResponse.value = handleArticleResponse(response)
            } catch (e: Exception) {
                _articleResponse.value = NetworkResult.Error("Something went wrong ${e.message}")
            }
        } else {
            _articleResponse.value = NetworkResult.Error("No internet connection")
        }
    }

    private fun handleArticleResponse(response: Response<ArticleResponse>): NetworkResult<ArticleResponse>? {
        return when {
            response.message().toString().contains("Timeout") -> {
                NetworkResult.Error("Connection time out")
            }

            response.isSuccessful -> {
                val loginResponse = response.body()
                loginResponse?.data?.articles?.let { articles ->
                    val breakingNews = articles.take(3)
                    _updateRecyclerView.postValue(SingleEvents(Pair(breakingNews, articles)))
                }
                loginResponse?.data?.article_categories?.let { categories ->
                    _updateArticleCategories.postValue(SingleEvents(categories))
                }
                NetworkResult.Success(loginResponse!!)
            }

            response.message().toString().contains("Bad Request") -> {
                NetworkResult.Error("Recheck your email and password")
            }

            response.code() == 500 -> {
                NetworkResult.Error("Authentication Error")
            }

            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasConnectedInternet(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    override fun onNewsClicked(article: Article) {
        _onClickNews.value = SingleEvents(article)
    }
}