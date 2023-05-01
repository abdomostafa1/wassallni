package com.wassallni.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.wassallni.R
import com.wassallni.data.model.*
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

@HiltViewModel
class BookVM @Inject constructor(
    private val bookTripRepository: BookTripRepository,
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

    var counter: Int = 1

    private val _nearestStations = MutableStateFlow<List<NearestStation>?>(null)
    val nearestStations = _nearestStations.asStateFlow()

    private val _reservationUiState =
        MutableStateFlow<ReservationUiState>(ReservationUiState.InitialState)
    val reservationUiState = _reservationUiState.asStateFlow()

    private val _address = MutableStateFlow<String>("")
    val address = _address.asStateFlow()

    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    private var distances: List<DistanceItem>? = null
    var userLocation: Origin = Origin()
    var selectedStation: Int = 0
    var movementMode: String = "walking"

    fun getTripDetails(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            fullTrip = bookTripRepository.getTripDetails(id)
            val driverName = fullTrip?.driverName!!
            val price = fullTrip?.price!!
            val date = DateUseCase.fromMillisToString2(fullTrip?.startTime!!)

            _tripUiState.value = TripUiState(driverName, price, counter, date)
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
                _message.postValue(ex.message)
            }
        }
    }

    private fun calculateMovementMode(): String {
        if (userLocation.placeId != null)
            return "driving"
        else
            return bookTripRepository.chooseMovementMode(
                userLocation.coordinates!!,
                stations.value!!
            )

    }

    private fun getPolyLine1() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _polyline1.value = bookTripRepository.getPolyLine1(fullTrip?.stations!!)
            } catch (ex: Exception) {
                _message.postValue( ex.message)
            }
        }
    }

    fun incrementCounter() {
        if (counter == 1 && fullTrip!!.availableSeats >= 2) {
            ++counter
            updateTripUiState()
        } else {
            if (counter == 2)
                _message.postValue(appContext.getString(R.string.maximum_seats_num) + " 2")
            else
                _message.postValue( appContext.getString(R.string.num_available_seats) + " 1")
        }
    }

    fun decrementCounter() {
        if (counter == 1)
            return
        else {
            --counter
            updateTripUiState()
        }
    }

    private fun updateTripUiState() {
        _tripUiState.update {
            it?.copy(counter = counter, price = fullTrip!!.price * counter)
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
                _message.postValue( ex.message)
            }
        }
    }

    fun bookTrip() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _reservationUiState.value = ReservationUiState.Loading

                val point= nearestStations.value?.get(selectedStation)?.index!!
                val numOfSeat=counter
                val bookingTrip = bookTripRepository.bookTrip(fullTrip!!, point,numOfSeat)
                if (bookingTrip)
                    _reservationUiState.value = ReservationUiState.Success
            } catch (ex: Exception) {
                ex.message?.let {
                    _reservationUiState.value = ReservationUiState.Error(it)
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
                _message.postValue( ex.message)
            }

        }
    }

}