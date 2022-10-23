package com.wassallni.datasource

import android.content.Context
import com.wassallni.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class RouteRemoteDataSource (){
    //https://maps.googleapis.com/?origin=Disneyland&destination=Universal+Studios+Hollywood&key=YOUR_API_KEY

    private val url = "https://maps.googleapis.com/"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    fun drawRoute(originLatLng: String,destinationLatLng: String,key:String,callback:(response:String) ->Unit){
        val routeRetrofit=retrofit.create(RouteRetrofit::class.java)
        routeRetrofit.getPolyLine(originLatLng,destinationLatLng,key).enqueue(object :
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful)
                    callback.invoke(response.body().toString())
                else{
                    throw Exception(response.headers().toString()+response.body().toString())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                throw  t
            }
        })

    }
}
interface RouteRetrofit{
    @GET("maps/api/directions/json")
    fun getPolyLine(
        @Query("origin", encoded = true) originLatLng: String,
        @Query("destination", encoded = true) destinationLatLng: String,
        @Query("key") key: String
    ): Call<String>
}