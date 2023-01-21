package com.wassallni.data.repository

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.PolyUtil
import com.wassallni.R
import com.wassallni.data.datasource.RouteRemoteDataSource
import com.wassallni.data.model.PlaceInfo
import org.json.JSONObject

class RouteRepository (val context: Context, val origin: PlaceInfo, val destination: PlaceInfo){
    private val routeRemoteDataSource=RouteRemoteDataSource()
    private val latLngUseCase=LatLngUseCase()
    private val apiKey: String = context.resources.getString(R.string.google_map_api_key)

    private val maximumLng: Double = 31.212882
    private val minimumLng: Double = 31.188709
    private val minimumLat: Double = 29.308123
    private val maximumLat: Double = 29.350525

    fun drawRoute(callback:(points:List<LatLng>) ->Unit){
        val originLatLng=LatLng(origin.latitude!!, origin.longitude!!)
        val destinationLatLng=LatLng(destination.latitude!!, destination.longitude!!)
        // Format LatLng to match query
        val originFormattedLatLng=latLngUseCase.formatLatLng(originLatLng)
        val destinationFormattedLatLng=latLngUseCase.formatLatLng(destinationLatLng)

        routeRemoteDataSource.drawRoute(originFormattedLatLng,destinationFormattedLatLng,apiKey){

            val root= JSONObject(it)

            val route= root.getJSONArray("routes").getJSONObject(0)
            //val leg=route.getJSONArray("legs").getJSONObject(0)
            val overviewPolyline=route.getJSONObject("overview_polyline")
            val encodesPath=overviewPolyline.getString("points")
            var points= PolyUtil.decode(encodesPath)

            callback.invoke(points)
        }
    }

    fun getOriginLatLng() :LatLng{
        return LatLng(origin.latitude!!,origin.longitude!!)
    }
    fun getDestinationLatLng() :LatLng{
        return return LatLng(destination.latitude!!,destination.longitude!!)
    }

    fun getLatLngBounds():LatLngBounds{

        var builder = LatLngBounds.Builder()
        builder.include(LatLng(maximumLat,maximumLng))
        builder.include(LatLng(maximumLat,minimumLng))
        builder.include(LatLng(minimumLat,maximumLng))
        builder.include(LatLng(minimumLat,minimumLng))

        return builder.build()
    }
}