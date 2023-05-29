package com.wassallni.data.datasource

import android.content.SharedPreferences
import com.squareup.moshi.Json
import com.wassallni.data.model.BookedTrip
import com.wassallni.data.model.TripService
import javax.inject.Inject

class PassengerTripsDataSource @Inject constructor(
    private val tripsService: TripService,
    private val sharedPreferences: SharedPreferences
) {

    fun getReservedTrips(): List<BookedTrip> {
        val token=sharedPreferences.getString("token","")
        val task = tripsService.getReservedTrips(token!!).execute()
        if (task.isSuccessful)
            return task.body()?.bookedTrips!!
        else
            throw Exception(task.errorBody()?.string())
    }

}

data class ReservedTripsApiResponse(
    @Json(name = "FlightTrips")
    val bookedTrips: List<BookedTrip>
)