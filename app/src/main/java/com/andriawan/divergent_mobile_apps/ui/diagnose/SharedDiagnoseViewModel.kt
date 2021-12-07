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
import com.andriawan.divergent_mobile_apps.models.diagnose.PostDiagnose
import com.andriawan.divergent_mobile_apps.models.diagnose.form.DiagnoseErrorForm
import com.andriawan.divergent_mobile_apps.models.diagnose.response.DiagnoseResponse
import com.andriawan.divergent_mobile_apps.models.symptoms.SymptomsResponse
import com.andriawan.divergent_mobile_apps.models.symptoms.response.Symptom
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.andriawan.divergent_mobile_apps.utils.SingleEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SharedDiagnoseViewModel @Inject constructor(
    application: Application,
    private val repository: Repository
): AndroidViewModel(application) {

    private val _symptoms = MutableLiveData<NetworkResult<SymptomsResponse>>()
    val symptoms: LiveData<NetworkResult<SymptomsResponse>> = _symptoms

    private val _diagnose = MutableLiveData<NetworkResult<DiagnoseResponse>>()
    val diagnose: LiveData<NetworkResult<DiagnoseResponse>> = _diagnose

    private val _errorForm = MutableLiveData<DiagnoseErrorForm>()
    val errorForm: LiveData<DiagnoseErrorForm> = _errorForm

    private val _showToast = MutableLiveData<SingleEvents<String>>()
    val showToast: LiveData<SingleEvents<String>> = _showToast

    private val _cancelClicked = MutableLiveData<SingleEvents<Boolean>>()
    val cancelClicked: LiveData<SingleEvents<Boolean>> = _cancelClicked

    private val _nextClicked = MutableLiveData<SingleEvents<Boolean>>()
    val nextClicked: LiveData<SingleEvents<Boolean>> = _nextClicked

    private val _submitClicked = MutableLiveData<SingleEvents<Boolean>>()
    val submitClicked: LiveData<SingleEvents<Boolean>> = _submitClicked

    private val _postModel = MutableLiveData(PostDiagnose())
    val postModel: LiveData<PostDiagnose> = _postModel

    val _listClicked = mutableListOf<Int>()
    val _listUnClicked = mutableListOf<Int>()

    init {
        _errorForm.value = DiagnoseErrorForm()
    }

    fun answerYes(symptomsId: Int) {
        if (!_listClicked.contains(symptomsId)) {
            _listClicked.add(symptomsId)
            if (_listUnClicked.contains(symptomsId)) {
                _listUnClicked.remove(symptomsId)
            }
        }
        Timber.d("List clicked id $_listClicked list unclicked id $_listUnClicked")
    }

    fun answerNo(symptomsId: Int) {
        if (!_listUnClicked.contains(symptomsId)) {
            _listUnClicked.add(symptomsId)
            if (_listClicked.contains(symptomsId)) {
                _listClicked.remove(symptomsId)
            }
        }
        Timber.d("List clicked id $_listClicked list unclicked id $_listUnClicked")
    }

    fun cancel() {
        _cancelClicked.value = SingleEvents(true)
    }

    fun showToast(msg: String) {
        _showToast.value = SingleEvents(msg)
    }

    fun next(isSubmit: Boolean = false) {
        if (isSubmit) {
            _submitClicked.value = SingleEvents(true)
        } else {
            _nextClicked.value = SingleEvents(true)
        }
    }

    fun submitDiagnose() = viewModelScope.launch {
        _postModel.value?.let { postMod ->
            postMod.symptoms = _listClicked
            submitDiagnoseSafeCall(postMod)
        }?:kotlin.run {
            _showToast.value = SingleEvents("Failed to submit")
        }
    }

    private suspend fun submitDiagnoseSafeCall(postMod: PostDiagnose) {
        val hasConnection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hasConnectedInternet()
            } else {
                true
            }

        if (hasConnection) {
            _diagnose.value = NetworkResult.Loading()
            try {
                val response = repository.remote.diagnose(postMod)
                _diagnose.value = handleDiagnoseResponse(response)
            } catch (e: Exception) {
                _diagnose.value = NetworkResult.Error("Something went wrong ${e.message}")
            }
        } else {
            _diagnose.value = NetworkResult.Error("No internet connection")
        }
    }

    private fun handleDiagnoseResponse(response: Response<DiagnoseResponse>): NetworkResult<DiagnoseResponse> {
        return when {
            response.message().toString().contains("Timeout") -> {
                NetworkResult.Error("Connection time out")
            }

            response.isSuccessful -> {
                val loginResponse = response.body()
                NetworkResult.Success(loginResponse!!)
            }

            else -> {
                NetworkResult.Error(response.message())
            }
        }
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
                _postModel.value?.let { postMod ->
                    postMod.name = name
                    postMod.age = age.toInt()

                    _postModel.value = postMod
                }

                _nextClicked.value = SingleEvents(true)
            }
        }
    }
}