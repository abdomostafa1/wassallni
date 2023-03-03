package com.wassallni.di

import com.wassallni.data.datasource.LoginService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(ActivityComponent::class)
object LoginModule {

    @Provides
    fun provideLoginService(url:String="https://15c8-41-68-221-8.eu.ngrok.io"): LoginService {

        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(LoginService::class.java)
    }
}