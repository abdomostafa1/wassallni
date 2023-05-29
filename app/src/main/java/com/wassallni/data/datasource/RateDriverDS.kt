package com.wassallni.data.datasource

import android.content.SharedPreferences
import com.wassallni.data.model.DriverInfo
import kotlinx.coroutines.delay
import javax.inject.Inject

class RateDriverDS @Inject constructor(private val sharedPreferences: SharedPreferences){

    suspend fun getDriverInfo(): DriverInfo{
        delay(4000)
        return DriverInfo("احمد علي",
      "https://s3images.coroflot.com/user_files/individual_files/large_475212_iDk6jRCvQIBMQcwBXGEj6Um4R.jpg")
    }

    suspend fun rateDriver(stars: Float, message: String, tripId: String, driverId: String){
        val token=sharedPreferences.getString("token","")
        delay(4000)
    }
}