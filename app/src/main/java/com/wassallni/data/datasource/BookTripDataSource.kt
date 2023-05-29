package com.wassallni.data.datasource

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.wassallni.BuildConfig.MAPS_API_KEY
import com.wassallni.data.model.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*
import javax.inject.Inject
import kotlin.collections.Map
import kotlin.collections.HashMap

private const val TAG = "ReservationDataSource"

class BookTripDataSource @Inject constructor(
    private val tripService: TripService,
    private val distanceApiService: DistanceApiService,
    private val geocodeApiService: GeocodingApiService,
    private val paymentService: PaymentService
) {

    fun callDistanceMatrixApi(
        origin: String,
        destination: String,
        mode: String
    ): List<DistanceItem> {
        val key = MAPS_API_KEY
        val language = Locale.getDefault().language
        val task =
            distanceApiService.getDistances(origin, destination, language, mode, key).execute()
        if (task.isSuccessful) {
            val body = task.body()
            Log.e(TAG, "Distances Body:$body ")
            val list = task.body()?.rows?.get(0)?.elements!!
            Log.e(TAG, "distances list:$list ")
            return list
        } else
            throw Exception(task.errorBody()?.string())
    }

    fun bookTrip(token: String, body: Map<String, Any>): Boolean {
        Log.e(TAG, "token:$token ")
        Log.e(TAG, "body:$body ")
        val task = tripService.bookTrip(token, body).execute()
        if (task.isSuccessful)
            return true
        else {
            val error = task.errorBody()?.string()
            Log.e(TAG, "bookTrip error:$error ")
            throw Exception(error)
        }
    }

    fun callGeocodeApi(latLng: String): String {
        val key = MAPS_API_KEY
        val task = geocodeApiService.getAddress(latLng, key).execute()
        if (task.isSuccessful)
            return task.body()!!
        else
            throw Exception(task.errorBody()?.string())
    }

    fun payOnline() {

    }

    fun stepOne(body: Map<String, Any>):Map <Any,Any>{
        val task = paymentService.authenticateRequest("application/json",body).execute()
        if(task.isSuccessful) {
            return task.body()!!
        }
        else
            Log.e(TAG, "stepOne: ${task.errorBody()?.string()}", )
        return kotlin.collections.HashMap<Any,Any>()
    }
    fun stepTwo(body: HashMap<String, Any>): Map<Any, Any> {
        val task=paymentService.orderRegistrationApi("application/json",body).execute()
        return task.body()!!
    }

    fun stepThree(body: PaymentRequest) :Map<Any, Any>{
        val task=paymentService.requestPaymentKey("application/json",body).execute()
        return task.body()!!

    }
}


//https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood&key=YOUR_API_KEY

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

interface GeocodingApiService {
    @GET("geocode/json")
    fun getAddress(
        @Query("latlng", encoded = true) origins: String,
        @Query("key") key: String
    ): Call<String>
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
