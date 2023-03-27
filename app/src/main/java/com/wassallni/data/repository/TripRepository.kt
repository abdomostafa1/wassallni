package com.wassallni.data.repository

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.wassallni.data.datasource.TripDataSource
import com.wassallni.data.model.DistanceItem
import com.wassallni.data.model.FullTrip
import com.wassallni.data.model.NearestStation
import com.wassallni.data.model.Station
import com.wassallni.utils.LatLngUseCase
import javax.inject.Inject

class TripRepository @Inject constructor(private val tripDataSource: TripDataSource) {
    private val TAG = "TripRepository"

    suspend fun getTripDetails(id: String): FullTrip {
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

    fun callDistanceMatrixApi(userLocation: LatLng, stations: List<Station>): List<DistanceItem> {
        val origin = LatLngUseCase.formatLatLng(userLocation)
        var points = ArrayList<LatLng>()
        for (element in stations) {
            val location = element.location
            points.add(LatLng(location.lat, location.lng))
        }
        val encodedPoints = PolyUtil.encode(points)
        val destinations = "enc:${encodedPoints}:"
        Log.e(TAG, "origin:$origin & destination:$destinations")
        return tripDataSource.callDistanceMatrixApi(origin, destinations)
    }

    fun calculateShortDistances(
        stations: List<Station>,
        distances: List<DistanceItem>
    ): List<NearestStation> {
        var index1: Int = 0
        var nearestDistance: Int = distances[0].distance.value
        var index2: Int = 1
        var secondNearestDistance: Int = distances[1].distance.value
        for (i in 1 until distances.size) {
            val distance = distances[i].distance.value
            if (nearestDistance > distance) {
                index2 = index1
                secondNearestDistance = nearestDistance
                nearestDistance = distance
                index1 = i
            } else if (secondNearestDistance > distance) {
                secondNearestDistance = distance
                index2 = i
            }
        }

        return getNearestStations(
            stations[index1],
            stations[index2],
            distances[index1],
            distances[index2],
            index1,
            index2
        )
    }

    private fun getNearestStations(
        station1: Station,
        station2: Station,
        distanceItem1: DistanceItem,
        distanceItem2: DistanceItem,
        index1: Int,
        index2: Int
    ): List<NearestStation> {
        val name1 = station1.name
        val time1 = distanceItem1.duration.text
        val location1 = station1.location
        val nearestStation = NearestStation(name1, index1, time1, location1)

        val name2 = station2.name
        val time2 = distanceItem2.duration.text
        val location2 = station2.location
        val secondNearStation = NearestStation(name2, index2, time2, location2)
        val nearestStations = emptyList<NearestStation>()
        return nearestStations.plus(nearestStation).plus(secondNearStation)
    }


    fun getPolyLine2(origin: LatLng, destination: LatLng): List<LatLng> {
        val originParam = LatLngUseCase.formatLatLng(origin)
        val destinationParam = LatLngUseCase.formatLatLng(destination)
        Log.e(TAG, "origin:$originParam,destination:$destinationParam ", )
        return tripDataSource.getPolyLine2(originParam, destinationParam)
    }
}
