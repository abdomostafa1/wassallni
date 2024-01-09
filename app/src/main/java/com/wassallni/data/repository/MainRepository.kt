package com.wassallni.data.repository

import com.wassallni.data.datasource.MainDataSource
import com.wassallni.data.model.Trip
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject


class MainRepository @Inject constructor(private val mainDataSource: MainDataSource) {

    fun getTrips(): List<Trip> {
        return mainDataSource.getTrips()
    }

    suspend fun ok() {
        coroutineScope {
            async {  }
            delay(7676)
        }
        supervisorScope {

        }
    }
}