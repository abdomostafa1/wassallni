package com.wassallni.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wassallni.data.datasource.DirectionApiService
import com.wassallni.data.datasource.DistanceApiService
import com.wassallni.data.datasource.GeocodingApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

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
    fun provideDistanceApiService(okHttpClient: OkHttpClient): DistanceApiService {
        //"https://maps.googleapis.com/maps/api/distancematrix/json?origins=40.6655101%2C-73.89188969999998&destinations=40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626&key=YOUR_API_KEY"
        val baseUrl = "https://maps.googleapis.com/maps/api/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
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
    fun provideGeocodingApiService(): GeocodingApiService {
        //https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood&key=YOUR_API_KEY
        val baseUrl = "https://maps.googleapis.com/maps/api/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        return retrofit.create(GeocodingApiService::class.java)
    }
}