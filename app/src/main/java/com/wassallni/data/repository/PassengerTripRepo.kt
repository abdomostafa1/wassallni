package com.wassallni.data.repository

import com.wassallni.data.datasource.BookedTripDataSource
import javax.inject.Inject

class PassengerTripRepo @Inject constructor(val bookedTripDS: BookedTripDataSource):TripRepository() {
    fun cancelTrip(id: String,startTime:Long):Boolean {
        return bookedTripDS.cancelTrip(id,startTime)
    }

}