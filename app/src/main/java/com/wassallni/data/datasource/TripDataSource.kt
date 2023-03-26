package com.wassallni.data.datasource

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.wassallni.BuildConfig.MAPS_API_KEY
import com.wassallni.data.model.DistanceAPIResponse
import com.wassallni.data.model.DistanceItem
import com.wassallni.data.model.FullTrip
import com.wassallni.data.model.fullTrip
import com.wassallni.utils.LatLngUseCase
import kotlinx.coroutines.delay
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*
import javax.inject.Inject

class TripDataSource @Inject constructor(
    private val tripDetails: TripInfoService,
    private val directionApiService: DirectionApiService,
    private val distanceApiService: DistanceApiService
) {

    private val TAG = "TripDataSource"
    val origin = LatLng(29.343262, 31.203258)
    val destination = LatLng(29.043484, 31.109482)
    val station = LatLng(29.327294, 31.197011)
    val myLocation = LatLng(29.322858, 31.200796)
    suspend fun getTripDetails(id: String): FullTrip {
        delay(3000)
        return fullTrip
    }

    fun getPolyLine1(origin: String, destination: String, waypoints: String): List<LatLng> {
        val key = MAPS_API_KEY
        val task = directionApiService.getPolyLine1(origin, destination, waypoints, key).execute()
        if (task.isSuccessful) {
            Log.e(TAG, "getPolyLine: ${task.body()}")
            return handlePolyLineResponse(task.body()!!)
        } else
            throw Exception(task.errorBody()?.string())
    }

    suspend fun getPolyLine2(): List<LatLng>? {
        val key = MAPS_API_KEY
        val originParam = LatLngUseCase.formatLatLng(station)
        val destinationParam = LatLngUseCase.formatLatLng(myLocation)
        val task = directionApiService.getPolyLine2(originParam, destinationParam, key).execute()
        return if (task.isSuccessful) {
            Log.e(TAG, "getPolyLine: ${task.body()}")
            handlePolyLineResponse(task.body()!!)
        } else
            null

    }

    private fun handlePolyLineResponse(response: String): List<LatLng> {
        val root = JSONObject(response)
        var points = emptyList<LatLng>()
        val route = root.getJSONArray("routes").getJSONObject(0)
        //val leg=route.getJSONArray("legs").getJSONObject(0)
        val overviewPolyline = route.getJSONObject("overview_polyline")
        val encodesPath = overviewPolyline.getString("points")

        points = PolyUtil.decode(encodesPath)
        Log.e(TAG, "points ${points.toString()}: ")
        Log.e(TAG, "encodesPath:$encodesPath")

        return points
        //val leg=route.getJSONArray("legs").getJSONObject(0)
    }

    fun calculateDistances(origin: String, destination: String):List<DistanceItem>{
        val key = MAPS_API_KEY
        val mode="walking"
        val language=Locale.getDefault().language
        val task=distanceApiService.getDistances(origin,destination,language, mode, key).execute()
        if (task.isSuccessful) {
            val body= task.body()
            Log.e(TAG, "Distances Body:$body ", )
            val list=task.body()?.rows?.get(0)?.elements!!
            Log.e(TAG, "distances list:$list ", )
            return list
        }
        else
            throw Exception(task.errorBody()?.string())
    }
}

interface TripInfoService {

    @GET()
    fun getTripDetails(): Call<String>
}

//https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood&key=YOUR_API_KEY
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

interface DistanceApiService {
    @GET("distancematrix/json")
    fun getDistances(
        @Query("origins", encoded = true) origins: String,
        @Query("destinations", encoded = true) destinations: String,
        @Query("language", encoded = true) language: String,
        @Query("mode", encoded = true) mode: String,
        @Query("key") key: String
    ): Call<DistanceAPIResponse>
}

val stations = listOf(
    LatLng(29.327294, 31.197011),
    LatLng(29.302335, 31.186068),
    LatLng(29.282451, 31.189469),
    LatLng(29.256526, 31.193339),
    LatLng(29.238141, 31.191408),
    LatLng(29.220333, 31.181162),
    LatLng(29.145256, 31.137614),
    LatLng(29.049163, 31.118871),
    LatLng(29.039685, 31.132564),
    LatLng(29.038052, 31.122425),
)
