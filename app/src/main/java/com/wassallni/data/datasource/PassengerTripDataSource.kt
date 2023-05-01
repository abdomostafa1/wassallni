package com.wassallni.data.datasource

import com.wassallni.data.model.LoggedInUser
import com.wassallni.data.model.TripService
import javax.inject.Inject

class BookedTripDataSource @Inject constructor(private val tripService: TripService) {
    @Inject
    lateinit var loggedInUser: LoggedInUser
    fun cancelTrip(id: String,startTime:Long): Boolean {
        val token=loggedInUser.getToken()
        val map = HashMap<String, Any>()
        map["startTime"] = startTime
        val task = tripService.cancelTrip(token, map,id).execute()
        if (task.isSuccessful) {
            return task.body()?.message == "Trip Cancel Success"
        } else
            throw Exception(task.errorBody()?.string())

    }

}

data class CancelTripResponse(val message: String)