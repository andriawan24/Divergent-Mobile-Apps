package com.andriawan.divergent_mobile_apps.data.network

import com.andriawan.divergent_mobile_apps.models.auth.response.LoginResponse
import com.andriawan.divergent_mobile_apps.models.auth.PostLogin
import com.andriawan.divergent_mobile_apps.models.auth.PostRegister
import com.andriawan.divergent_mobile_apps.models.articles.ArticleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface DivergentApi {
    @POST("login")
    suspend fun login(@Body postLogin: PostLogin): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body postRegister: PostRegister): Response<LoginResponse>

    @GET("articles")
    suspend fun getArticles(@QueryMap params: Map<String, String>): Response<ArticleResponse>
}