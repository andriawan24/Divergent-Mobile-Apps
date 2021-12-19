package com.andriawan.divergent_mobile_apps.ui.home

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
import com.andriawan.divergent_mobile_apps.models.articles.ArticleResponse
import com.andriawan.divergent_mobile_apps.models.auth.response.LoginResponse
import com.andriawan.divergent_mobile_apps.models.auth.response.User
import com.andriawan.divergent_mobile_apps.ui.articles.NewsListener
import com.andriawan.divergent_mobile_apps.utils.Constants
import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.PREFERENCE_USER_PROFILE
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.andriawan.divergent_mobile_apps.utils.SharedPreferenceHelper
import com.andriawan.divergent_mobile_apps.utils.SingleEvents
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val sharedPreferenceHelper: SharedPreferenceHelper,
    private val repository: Repository
) : AndroidViewModel(application), HomeListener, NewsListener {

    private val _users = MutableLiveData<SingleEvents<User>>()
    val users: LiveData<SingleEvents<User>> = _users

    private val _showToast = MutableLiveData<SingleEvents<String>>()
    val showToast: LiveData<SingleEvents<String>> = _showToast

    private val _updateRecyclerView = MutableLiveData<SingleEvents<List<Article>>>()
    val updateRecyclerView: LiveData<SingleEvents<List<Article>>> = _updateRecyclerView

    private val _articleResponse = MutableLiveData<NetworkResult<ArticleResponse>>()
    val articleResponse: LiveData<NetworkResult<ArticleResponse>> = _articleResponse

    private val _goToProfile = MutableLiveData<SingleEvents<Boolean>>()
    val goToProfile: LiveData<SingleEvents<Boolean>> = _goToProfile

    private val _goToDiagnose = MutableLiveData<SingleEvents<Boolean>>()
    val goToDiagnose: LiveData<SingleEvents<Boolean>> = _goToDiagnose

    private val _goToArticles = MutableLiveData<SingleEvents<Boolean>>()
    val goToArticles: LiveData<SingleEvents<Boolean>> = _goToArticles

    private val _goToDiagnoseHistory = MutableLiveData<SingleEvents<Boolean>>()
    val goToDiagnoseHistory: LiveData<SingleEvents<Boolean>> = _goToDiagnoseHistory

    private val _goToWiki = MutableLiveData<SingleEvents<Boolean>>()
    val goToWiki: LiveData<SingleEvents<Boolean>> = _goToWiki

    private val _onClickNews = MutableLiveData<SingleEvents<Article>>()
    val onClickNews: LiveData<SingleEvents<Article>> = _onClickNews

    init {
        getArticles()
    }

    fun getUser() {
        Timber.d("Get User")
        _users.value = SingleEvents(
            Gson().fromJson(sharedPreferenceHelper.getString(PREFERENCE_USER_PROFILE), User::class.java)
        )
    }

    private fun getArticles() = viewModelScope.launch {
        val params = mapOf(
            "latest" to "true"
        )

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
                    _updateRecyclerView.postValue(SingleEvents(articles.take(3)))
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

    override fun goToEditProfile() {
        _goToProfile.postValue(SingleEvents(true))
    }

    override fun menuClicked(menuName: String) {
        when (menuName) {
            "diagnose" -> {
                _goToDiagnose.value = SingleEvents(true)
            }

            "history" -> {
                _goToDiagnoseHistory.value = SingleEvents(true)
            }

            "wiki" -> {
                _goToWiki.value = SingleEvents(true)
            }

            "article" -> {
                _goToArticles.postValue(SingleEvents(true))
            }
        }
    }

    override fun onNewsClicked(article: Article) {
        _onClickNews.value = SingleEvents(article)
    }
}