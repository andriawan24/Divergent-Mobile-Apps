package com.andriawan.divergent_mobile_apps.data

import com.andriawan.divergent_mobile_apps.data.network.DivergentApi
import com.andriawan.divergent_mobile_apps.models.auth.PostLogin
import com.andriawan.divergent_mobile_apps.models.auth.PostRegister
import com.andriawan.divergent_mobile_apps.models.auth.response.LoginResponse
import com.andriawan.divergent_mobile_apps.models.articles.ArticleResponse
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val divergentApi: DivergentApi
) {

    suspend fun login(postLogin: PostLogin): Response<LoginResponse> {
        return divergentApi.login(postLogin)
    }

    suspend fun register(postRegister: PostRegister): Response<LoginResponse> {
        return divergentApi.register(postRegister)
    }

    suspend fun getArticles(params: Map<String, String>): Response<ArticleResponse> {
        return divergentApi.getArticles(params)
    }
}