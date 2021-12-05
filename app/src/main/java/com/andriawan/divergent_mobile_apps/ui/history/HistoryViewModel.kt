package com.andriawan.divergent_mobile_apps.ui.history

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.andriawan.divergent_mobile_apps.data.Repository
import com.andriawan.divergent_mobile_apps.models.articles.Article
import com.andriawan.divergent_mobile_apps.models.articles.ArticleResponse
import com.andriawan.divergent_mobile_apps.models.history_diagnose.Consultation
import com.andriawan.divergent_mobile_apps.models.history_diagnose.HistoryDiagnoseResponse
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.andriawan.divergent_mobile_apps.utils.SingleEvents
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    application: Application,
    private val repository: Repository
): AndroidViewModel(application), HistoryListener {

    private val _models = MutableLiveData<NetworkResult<List<Consultation>>>()
    val models: LiveData<NetworkResult<List<Consultation>>> = _models

    private val _updateRecyclerView = MutableLiveData<SingleEvents<List<Consultation>>>()
    val updateRecyclerView: LiveData<SingleEvents<List<Consultation>>> = _updateRecyclerView

    private val _sortSelected = MutableLiveData<SingleEvents<String>>()
    val sortSelected: LiveData<SingleEvents<String>> = _sortSelected

    fun getHistories(sort: String = HistoryFragment.SORT_NEWEST) = viewModelScope.launch {
        getHistorySafeCall(sort)
    }

    private suspend fun getHistorySafeCall(params: String) {
        val hasConnection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hasConnectedInternet()
            } else {
                true
            }

        if (hasConnection) {
            _models.value = NetworkResult.Loading()
            try {
                val response = repository.remote.diagnoseHistories(params)
                _models.value = handleHistoryResponse(response)
            } catch (e: Exception) {
                _models.value = NetworkResult.Error("Something went wrong ${e.message}")
            }
        } else {
            _models.value = NetworkResult.Error("No internet connection")
        }
    }

    private fun handleHistoryResponse(response: Response<HistoryDiagnoseResponse>): NetworkResult<List<Consultation>> {
        return when {
            response.message().toString().contains("Timeout") -> {
                NetworkResult.Error("Connection time out")
            }

            response.isSuccessful -> {
                val loginResponse = response.body()
                loginResponse?.data?.consultations?.let { consultations ->
                    _updateRecyclerView.postValue(SingleEvents(consultations))
                }
                NetworkResult.Success(loginResponse?.data?.consultations!!)
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

    override fun onButtonClicked(view: View) {
        val button = view as MaterialButton
        _sortSelected.value = SingleEvents(button.text.toString())
    }
}