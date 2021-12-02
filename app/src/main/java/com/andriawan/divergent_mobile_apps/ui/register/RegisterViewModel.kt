package com.andriawan.divergent_mobile_apps.ui.register

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.andriawan.divergent_mobile_apps.data.Repository
import com.andriawan.divergent_mobile_apps.models.auth.PostLogin
import com.andriawan.divergent_mobile_apps.models.auth.PostRegister
import com.andriawan.divergent_mobile_apps.models.auth.form.ErrorRegisterForm
import com.andriawan.divergent_mobile_apps.models.auth.response.LoginResponse
import com.andriawan.divergent_mobile_apps.models.auth.response.User
import com.andriawan.divergent_mobile_apps.utils.Constants
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
class RegisterViewModel @Inject constructor(
    application: Application,
    private val sharedPreferenceHelper: SharedPreferenceHelper,
    private val repository: Repository
): AndroidViewModel(application), RegisterListener {

    private val _errorForm = MutableLiveData<ErrorRegisterForm>()
    val errorForm: LiveData<ErrorRegisterForm> = _errorForm

    private val _showToast = MutableLiveData<SingleEvents<Boolean>>()
    val showToast: LiveData<SingleEvents<Boolean>> = _showToast

    private val _goToLogin = MutableLiveData<SingleEvents<Boolean>>()
    val goToLogin: LiveData<SingleEvents<Boolean>> = _goToLogin

    private val _registerResponse = MutableLiveData<NetworkResult<LoginResponse>>()
    val registerResponse: LiveData<NetworkResult<LoginResponse>> = _registerResponse

    init {
        _errorForm.value = ErrorRegisterForm()
    }

    fun onSubmitClicked(
        name: String,
        email: String,
        phoneNumber: String,
        password: String,
        passwordConfirmation: String
    ) {
        Timber.d("Go here")
        errorForm.value?.let {
            Timber.d("Go to submit")
            it.name = ""
            it.email = ""
            it.phone = ""
            it.password = ""
            it.password = ""

            var valid = true

            if (name.isEmpty()) {
                it.name = "Please fill name field"
                valid = false
            }

            if (email.isEmpty()) {
                valid = false
                it.email = "Please fill email field"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                valid = false
                it.email = "Enter a valid email"
            }

            if (phoneNumber.isEmpty()) {
                it.phone = "Please fill phone number field"
                valid = false
            }

            when {
                password.isEmpty() -> {
                    it.password = "Please fill password field"
                    valid = false
                }

                password.length !in 13 downTo 7 -> {
                    it.password = "Password must be between 8-12 character"
                    valid = false
                }

                !password.matches(Regex(".*\\d.*")) -> {
                    it.password = "Password must contain number"
                    valid = false
                }
            }

            if (passwordConfirmation.isEmpty()) {
                it.passwordConfirmation = "Please fill password confirmation field"
                valid = false
            } else if (passwordConfirmation != password) {
                it.passwordConfirmation = "Confirmation password not valid"
            }

            _errorForm.value = it

            Timber.d("Is valid $valid data error ${errorForm.value}")

            if (valid) {
                register(name, email, phoneNumber, password, passwordConfirmation)
            }
        }
    }

    private fun register(name: String, email: String, phoneNumber: String, password: String, passwordConfirmation: String) = viewModelScope.launch {
        val postRegister = PostRegister(name, email, phoneNumber, password, passwordConfirmation)
        registerSafeCall(postRegister)
    }

    private suspend fun registerSafeCall(postRegister: PostRegister) {
        val hasConnection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hasConnectedInternet()
            } else {
                true
            }

        if (hasConnection) {
            _registerResponse.value = NetworkResult.Loading()
            try {
                val response = repository.remote.register(postRegister)
                _registerResponse.value = handleLoginResponse(response)

                response.body()?.data?.user?.let {
                    updateUserData(it)
                }

                response.body()?.data?.access_token?.let {
                    sharedPreferenceHelper.saveString(Constants.PREFERENCE_ACCESS_TOKEN, it)
                }
            } catch (e: Exception) {
                _registerResponse.value = NetworkResult.Error("Something went wrong ${e.message}")
            }
        } else {
            _registerResponse.value = NetworkResult.Error("No internet connection")
        }
    }

    private fun handleLoginResponse(response: Response<LoginResponse>): NetworkResult<LoginResponse> {
        return when {
            response.message().toString().contains("Timeout") -> {
                NetworkResult.Error("Connection time out")
            }

            response.isSuccessful -> {
                sharedPreferenceHelper.saveBoolean(Constants.PREFERENCE_IS_LOGGED_IN, true)
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
                NetworkResult.Error("Email is already taken")
            }
        }
    }

    private fun updateUserData(userData: User) {
        sharedPreferenceHelper.saveObject(Constants.PREFERENCE_USER_PROFILE, userData)
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

    override fun goToLogin() {
        TODO("Not yet implemented")
    }
}