package com.andriawan.divergent_mobile_apps.ui.profile

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
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
import com.andriawan.divergent_mobile_apps.models.auth.PostRegister
import com.andriawan.divergent_mobile_apps.models.auth.form.ErrorRegisterForm
import com.andriawan.divergent_mobile_apps.models.auth.response.LoginResponse
import com.andriawan.divergent_mobile_apps.models.auth.response.User
import com.andriawan.divergent_mobile_apps.models.profile.PostProfile
import com.andriawan.divergent_mobile_apps.models.profile.form.ErrorProfileForm
import com.andriawan.divergent_mobile_apps.models.profile.response.UpdateProfileResponse
import com.andriawan.divergent_mobile_apps.utils.Constants
import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.PREFERENCE_ACCESS_TOKEN
import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.PREFERENCE_IS_LOGGED_IN
import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.PREFERENCE_USER_PROFILE
import com.andriawan.divergent_mobile_apps.utils.NetworkResult
import com.andriawan.divergent_mobile_apps.utils.SharedPreferenceHelper
import com.andriawan.divergent_mobile_apps.utils.SingleEvents
import com.andriawan.divergent_mobile_apps.utils.Utils.getImageString
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val sharedPreferenceHelper: SharedPreferenceHelper,
    private val repository: Repository
): AndroidViewModel(application), ProfileListener {

    private var imageFile: Bitmap? = null

    fun updateImageFile(image: Bitmap?) {
        this.imageFile = image
    }

    private val _errorForm = MutableLiveData<ErrorProfileForm>()
    val errorForm: LiveData<ErrorProfileForm> = _errorForm

    private val _users = MutableLiveData<SingleEvents<User>>()
    val users: LiveData<SingleEvents<User>> = _users

    private val _goToLoginPage = MutableLiveData<SingleEvents<Boolean>>()
    val goToLoginPage: LiveData<SingleEvents<Boolean>> = _goToLoginPage

    private val _updateProfileResponse = MutableLiveData<NetworkResult<UpdateProfileResponse>>()
    val updateProfileResponse: LiveData<NetworkResult<UpdateProfileResponse>> = _updateProfileResponse

    init {
        _users.value = SingleEvents(
            Gson().fromJson(sharedPreferenceHelper.getString(PREFERENCE_USER_PROFILE), User::class.java)
        )
        _errorForm.value = ErrorProfileForm()
    }

    fun onSubmitClicked(
        name: String,
        email: String,
        phoneNumber: String,
        password: String
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

            if (password.isNotEmpty()) {
                when {
                    password.length !in 13 downTo 7 -> {
                        it.password = "Password must be between 8-12 character"
                        valid = false
                    }

                    !password.matches(Regex(".*\\d.*")) -> {
                        it.password = "Password must contain number"
                        valid = false
                    }
                }
            }

            _errorForm.value = it

            Timber.d("Is valid $valid data error ${errorForm.value}")

            if (valid) {
                updateProfile(name, phoneNumber, password)
            }
        }
    }

    private fun updateProfile(name: String, phoneNumber: String, password: String) = viewModelScope.launch {
        val postUpdateProfile = PostProfile(name = name, phone_number = phoneNumber)

        if (password.isNotEmpty()) {
            postUpdateProfile.password = password
        }

        if (imageFile != null) {
            postUpdateProfile.image = "data:image/jpeg;base64,${getImageString(imageFile!!)}"
        }

        updateProfileSafeCall(postUpdateProfile)
    }

    private suspend fun updateProfileSafeCall(postUpdateProfile: PostProfile) {
        val hasConnection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hasConnectedInternet()
            } else {
                true
            }

        if (hasConnection) {
            _updateProfileResponse.value = NetworkResult.Loading()
            try {
                val response = repository.remote.updateProfile(postUpdateProfile)
                _updateProfileResponse.value = handleUpdateProfileResponse(response)

                response.body()?.data?.user?.let { user ->
                    sharedPreferenceHelper.saveString(
                        PREFERENCE_USER_PROFILE, Gson().toJson(user)
                    )
                    _users.value = SingleEvents(user)
                }
            } catch (e: Exception) {
                _updateProfileResponse.value = NetworkResult.Error("Something went wrong ${e.message}")
            }
        } else {
            _updateProfileResponse.value = NetworkResult.Error("No internet connection")
        }
    }

    private fun handleUpdateProfileResponse(response: Response<UpdateProfileResponse>): NetworkResult<UpdateProfileResponse> {
        return when {
            response.message().toString().contains("Timeout") -> {
                NetworkResult.Error("Connection time out")
            }

            response.isSuccessful -> {
                val updateProfileResponse = response.body()
                NetworkResult.Success(updateProfileResponse!!)
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

    override fun logoutClicked() {
        sharedPreferenceHelper.saveString(PREFERENCE_ACCESS_TOKEN, "")
        sharedPreferenceHelper.saveString(PREFERENCE_USER_PROFILE, "")
        sharedPreferenceHelper.saveBoolean(PREFERENCE_IS_LOGGED_IN, false)
        _goToLoginPage.postValue(SingleEvents(true))
    }
}