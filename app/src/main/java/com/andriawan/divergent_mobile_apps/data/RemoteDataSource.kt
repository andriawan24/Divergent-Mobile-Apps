package com.andriawan.divergent_mobile_apps.data

import com.andriawan.divergent_mobile_apps.data.network.DivergentApi
import com.andriawan.divergent_mobile_apps.models.login.PostLogin
import com.andriawan.divergent_mobile_apps.models.login.response.LoginResponse
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val divergentApi: DivergentApi
) {

    suspend fun login(postLogin: PostLogin): Response<LoginResponse> {
        return divergentApi.login(postLogin)
    }
}