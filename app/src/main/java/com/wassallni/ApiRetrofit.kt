package com.wassallni

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

//latLng=40.714224,-73.961452&key=YOUR_API_KEY
interface ApiRetrofit {
    @GET("maps/api/geocode/json")
    fun getAddress(@Query("latlng", encoded = true) latlng:String, @Query("key") key:String):Call<String>

    @GET("maps/api/geocode/json")
    fun getLatLng(@Query("place_id") id:String,@Query("key") key: String): Call<String>

    //https://maps.googleapis.com/?origin=Disneyland&destination=Universal+Studios+Hollywood&key=YOUR_API_KEY
    @GET("maps/api/directions/json")
    fun getPolyLine(@Query("origin" , encoded = true)originLatLng: String,@Query("destination" , encoded = true)destinationLatLng: String
                    ,@Query("key")key:String):Call<String>
}