package com.wassallni.data.datasource

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.wassallni.BuildConfig
import com.wassallni.data.model.FullTrip
import com.wassallni.data.model.TripService
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

private const val TAG = "TripDataSource"

class BaseTripDataSource @Inject constructor(
    private val tripService: TripService,
    private val directionApiService: DirectionApiService,
) {

    fun getTripDetails(id: String): FullTrip {
        val task = tripService.getTripDetails(id).execute()
        if (task.isSuccessful) {
            val fullDetails = task.body()!!
            Log.e(TAG, "getTripDetails: $fullDetails")
            return fullDetails
        } else
            throw Exception(task.errorBody()?.string())

    }

    fun getPolyLine1(origin: String, destination: String, waypoints: String): List<LatLng> {
        val key = BuildConfig.MAPS_API_KEY
        val task = directionApiService.getPolyLine1(origin, destination, waypoints, key).execute()
        if (task.isSuccessful) {
            Log.e(TAG, "getPolyLine: ${task.body()}")
            return handlePolyLineResponse(task.body()!!)
        } else
            throw Exception(task.errorBody()?.string())
    }

    fun getPolyLine2(origin: String, destination: String): List<LatLng> {
        val key = BuildConfig.MAPS_API_KEY
        val task = directionApiService.getPolyLine2(origin, destination, key).execute()
        return if (task.isSuccessful) {
            Log.e(TAG, "getPolyLine: ${task.body()}")
            handlePolyLineResponse(task.body()!!)
        } else
            throw Exception(task.errorBody()?.string())

    }

    private fun handlePolyLineResponse(response: String): List<LatLng> {
        val root = JSONObject(response)

        val route = root.getJSONArray("routes").getJSONObject(0)
        //val leg=route.getJSONArray("legs").getJSONObject(0)
        val overviewPolyline = route.getJSONObject("overview_polyline")
        val encodesPath = overviewPolyline.getString("points")

        val points = PolyUtil.decode(encodesPath)
        Log.e(TAG, "points $points: ")
        Log.e(TAG, "encodesPath:$encodesPath")

        return points
        //val leg=route.getJSONArray("legs").getJSONObject(0)
    }

}

interface DirectionApiService {
    @GET("directions/json")
    fun getPolyLine1(
        @Query("origin", encoded = true) originLatLng: String,
        @Query("destination", encoded = true) destinationLatLng: String,
        @Query("waypoints", encoded = true) waypoints: String,
        @Query("key") key: String
    ): Call<String>

    @GET("directions/json")
    fun getPolyLine2(
        @Query("origin", encoded = true) originLatLng: String,
        @Query("destination", encoded = true) destinationLatLng: String,
        @Query("key") key: String
    ): Call<String>
}

