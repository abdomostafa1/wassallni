package com.wassallni.data.datasource

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.wassallni.data.model.Trip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.internal.immutableListOf
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import javax.inject.Inject

class MainDataSource @Inject constructor(private val tripService: TripService) {

    private val TAG = "MainDataSource"

    suspend fun getTrips(): List<Trip> {
//        val task = tripService.getAllTrips().execute()
//        if (task.isSuccessful) {
//            Log.e(TAG, "isSuccessful ")
//            val allTripsResponse = task.body()
//            val trips = allTripsResponse?.trips
//            Log.e(TAG, "Trips: ${trips.toString()}")
//            return trips!!
//        } else {
//            val error = task.errorBody()?.string()
//            throw Exception(error)
//        }
        delay(3000)
        return trips
    }
}

interface TripService {

    @GET("trips/getAllTrips")
    fun getAllTrips(): Call<AllTripsResponse>
}

data class AllTripsResponse(val trips: List<Trip>)

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(val trips: List<Trip>) : MainUiState()
    data class Error(val errorMsg: String) : MainUiState()
}


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

