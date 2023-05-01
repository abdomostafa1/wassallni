package com.wassallni.data.datasource

import android.content.SharedPreferences
import com.squareup.moshi.Json
import com.wassallni.data.model.ReservedTrip
import com.wassallni.data.model.TripService
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import javax.inject.Inject

class MyTripsDataSource @Inject constructor(
    private val tripsService: TripService,
    private val sharedPreferences: SharedPreferences
) {

    fun getReservedTrips(): List<ReservedTrip> {
        val token=sharedPreferences.getString("token","")
        val task = tripsService.getReservedTrips(token!!).execute()
        if (task.isSuccessful)
            return task.body()?.ReservedTrips!!
        else
            throw Exception(task.errorBody()?.string())
    }

}

data class ReservedTripsApiResponse(
    @Json(name = "FlightTrips")
    val ReservedTrips: List<ReservedTrip>
)