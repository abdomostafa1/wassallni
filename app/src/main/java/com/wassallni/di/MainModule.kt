package com.wassallni.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wassallni.data.model.TripService
import com.wassallni.utils.Domain
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
object MainModule {

    @Provides
    @ViewModelScoped
    fun provideTripService(): TripService {
        val baseUrl=Domain+"api/v1/"
        val retrofit=Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                MoshiConverterFactory.create(
                Moshi.Builder()
                    .addLast(KotlinJsonAdapterFactory())
                    .build()
            ))
            .build()
        return retrofit.create(TripService::class.java)
    }

}