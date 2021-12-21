package com.andriawan.divergent_mobile_apps.ui.wiki

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
import com.andriawan.divergent_mobile_apps.models.symptoms.SymptomsResponse
import com.andriawan.divergent_mobile_apps.models.symptoms.response.Symptom
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.andriawan.divergent_mobile_apps.utils.SingleEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class WikiViewModel @Inject constructor(
    application: Application,
    private val repository: Repository
): AndroidViewModel(application), WikiListener {

    private val _wikiResponse = MutableLiveData<NetworkResult<SymptomsResponse>>()
    val wikiResponse: LiveData<NetworkResult<SymptomsResponse>> = _wikiResponse

    private val _onClickSymptom = MutableLiveData<SingleEvents<Symptom>>()
    val onClickSymptom: LiveData<SingleEvents<Symptom>> = _onClickSymptom

    private val _showToast = MutableLiveData<SingleEvents<String>>()
    val showToast: LiveData<SingleEvents<String>> = _showToast

    private val _updateRecyclerView = MutableLiveData<SingleEvents<List<Symptom>>>()
    val updateRecyclerView: LiveData<SingleEvents<List<Symptom>>> = _updateRecyclerView

    private val _searchWiki = MutableLiveData<String>()
    val searchWiki: LiveData<String> = _searchWiki

    init {
        getArticles()
    }

    private fun getArticles(searchQuery: String? = null) = viewModelScope.launch {
        val query = searchQuery ?: ""

        val params = mutableMapOf(
            "search" to query
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
            _wikiResponse.value = NetworkResult.Loading()
            try {
                val response = repository.remote.getSymptoms(params)
                _wikiResponse.value = handleSymptomsResponse(response)
            } catch (e: Exception) {
                _wikiResponse.value = NetworkResult.Error("Something went wrong ${e.message}")
            }
        } else {
            _wikiResponse.value = NetworkResult.Error("No internet connection")
        }
    }

    private fun handleSymptomsResponse(response: Response<SymptomsResponse>): NetworkResult<SymptomsResponse> {
        return when {
            response.message().toString().contains("Timeout") -> {
                NetworkResult.Error("Connection time out")
            }

            response.isSuccessful -> {
                val loginResponse = response.body()
                loginResponse?.data?.symptoms?.let { wiki ->
                    _updateRecyclerView.postValue(SingleEvents(wiki))
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

    override fun onSearchSubmitted(search: String) {
        getArticles(search)
    }

    override fun onCardClicked(wiki: Symptom) {
        _onClickSymptom.value = SingleEvents(wiki)
    }
}