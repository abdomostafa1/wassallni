package com.wassallni.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wassallni.data.datasource.DirectionApiService
import com.wassallni.data.datasource.DistanceApiService
import com.wassallni.data.datasource.TripInfoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
object TripModule {

    @Provides
    @ViewModelScoped
    fun provideDirectionApiService(): DirectionApiService {
        //https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood&key=YOUR_API_KEY
        val baseUrl = "https://maps.googleapis.com/maps/api/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        return retrofit.create(DirectionApiService::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideDistanceApiService(): DistanceApiService {
        //"https://maps.googleapis.com/maps/api/distancematrix/json?origins=40.6655101%2C-73.89188969999998&destinations=40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626&key=YOUR_API_KEY"
        val baseUrl = "https://maps.googleapis.com/maps/api/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(
                Moshi.Builder()
                    .addLast(KotlinJsonAdapterFactory())
                    .build()
            ))
            .build()
        return retrofit.create(DistanceApiService::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideTripInfoService(): TripInfoService {
        val baseUrl = "https://maps.googleapis.com/maps/api/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create(TripInfoService::class.java)
    }
}