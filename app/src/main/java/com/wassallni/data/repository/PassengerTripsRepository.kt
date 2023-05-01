package com.wassallni.data.repository

import com.wassallni.data.datasource.MyTripsDataSource
import com.wassallni.data.model.BookedTrip
import javax.inject.Inject

class PassengerTripsRepository @Inject constructor(private val myTripsDataSource: MyTripsDataSource){

    fun  getReservedTrips(): List<BookedTrip> {

        return myTripsDataSource.getReservedTrips()
    }
}