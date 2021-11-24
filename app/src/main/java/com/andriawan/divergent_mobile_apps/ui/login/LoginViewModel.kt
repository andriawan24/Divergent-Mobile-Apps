package com.andriawan.divergent_mobile_apps.ui.login

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
import com.andriawan.divergent_mobile_apps.models.login.PostLogin
import com.andriawan.divergent_mobile_apps.models.login.response.LoginResponse
import com.andriawan.divergent_mobile_apps.models.login.response.User
import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.PREFERENCE_ACCESS_TOKEN
import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.PREFERENCE_IS_LOGGED_IN
import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.PREFERENCE_USER_PROFILE
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.andriawan.divergent_mobile_apps.utils.SharedPreferenceHelper
import com.andriawan.divergent_mobile_apps.utils.SingleEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val repository: Repository,
    private val sharedPreferenceHelper: SharedPreferenceHelper
): AndroidViewModel(application), LoginListener {

    private val _goToMainPage = MutableLiveData<SingleEvents<Boolean>>()
    val goToMainPage: LiveData<SingleEvents<Boolean>> = _goToMainPage

    private val _loginResponse = MutableLiveData<NetworkResult<LoginResponse>>()
    val loginResponse: LiveData<NetworkResult<LoginResponse>> = _loginResponse

    init {
        val isLoggedIn = sharedPreferenceHelper.getBoolean(PREFERENCE_IS_LOGGED_IN)
        if (isLoggedIn) {
            _goToMainPage.value = SingleEvents(true)
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        val postLogin = PostLogin(email, password)
        loginSafeCall(postLogin)
    }

    private suspend fun loginSafeCall(postLogin: PostLogin) {
        val hasConnection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hasConnectedInternet()
            } else {
                true
            }

        if (hasConnection) {
            _loginResponse.value = NetworkResult.Loading()
            try {
                val response = repository.remote.login(postLogin)
                _loginResponse.value = handleLoginResponse(response)

                response.body()?.data?.user?.let {
                    updateUserData(it)
                }

                response.body()?.data?.access_token?.let {
                    sharedPreferenceHelper.saveString(PREFERENCE_ACCESS_TOKEN, it)
                }
            } catch (e: Exception) {
                _loginResponse.value = NetworkResult.Error("Something went wrong ${e.message}")
            }
        } else {
            _loginResponse.value = NetworkResult.Error("No internet connection")
        }
    }

    private fun handleLoginResponse(response: Response<LoginResponse>): NetworkResult<LoginResponse> {
        return when {
            response.message().toString().contains("Timeout") -> {
                NetworkResult.Error("Connection time out")
            }

            response.isSuccessful -> {
                sharedPreferenceHelper.saveBoolean(PREFERENCE_IS_LOGGED_IN, true)
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

    private fun updateUserData(userData: User) {
        sharedPreferenceHelper.saveObject(PREFERENCE_USER_PROFILE, userData)
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

    override fun onSubmitClicked(email: String, password: String) {
        if (email.isNotEmpty() and password.isNotEmpty()) {
            login(email, password)
        }
    }

    override fun toRegisterClicked() {
        // no ops yet
    }
}