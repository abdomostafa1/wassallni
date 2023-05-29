package com.wassallni.data.repository

import com.wassallni.data.datasource.PassengerTripsDataSource
import com.wassallni.data.model.BookedTrip
import javax.inject.Inject

class PassengerTripsRepository @Inject constructor(private val passengerTripsDataSource: PassengerTripsDataSource){

    fun  getReservedTrips(): List<BookedTrip> {

        return passengerTripsDataSource.getReservedTrips()
    }
}