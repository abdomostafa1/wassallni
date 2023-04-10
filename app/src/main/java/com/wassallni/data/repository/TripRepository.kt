package com.wassallni.data.repository

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.wassallni.data.datasource.TripDataSource
import com.wassallni.data.model.FullTrip
import com.wassallni.data.model.Origin
import com.wassallni.data.model.Station
import com.wassallni.utils.LatLngUseCase
import javax.inject.Inject

private const val TAG = "TripRepository"
open class TripRepository {

    @Inject
    lateinit var tripDataSource: TripDataSource

    fun getTripDetails(id: String): FullTrip {
        return tripDataSource.getTripDetails(id)
    }

    fun getPolyLine1(stations: List<Station>): List<LatLng> {
        val first = stations[0].location
        val last = stations[stations.size - 1].location
        val origin = LatLngUseCase.formatLatLng(LatLng(first.lat, first.lng))
        val destination = LatLngUseCase.formatLatLng(LatLng(last.lat, last.lng))
        var points = ArrayList<LatLng>()
        for (i in 1..stations.size - 2) {
            val location = stations[i].location
            points.add(LatLng(location.lat, location.lng))
        }
        Log.e(TAG, "points:$points ")
        val encodedPoints = PolyUtil.encode(points)
        val wayPoints = "enc:${encodedPoints}:"
        Log.e(TAG, "origin:$origin & destination:$destination & waypoint:$wayPoints")
        Log.e(TAG, "origin:$origin::dest:$destination  ")
        return tripDataSource.getPolyLine1(origin, destination, wayPoints)
    }

    fun getPolyLine2(userLocation: Origin, destination: LatLng): List<LatLng> {
        val origin=if(userLocation.placeId!=null)
            "place_id:${userLocation.placeId}"
        else
            LatLngUseCase.formatLatLng(userLocation.coordinates!!)
        val destinationParam = LatLngUseCase.formatLatLng(destination)
        Log.e(TAG, "origin:$origin,destination:$destinationParam ", )
        return tripDataSource.getPolyLine2(origin, destinationParam)
    }
}