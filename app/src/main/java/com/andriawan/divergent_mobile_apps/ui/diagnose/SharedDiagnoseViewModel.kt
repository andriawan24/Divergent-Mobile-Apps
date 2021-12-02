package com.andriawan.divergent_mobile_apps.ui.diagnose

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
import com.andriawan.divergent_mobile_apps.models.diagnose.form.DiagnoseErrorForm
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
class SharedDiagnoseViewModel @Inject constructor(
    application: Application,
    private val repository: Repository
): AndroidViewModel(application) {

    private val _symptoms = MutableLiveData<NetworkResult<SymptomsResponse>>()
    val symptoms: LiveData<NetworkResult<SymptomsResponse>> = _symptoms

    private val _errorForm = MutableLiveData<DiagnoseErrorForm>()
    val errorForm: LiveData<DiagnoseErrorForm> = _errorForm

    private val _showToast = MutableLiveData<SingleEvents<String>>()
    val showToast: LiveData<SingleEvents<String>> = _showToast

    init {
        _errorForm.value = DiagnoseErrorForm()
    }

    fun getSymptoms() = viewModelScope.launch {
        getSymptomsSafeCall()
    }

    private suspend fun getSymptomsSafeCall() {
        val hasConnection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hasConnectedInternet()
            } else {
                true
            }

        if (hasConnection) {
            _symptoms.value = NetworkResult.Loading()
            try {
                val response = repository.remote.getSymptoms()
                _symptoms.value = handleSymptomsResponse(response)
            } catch (e: Exception) {
                _symptoms.value = NetworkResult.Error("Something went wrong ${e.message}")
            }
        } else {
            _symptoms.value = NetworkResult.Error("No internet connection")
        }
    }

    private fun handleSymptomsResponse(response: Response<SymptomsResponse>): NetworkResult<SymptomsResponse> {
        return when {
            response.message().toString().contains("Timeout") -> {
                NetworkResult.Error("Connection time out")
            }

            response.isSuccessful -> {
                val loginResponse = response.body()
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

    fun onNextIntro(
        name: String,
        age: String
    ) {
        _errorForm.value?.let {
            it.name = ""
            it.age = ""

            var valid = true

            if (name.isEmpty()) {
                valid = false
                it.name = "Enter your kid's name"
            }

            if (age.isEmpty()) {
                valid = false
                it.age = "Enter your kid's age"
            }

            _errorForm.value = it

            if (valid) {
                _showToast.value = SingleEvents("Next Success")
            }
        }
    }
}