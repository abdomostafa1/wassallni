package com.wassallni.data.datasource

import android.util.Log
import com.wassallni.data.model.Trip
import com.wassallni.data.model.TripService
import javax.inject.Inject


private const val TAG = "MainDataSource"

class MainDataSource @Inject constructor(private val tripService: TripService) {


    fun getTrips(): List<Trip> {
        val task = tripService.getAllTrips().execute()
        if (task.isSuccessful) {
            Log.e(TAG, "isSuccessful ")
            val allTripsResponse = task.body()
            val trips = allTripsResponse?.trips
            Log.e(TAG, "Trips: ${trips.toString()}")
            return trips!!
        } else {
            val error = task.errorBody()?.string()
            throw Exception(error)
        }
    }
}


data class AllTripsResponse(val trips: List<Trip>)

val trips = listOf(
    Trip(
        "23987492",
        "كوبري المشاه طريق صلاح سالم",
        "التوحيد والنور شارع الحجاز",
        1677857400,
        1677860100,
        5.5
    ),
    Trip(
        "23987491",
        "الوسطي",
        "شرق النيل",
        1677862800,
        1677865500,
        10.0
    ),
    Trip(
        "23987490",
        "العيور",
        "ببا",
        1677873600,
        1677876300,
        5.5
    ),
    Trip(
        "23987489",
        "تعليم صناعي",
        "الواسطي",
        1677880800,
        1677884400,
        11.0
    ),
    Trip(
        "23987488",
        "اهناسيا المدينة",
        "العبور",
        1677857400,
        1677860100,
        5.5
    ),
    Trip(
        "23987492",
        "جامعة النهضة",
        "الفشن",
        1677857400,
        1677860100,
        10.0
    ),
    Trip(
        "23987492",
        "بني سويف",
        "الواسطي",
        1677857400,
        1677860100,
        5.5
    ),
    Trip(
        "23987492",
        "مركز ناصر",
        "كلية الهندسة والتربية",
        1677857400,
        1677860100,
        10.0
    ),
    Trip(
        "23987492",
        "بني سويف",
        "شرق النيل المحافظة",
        1677857400,
        1677860100,
        5.5
    ),
    Trip(
        "23987492",
        "شارع البنوك",
        "صلاح سالم",
        1677857400,
        1677860100,
        5.5
    )

)

