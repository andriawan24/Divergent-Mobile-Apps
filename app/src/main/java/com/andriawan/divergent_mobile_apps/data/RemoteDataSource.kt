package com.andriawan.divergent_mobile_apps.data

import com.andriawan.divergent_mobile_apps.data.network.DivergentApi
import com.andriawan.divergent_mobile_apps.models.auth.PostLogin
import com.andriawan.divergent_mobile_apps.models.auth.PostRegister
import com.andriawan.divergent_mobile_apps.models.auth.response.LoginResponse
import com.andriawan.divergent_mobile_apps.models.articles.ArticleResponse
import com.andriawan.divergent_mobile_apps.models.diagnose.PostDiagnose
import com.andriawan.divergent_mobile_apps.models.diagnose.response.DiagnoseResponse
import com.andriawan.divergent_mobile_apps.models.history_diagnose.HistoryDiagnoseResponse
import com.andriawan.divergent_mobile_apps.models.profile.PostProfile
import com.andriawan.divergent_mobile_apps.models.profile.response.UpdateProfileResponse
import com.andriawan.divergent_mobile_apps.models.symptoms.SymptomsResponse
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

    suspend fun updateProfile(postUpdateProfile: PostProfile): Response<UpdateProfileResponse> {
        return divergentApi.updateProfile(postUpdateProfile)
    }

    suspend fun getSymptoms(fieldMap: Map<String, String>): Response<SymptomsResponse> {
        return divergentApi.getSymptoms(fieldMap)
    }

    suspend fun diagnose(postDiagnose: PostDiagnose): Response<DiagnoseResponse> {
        return divergentApi.diagnose(postDiagnose)
    }

    suspend fun diagnoseHistories(sort: String): Response<HistoryDiagnoseResponse> {
        return divergentApi.diagnoseHistories(sort)
    }
}