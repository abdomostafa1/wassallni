package com.wassallni.data.model

import com.wassallni.data.datasource.AllTripsResponse
import com.wassallni.data.datasource.CancelTripResponse
import com.wassallni.data.datasource.ReservedTripsApiResponse
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

@JvmSuppressWildcards
interface TripService {

    @GET("trips/getAllTrips")
    fun getAllTrips(): Call<AllTripsResponse>

    @GET("trips/getDetailsOfTrip/{id}")
    fun getTripDetails(@Path("id") id: String): Call<FullTrip>

    @POST("flightTrip")
    fun bookTrip(@Header("token") token: String, @Body body: Map<String, Any>): Call<Any>

    @GET("flightTrip/getFlightTripsByToken")
    fun getReservedTrips(@Header("token") token: String): Call<ReservedTripsApiResponse>

    @PUT("flightTrip/cancelFlightTrip/{id}")
    fun cancelTrip(
        @Header("token") token: String,
        @Body body: Map<String, Any>,
        @Path("id") id: String
    ): Call<CancelTripResponse>

    @POST("support")
    fun sendFeedback(@Header("token") token: String, @Body body: Map<String, Any>): Call<Any>

    @GET("driver/{id}")
    fun getDriver(@Path("id") id: String): Call<Driver>

    @POST("review")
    fun rateDriver(
        @Header("token") token: String,
        @Body rating: Rating
    ): Call<Rating>
}

