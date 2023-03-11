package com.wassallni.data.repository

import com.wassallni.data.datasource.MainDataSource
import javax.inject.Inject


class MainRepository @Inject constructor(private val mainDataSource: MainDataSource){

    val state=mainDataSource.state
    suspend fun getTrips(){
        mainDataSource.getTrips()
    }
}