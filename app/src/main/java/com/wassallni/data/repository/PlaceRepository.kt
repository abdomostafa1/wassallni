package com.wassallni.data.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.wassallni.data.model.PlaceInfo
import com.wassallni.data.datasource.PlaceRemoteDataSource
import org.json.JSONObject

class PlaceRepository(val context: Context) {

    private val placeRemoteDataSource = PlaceRemoteDataSource(context)
    private var origin = PlaceInfo(null, null, null, null, null)
    private var destination = PlaceInfo(null, null, null, null, null)

    private val maximumLng: Double = 31.212882
    private val minimumLng: Double = 31.188709
    private val minimumLat: Double = 29.308123
    private val maximumLat: Double = 29.350525

    private val FOCUS_IN_ORIGIN_EDT: Int = 1
    private val predictedPlaces = ArrayList<PlaceInfo>()
    private val latLngUseCase=LatLngUseCase()

    fun getAddressFromLatLng(latLng: LatLng,currentFocus: Int, callback: (address: String?) -> Unit) {
        val formattedLatLng = latLngUseCase.formatLatLng(latLng)
        if(currentFocus==FOCUS_IN_ORIGIN_EDT)
            updateOrigin(latLng)
        else
            updateDestination(latLng)
        placeRemoteDataSource.getAddressFromLatLng(formattedLatLng) { response: String ->
            var address :String?=null
            address = parseAddressJson(response)
            if(currentFocus==FOCUS_IN_ORIGIN_EDT)
                origin.address=address
            else
                destination.address=address
            callback.invoke(address)
        }

    }

    fun getLatLngFromPlaceId(placeId: String, callback: (latLng: LatLng) -> Unit) {
        placeRemoteDataSource.getLatLngFromPlaceId(placeId) { response: String ->
            var latLng = parseLatLngJson(response)
            callback.invoke(latLng)
        }

    }

    private fun parseAddressJson(jsonString: String): String {
        val root: JSONObject = JSONObject(jsonString)
        val status = root.getString("status")
        val plusCode = root.getJSONObject("plus_code")
        val compoundCode = plusCode.getString("compound_code")

        Log.e("address equal ", compoundCode)
        return compoundCode
    }

    private fun parseLatLngJson(jsonString: String): LatLng {
        val root = JSONObject(jsonString)
        val status = root.getString("status")

        val result = root.getJSONArray("results").getJSONObject(0)

        val location = result.getJSONObject("geometry").getJSONObject("location")
        val latitude = location.getDouble("lat")
        val longitude = location.getDouble("lng")
        return LatLng(latitude, longitude)

    }

    fun findAutocompletePredictions(
        query: String,
        callback: (predictedPlaces: List<PlaceInfo>) -> Unit
    ) {

        placeRemoteDataSource.findAutocompletePredictions(query) {

            predictedPlaces.clear()
            val predictionsResponse = it.autocompletePredictions

            for (prediction in predictionsResponse) {
                val primaryName = prediction.getPrimaryText(null).toString()
                val secondaryName = prediction.getSecondaryText(null).toString()
                val placeId = prediction.placeId
                val placeInfo = PlaceInfo(primaryName, secondaryName, placeId, null, null)
                predictedPlaces.add(placeInfo)
            }
            callback.invoke(predictedPlaces)
        }

    }



    fun updateOrigin(latLng: LatLng) {
        origin.latitude = latLng.latitude
        origin.longitude = latLng.longitude
    }

    fun updateDestination(latLng: LatLng) {
        destination.latitude = latLng.latitude
        destination.longitude = latLng.longitude
    }

    fun updateOrigin(address: String) {
        origin.address = address
    }

    fun updateDestination(address: String) {
        destination.address = address
    }

    fun pickLocation(index: Int, currentFocus: Int): String? {
        if (currentFocus == FOCUS_IN_ORIGIN_EDT)
            origin = predictedPlaces[index]
        else
            destination = predictedPlaces[index]

        return predictedPlaces[index].primaryName

    }

    fun isServiceAvailable(callback: (isAvailable:Boolean) -> Unit){
        checkLatLngValues {

            val isAvailable=checkAvailability()
            callback.invoke(isAvailable)
        }
    }
    private fun checkAvailability() :Boolean{

        return (origin.latitude!! in minimumLat..maximumLat && origin.longitude!! in minimumLng..maximumLng) &&
                (destination.latitude!! in minimumLat..maximumLat && destination.longitude!! in minimumLng..maximumLng)

    }

    private fun checkLatLngValues(callback: () -> Unit) {

        // origin and destination have LatLng
        if (origin.latitude != null && destination.latitude != null) {
            callback.invoke()
            return
        }
        getOriginLatLng() {
            getDestinationLatLng {
                callback.invoke()
            }
        }

    }

    private fun getOriginLatLng(callback: () -> Unit) {

        if (origin.latitude != null)
            callback.invoke()
        else {
            getLatLngFromPlaceId(origin.placeId!!) {
                origin.latitude = it.latitude
                origin.longitude = it.longitude
                callback.invoke()
            }
        }
    }

    private fun getDestinationLatLng(callback: () -> Unit) {
        if (destination.latitude != null && destination.longitude != null)
            callback.invoke()
        else {
            getLatLngFromPlaceId(destination.placeId!!) {
                destination.latitude = it.latitude
                destination.longitude = it.longitude
                callback.invoke()
             }
        }
    }

    fun isOriginEmpty(): Boolean {
        return origin.placeId == null && (origin.latitude == null || origin.longitude == null)
    }

    fun isDestinationEmpty(): Boolean {
        return destination.placeId == null && (destination.latitude == null || destination.longitude == null)
    }

    fun retrieveOrigin() : PlaceInfo {
        return origin
    }
    fun retrieveDestination() : PlaceInfo {
        return destination
    }

}