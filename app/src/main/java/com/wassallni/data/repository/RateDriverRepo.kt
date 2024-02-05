package com.wassallni.data.repository

import com.wassallni.data.datasource.RateDriverDS
import com.wassallni.data.model.Driver
import javax.inject.Inject

class RateDriverRepo @Inject constructor(private val rateDriverDS: RateDriverDS) {

    suspend fun getDriverInfo(): Driver {
        return rateDriverDS.getDriverInfo()
    }

    suspend fun rateDriver(stars: Float, message: String, tripId: String, driverId: String) {
        rateDriverDS.rateDriver(stars, message, tripId, driverId)
    }

}