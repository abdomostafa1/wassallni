package com.wassallni.data.repository

import com.wassallni.data.datasource.RateDriverDS
import com.wassallni.data.model.Driver
import com.wassallni.data.model.Rating
import javax.inject.Inject

class RateDriverRepo @Inject constructor(private val rateDriverDS: RateDriverDS) {

    suspend fun getDriverInfo(driverId: String): Driver {
        return rateDriverDS.getDriverInfo(driverId)
    }

    suspend fun rateDriver(rating: Rating) {
        rateDriverDS.rateDriver(rating)
    }

}