package com.wassallni.data.datasource

import android.content.Context
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.wassallni.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class PlaceRemoteDataSource (val context: Context) {

    private val url = "https://maps.googleapis.com/"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private val token = AutocompleteSessionToken.newInstance()
    private val placesClient = Places.createClient(context)
    private val apiKey: String = context.resources.getString(R.string.google_map_api_key)

    fun getAddressFromLatLng(latLng: String, callback: (body: String) -> Unit) {

        // https://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&key=YOUR_API_KEY

        val placeRetrofit: PlaceRetrofit =
            retrofit.create(PlaceRetrofit::class.java)

        placeRetrofit.getAddress(latLng, apiKey)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {

                    if (response.isSuccessful) {
                        val jsonString = response.body().toString()
                        callback.invoke(jsonString)

                    } else {
                        response.code()
                        Log.e("error raw() ", response.raw().toString())
                        Log.e("error code() ", response.code().toString())
                        Log.e("error  headers()", response.headers().toString())
                        throw Exception("status code ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {

                    t.message?.let { Log.e("network", it) }
                    throw Exception(t.message)
                }
            })
    }

    fun getLatLngFromPlaceId(placeId: String, callback: (body: String) -> kotlin.Unit) {

        //  https://maps.googleapis.com/maps/api/geocode/xml?place_id=ChIJeRpOeF67j4AR9ydy_PIzPuM&key=YOUR_API_KEY

        val placeRetrofit: PlaceRetrofit = retrofit.create(PlaceRetrofit::class.java)

        placeRetrofit.getLatLng(placeId, apiKey)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val jsonString = response.body().toString()
                        callback.invoke(jsonString)

                    } else {
                        Log.e("errorBody3 ", response.headers().toString())
                        throw Throwable(response.headers().toString()+response.body().toString())
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("onFailure ", t.message.toString())
                    throw t
                }
            })
    }

    fun findAutocompletePredictions(
        query: String,
        callback: (response: FindAutocompletePredictionsResponse) -> Unit
    ) {

        val request =
            FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().

                .setTypeFilter(TypeFilter.ADDRESS)

                //.setCountries("EG")
                .setSessionToken(token)
                .setCountries("EG")
                .setTypeFilter(TypeFilter.ADDRESS)
                .setTypeFilter(TypeFilter.CITIES)
                .setTypeFilter(TypeFilter.REGIONS)
                .setQuery(query)
                .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->

                callback.invoke(response)
            }.addOnFailureListener{ exception: Exception? ->
                if (exception is ApiException)
                    Log.e("Place not found:", " " + exception.statusCode)
            }
    }
}


interface PlaceRetrofit {
    @GET("maps/api/geocode/json")
    fun getAddress(
        @Query("latlng", encoded = true) latlng: String,
        @Query("key") key: String
    ): Call<String>

    @GET("maps/api/geocode/json")
    fun getLatLng(@Query("place_id") id: String, @Query("key") key: String): Call<String>

}