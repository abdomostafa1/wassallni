package com.wassallni.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.wassallni.BuildConfig
import com.wassallni.data.datasource.BookTripDataSource
import com.wassallni.data.model.*
import com.wassallni.utils.LatLngUseCase
import org.json.JSONObject
import javax.inject.Inject
import kotlin.math.*

private const val TAG = "TripRepository"

class BookTripRepository @Inject constructor(private val bookTripDataSource: BookTripDataSource) :
    TripRepository() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    fun callDistanceMatrixApi(
        userLocation: Origin,
        stations: List<Station>,
        mode: String
    ): List<DistanceItem> {
        val origin = if (userLocation.placeId != null)
            "place_id:${userLocation.placeId}"
        else
            LatLngUseCase.formatLatLng(userLocation.coordinates!!)
        var points = ArrayList<LatLng>()
        for (element in stations) {
            val location = element.location
            points.add(LatLng(location.lat, location.lng))
        }
        val encodedPoints = PolyUtil.encode(points)
        val destinations = "enc:${encodedPoints}:"
        Log.e(TAG, "origin:$origin & destination:$destinations")
        return bookTripDataSource.callDistanceMatrixApi(origin, destinations, mode)
    }

    fun calculateShortDistances(
        stations: List<Station>,
        distances: List<DistanceItem>
    ): List<NearestStation> {
        var index1 = 0
        var nearestDistance: Int = distances[0].distance.value
        var index2 = 1
        var secondNearestDistance: Int = distances[1].distance.value
        for (i in 1 until distances.size) {
            //distance in metres
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


    fun bookTrip(fullTrip: FullTrip, point: Int, numOfSeat: Int): Boolean {
        val body = prepareBody(fullTrip, point, numOfSeat)
        val token = sharedPreferences.getString("token", "")!!

        return bookTripDataSource.bookTrip(token, body)
    }

    private fun prepareBody(fullTrip: FullTrip, point: Int, numOfSeat: Int): Map<String, Any> {
        val map = HashMap<String, Any>()

        fullTrip.let {
            map["tripId"] = it._id
            map["numOfSeat"] = numOfSeat
            map["point"] = point
            map["start"] = it.start
            map["destination"] = it.destination
            map["startTime"] = it.startTime
            map["endTime"] = it.endTime
            map["price"] = it.price
            val name: String = sharedPreferences.getString("name", "")!!
            map["name"] = name
        }

        return map
    }

    fun callGeocodeApi(latLng: LatLng?): String {
        val latLngStr = LatLngUseCase.formatLatLng(latLng!!)
        val response = bookTripDataSource.callGeocodeApi(latLngStr)
        return handlePolyLineResponse(response)
    }

    private fun handlePolyLineResponse(response: String): String {

        val root = JSONObject(response)
        root.getString("status")
        val plusCode = root.getJSONObject("plus_code")
        plusCode.getString("compound_code")
        val results = root.getJSONArray("results")
        val result = results.get(0) as JSONObject
        return result.getString("formatted_address")
    }

    fun chooseMovementMode(origin: LatLng, stations: List<Station>): String {
        val lat1 = origin.latitude
        val lon1 = origin.longitude
        var shortestDistance: Double =
            haversine(lat1, lon1, stations[0].location.lat, stations[0].location.lng)
        for (i in 1 until stations.size - 1) {
            val lat2 = stations[i].location.lat
            val lon2 = stations[i].location.lng
            val distance = haversine(lat1, lon1, lat2, lon2)
            if (distance < shortestDistance)
                shortestDistance = distance
        }
        return if (shortestDistance > 800)
            "driving"
        else
            "walking"
    }

    private fun haversine(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        // distance between latitudes and longitudes
        var lat1 = lat1
        var lat2 = lat2
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        // convert to radians
        lat1 = Math.toRadians(lat1)
        lat2 = Math.toRadians(lat2)

        // apply formulae
        val a = sin(dLat / 2).pow(2.0) +
                sin(dLon / 2).pow(2.0) *
                cos(lat1) *
                cos(lat2)
        val rad = 6371.0
        val c = 2 * asin(sqrt(a))
        return rad * c * 1000
    }

//    fun payOnline() {
//        bookTripDataSource.payOnline()
//    }

//    suspend fun getPaymentKey(apiKey: String, price: Long): String {
//
//        val response1 = stepOne(apiKey)
//        val token = response1["token"] as String
//
//        Log.e(TAG, "token: $token")
//        val response2 = stepTwo(token, price)
//
//        val orderId = response2["id"] as Double
//        val id = orderId.toInt()
//        Log.e(TAG, "id==$id ")
//        val response3 = stepThree(token, id, price)
//
//        val paymentKey = response3["token"] as String
//        return paymentKey
//    }
//
//    private fun stepOne(apiKey: String): Map<Any, Any> {
//        val body = HashMap<String, String>()
//        body["api_key"] = apiKey
//        return bookTripDataSource.stepOne(body)
//    }
//
//    private fun stepTwo(token: String, price: Long): Map<Any, Any> {
//        val body = HashMap<String, Any>()
//        body["auth_token"] = token
//        body["delivery_needed"] = "false"
//        body["amount_cents"] = "$price"
//        body["currency"] = "EGP"
//        body["items"] = emptyList<String>()
//
//        return bookTripDataSource.stepTwo(body)
//
//    }
//
//    private fun stepThree(token: String, orderId: Int, price: Long): Map<Any, Any> {
//        val integrationId = BuildConfig.integrationId
//        val billingData = BillingData(
//            email = "abdomostafa19999@gmail.com", phoneNumber = "01005730436",
//            firstName = "ahmed", lastName = "nabil"
//        )
//
//        val body = PaymentRequest(
//            token,
//            "$price",
//            3600,
//            "$orderId",
//            billingData,
//            integrationID = integrationId.toLong()
//        )
//
//        return bookTripDataSource.stepThree(body)
//
//    }

}
