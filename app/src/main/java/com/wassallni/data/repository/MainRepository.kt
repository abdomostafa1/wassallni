package com.wassallni.data.repository

import com.wassallni.data.datasource.MainDataSource


class MainRepository (private val mainDataSource: MainDataSource){

    val state=mainDataSource.state
    suspend fun getTrips(){
        mainDataSource.getTrips()
    }
}