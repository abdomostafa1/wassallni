package com.wassallni.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.wassallni.data.model.*
import com.wassallni.data.repository.TripRepository
import com.wassallni.utils.DateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationVM @Inject constructor(
    private val tripRepository: TripRepository,
    @ApplicationContext val appContext: Context
) : ViewModel() {
    private val TAG = "ReservationVM"

    private var fullTrip: FullTrip? = null

    private val _tripUiState = MutableStateFlow<TripUiState?>(null)
    val tripUiState = _tripUiState.asStateFlow()

    private val _stations = MutableStateFlow<List<Station>?>(null)
    val stations = _stations.asStateFlow()

    private val _polyline1 = MutableStateFlow<List<LatLng>?>(null)
    val polyline1 = _polyline1.asStateFlow()

    private val _polyline2 = MutableStateFlow<List<LatLng>?>(null)
    val polyline2 = _polyline2.asStateFlow()

    private val _counter = MutableStateFlow<Int>(1)
    val counter = _counter.asStateFlow()

    private val _nearestStations = MutableStateFlow<List<NearestStation>?>(null)
    val nearestStations = _nearestStations.asStateFlow()

    private var distances: List<DistanceItem>? = null
    var userLocation: LatLng? = null
     var selectedStation: Int = 0

    fun getTripDetails(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            fullTrip = tripRepository.getTripDetails(id)
            val driverName = fullTrip?.driverName!!
            val price = fullTrip?.price!!
            val fullDate = DateUseCase.fromMillisToString2(fullTrip?.startTime!!)
            val firstDate = DateUseCase.fromMillisToString1(fullTrip?.startTime!!)
            val lastDate = DateUseCase.fromMillisToString1(fullTrip?.endTime!!)

            _tripUiState.value = TripUiState(driverName, price, firstDate, lastDate, fullDate)
            _stations.value = fullTrip?.stations

        }
    }

    fun callDistanceMatrixApi(userLocation: LatLng) {
        this.userLocation = userLocation
        viewModelScope.launch(Dispatchers.IO) {
            distances = tripRepository.callDistanceMatrixApi(userLocation, fullTrip?.stations!!)
            _nearestStations.value =
                tripRepository.calculateShortDistances(stations.value!!, distances!!)
            Log.e(TAG, "_nearestStation: ${nearestStations.value.toString()}")

            val station = nearestStations.value?.get(selectedStation)
            getPolyLine2(LatLng(station?.location?.lat!!, station?.location?.lng!!))
        }
    }

    fun getPolyLine1() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.e(TAG, " why it doesn't work: ")
            _polyline1.value = tripRepository.getPolyLine1(fullTrip?.stations!!)
        }
    }

    fun getPolyLine2(station:LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            _polyline2.value = tripRepository.getPolyLine2(userLocation!!, station)
        }
    }

    fun incrementCounter() {
        if (counter.value == 2)
            return
        else
            ++_counter.value
    }

    fun decrementCounter() {
        if (counter.value == 1)
            return
        else
            --_counter.value
    }

    fun updatePolyline2() {
        val station = nearestStations.value?.get(selectedStation)
        val lat = station?.location?.lat
        val lng = station?.location?.lng
        val latLng = LatLng(lat!!, lng!!)
        getPolyLine2(latLng)
    }


}