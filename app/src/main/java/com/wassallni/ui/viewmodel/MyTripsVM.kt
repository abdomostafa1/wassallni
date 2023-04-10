package com.wassallni.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wassallni.data.model.uiState.MyTripsUiState
import com.wassallni.data.model.ReservedTrip
import com.wassallni.data.repository.MyTripsRepository
import com.wassallni.ui.fragment.main_graph.MyTripsFragment.Companion.UPCOMING_TRIP
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MyTripsVM"
@HiltViewModel
class MyTripsVM @Inject constructor(private val myTripsRepo: MyTripsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<MyTripsUiState>(MyTripsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var upcomingTrips: List<ReservedTrip>? = null

    private var pastTrips: List<ReservedTrip>? = null

    val UPCOMING_TRIPS = 1
    val PAST_TRIPS = -1
    fun getReservedTrips() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = MyTripsUiState.Loading
                val time = System.currentTimeMillis() / 1000
                val trips = myTripsRepo.getReservedTrips()

                for (i in trips){
                    Log.e(TAG, "trip:$i", )
                }
          //      Log.e(TAG, "ReservedTrips===$trips ", )
                Log.e(TAG, "current time===$time ", )
                upcomingTrips = trips.filter {
                    (it.endTime > time && it.state==0)
                }
                pastTrips = trips.filter {
                    ( it.endTime <= time ||it.state<0)
                }

                if (UPCOMING_TRIP)
                    fetchUpcomingTrips()
                else
                    fetchPastTrips()
            } catch (ex: Exception) {
                ex.message?.let {
                    _uiState.value = MyTripsUiState.Error(it)
                }
            }
        }

    }

    fun fetchUpcomingTrips() {
        if (upcomingTrips != null) {
            if (upcomingTrips!!.isNotEmpty())
                _uiState.value = MyTripsUiState.Success(upcomingTrips!!)
            else
                _uiState.value = MyTripsUiState.EmptyState

        }
    }

    fun fetchPastTrips() {
        if (pastTrips != null) {
            if (pastTrips!!.isNotEmpty())
                _uiState.value = MyTripsUiState.Success(pastTrips!!)
            else
                _uiState.value = MyTripsUiState.EmptyState

        }
    }

}