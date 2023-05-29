package com.wassallni.data.repository

import com.wassallni.data.datasource.PassengerTripDetailsDS
import javax.inject.Inject

class PassengerTripDetailsRepo @Inject constructor(private val bookedTripDS: PassengerTripDetailsDS):TripRepository() {
    fun cancelTrip(id: String,startTime:Long):Boolean {
        return bookedTripDS.cancelTrip(id,startTime)
    }

}