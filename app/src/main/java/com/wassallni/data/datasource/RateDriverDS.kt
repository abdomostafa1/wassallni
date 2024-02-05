package com.wassallni.data.datasource

import android.content.SharedPreferences
import com.wassallni.data.model.Driver
import kotlinx.coroutines.delay
import javax.inject.Inject

class RateDriverDS @Inject constructor(private val sharedPreferences: SharedPreferences) {

    suspend fun getDriverInfo(): Driver {
        delay(4000)
        return Driver("", 0, "", "", false, "ahmed ali", "", 0.0)
    }
        suspend fun rateDriver(stars: Float, message: String, tripId: String, driverId: String) {
            val token = sharedPreferences.getString("token", "")
            delay(4000)
        }
    }