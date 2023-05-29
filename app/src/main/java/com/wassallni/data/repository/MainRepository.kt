package com.wassallni.data.repository

import com.wassallni.data.datasource.MainDataSource
import com.wassallni.data.model.Trip
import javax.inject.Inject


class MainRepository @Inject constructor(private val mainDataSource: MainDataSource){

    fun getTrips():List<Trip>{
       return mainDataSource.getTrips()
    }
}