package com.wassallni.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wassallni.R
import com.wassallni.SingleLiveEvent
import com.wassallni.data.model.*
import com.wassallni.data.model.uiState.ReservationError
import com.wassallni.data.model.uiState.ReservationUiState
import com.wassallni.data.model.uiState.TripUiState
import com.wassallni.data.repository.BookTripRepository
import com.wassallni.utils.DateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ReservationVM"

@HiltViewModel
class BookVM @Inject constructor(
    private val bookTripRepository: BookTripRepository,
    @ApplicationContext val appContext: Context
) : ViewModel() {

    private var fullTrip: FullTrip? = null

    private val _tripUiState = MutableStateFlow<TripUiState?>(null)
    val tripUiState = _tripUiState.asStateFlow()

    private val _stations = MutableStateFlow<List<Station>?>(null)
    val stations = _stations.asStateFlow()

    private val _polyline1 = MutableStateFlow<List<LatLng>?>(null)
    val polyline1 = _polyline1.asStateFlow()

    private val _polyline2 = MutableStateFlow<List<LatLng>?>(null)
    val polyline2 = _polyline2.asStateFlow()

    private var seatsCounter: Int = 1

    private val _nearestStations = MutableStateFlow<List<NearestStation>?>(null)
    val nearestStations = _nearestStations.asStateFlow()

    private val _reservationUiState =
        MutableStateFlow<ReservationUiState>(ReservationUiState.InitialState)
    val reservationUiState = _reservationUiState.asStateFlow()

    private val _address = MutableStateFlow("")
    val address = _address.asStateFlow()

    val message = SingleLiveEvent<String>()


    private var distances: List<DistanceItem>? = null
    var userLocation: Origin = Origin()
    var selectedStation: Int = 0
    var movementMode: String = "walking"

    fun getTripDetails(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            fullTrip = bookTripRepository.getTripDetails(id)
            val driverName = fullTrip?.driverName!!
            val price = fullTrip?.price!!
            val date = DateUseCase.convertDateToYyMmDdHh(fullTrip?.startTime!!)

            _tripUiState.value = TripUiState(driverName, price, seatsCounter, date)
            _stations.value = fullTrip?.stations
            getPolyLine1()
        }
    }

    fun callDistanceMatrixApi() {
        viewModelScope.launch(Dispatchers.IO) {
            movementMode = calculateMovementMode()
            try {
                distances = bookTripRepository.callDistanceMatrixApi(
                    userLocation,
                    fullTrip?.stations!!,
                    movementMode
                )
                _nearestStations.value =
                    bookTripRepository.calculateShortDistances(stations.value!!, distances!!)
                Log.e(TAG, "_nearestStation: ${nearestStations.value.toString()}")
                val lat = nearestStations.value?.get(0)?.location?.lat
                val lng = nearestStations.value?.get(0)?.location?.lng
                val destination = LatLng(lat!!, lng!!)
                _polyline2.value = bookTripRepository.getPolyLine2(userLocation, destination)
            } catch (ex: Exception) {
                message.postValue(ex.message)
            }
        }
    }

    private fun calculateMovementMode(): String {
        return if (userLocation.placeId != null)
            "driving"
        else
            bookTripRepository.chooseMovementMode(
                userLocation.coordinates!!,
                stations.value!!
            )

    }

    private fun getPolyLine1() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _polyline1.value = bookTripRepository.getPolyLine1(fullTrip?.stations!!)
            } catch (ex: Exception) {
                message.postValue(ex.message)
            }
        }
    }

    fun incrementCounter() {
        if (seatsCounter == 1 && fullTrip!!.availableSeats >= 2) {
            ++seatsCounter
            updateTripUiState()
        } else {
            if (seatsCounter == 2)
                message.postValue(appContext.getString(R.string.maximum_seats_num) + " 2")
            else
                message.postValue(appContext.getString(R.string.num_available_seats) + " 1")
        }
    }

    fun decrementCounter() {
        if (seatsCounter == 1)
            return
        else {
            --seatsCounter
            updateTripUiState()
        }
    }

    private fun updateTripUiState() {
        _tripUiState.update {
            it?.copy(counter = seatsCounter)
        }
    }

    fun updatePolyline2() {
        viewModelScope.launch(Dispatchers.IO) {
            val station = nearestStations.value?.get(selectedStation)
            val lat = station?.location?.lat
            val lng = station?.location?.lng
            val destination = LatLng(lat!!, lng!!)
            try {
                _polyline2.value = bookTripRepository.getPolyLine2(userLocation, destination)
            } catch (ex: Exception) {
                message.postValue(ex.message)
            }
        }
    }

    fun bookTrip() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _reservationUiState.value = ReservationUiState.Loading

                val point = nearestStations.value?.get(selectedStation)?.index!!
                val numOfSeat = seatsCounter
                val bookingTrip = bookTripRepository.bookTrip(fullTrip!!, point, numOfSeat)
                if (bookingTrip)
                    _reservationUiState.value = ReservationUiState.Success
            } catch (ex: Exception) {
                ex.message?.let {
                    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val jsonAdapter: JsonAdapter<ReservationError> =
                        moshi.adapter(ReservationError::class.java)

                    val reservationError = jsonAdapter.fromJson(ex.message)
                    println(reservationError)
                    _reservationUiState.value = ReservationUiState.Error(reservationError!!)
                }
            }
        }
    }

    fun onDestroyReservationFragment() {
        _polyline2.value = null
        userLocation.placeId = null
        selectedStation = 0
    }

    fun callGeocodeApi() {
        viewModelScope.launch(Dispatchers.IO) {
            val latLng = userLocation.coordinates
            try {
                _address.value = bookTripRepository.callGeocodeApi(latLng)
            } catch (ex: Exception) {
                message.postValue(ex.message)
            }

        }
    }

//    fun payOnline() {
//        bookTripRepository.payOnline()
//    }
//
//    suspend fun getPaymentKey(apiKey:String): String {
//
//
//        return bookTripRepository.getPaymentKey(apiKey,100)
//    }

}