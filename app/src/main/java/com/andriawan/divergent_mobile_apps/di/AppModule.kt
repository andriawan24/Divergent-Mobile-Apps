package com.andriawan.divergent_mobile_apps.di

import android.content.Context
import com.andriawan.divergent_mobile_apps.utils.SharedPreferenceHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun providesSharedPrefHelper(
        @ApplicationContext context: Context,
        gson: Gson
    ) = SharedPreferenceHelper(context, gson)
}