package com.wassallni.data.repository

import com.wassallni.data.datasource.MyTripsDataSource
import com.wassallni.data.model.ReservedTrip
import javax.inject.Inject

class MyTripsRepository @Inject constructor(private val myTripsDataSource: MyTripsDataSource){

    fun  getReservedTrips(): List<ReservedTrip> {

        return myTripsDataSource.getReservedTrips()
    }
}