package com.wassallni.data.datasource

import android.content.SharedPreferences
import com.wassallni.data.model.Driver
import com.wassallni.data.model.Rating
import com.wassallni.data.model.TripService
import kotlinx.coroutines.delay
import javax.inject.Inject

class RateDriverDS @Inject constructor(
    private val tripService: TripService,
    private val sharedPreferences: SharedPreferences
) {

    suspend fun getDriverInfo(driverId: String): Driver {
        val response = tripService.getDriver(driverId).execute()
        if (response.isSuccessful)
            return response.body()!!
        else
            throw Exception(response.errorBody().toString())
    }

    suspend fun rateDriver(rating: Rating) {
        val token = sharedPreferences.getString("token", "")
        val response = tripService.rateDriver(token!!, rating).execute()
        if (response.isSuccessful)
            return
        else
            throw Exception(response.errorBody().toString())
    }
}