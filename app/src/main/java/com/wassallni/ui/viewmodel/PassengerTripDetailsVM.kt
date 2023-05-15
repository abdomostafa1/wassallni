package com.wassallni.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.wassallni.R
import com.wassallni.data.model.FullTrip
import com.wassallni.data.model.uiState.CancelTripUiState
import com.wassallni.data.repository.PassengerTripDetailsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PassengerTripDetailsVM @Inject constructor(
    private val passengerTripDetailsRepo: PassengerTripDetailsRepo,
    @ApplicationContext val context: Context
) : ViewModel() {

    private var _fullTrip = MutableStateFlow<FullTrip?>(null)
    val fullTrip = _fullTrip.asStateFlow()

    private val _polyline1 = MutableStateFlow<List<LatLng>?>(null)
    val polyline1 = _polyline1.asStateFlow()

    private val _cancelRequest = MutableStateFlow<CancelTripUiState>(CancelTripUiState.Default)
    val cancelRequest = _cancelRequest.asStateFlow()

    fun getTripDetails(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _fullTrip.value = passengerTripDetailsRepo.getTripDetails(id)

            getPolyLine1()
        }
    }

    private fun getPolyLine1() {
        viewModelScope.launch(Dispatchers.IO) {
            _polyline1.value = passengerTripDetailsRepo.getPolyLine1(fullTrip.value?.stations!!)
        }
    }

    fun cancelTrip(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _cancelRequest.value = CancelTripUiState.Loading
                val startTime=fullTrip.value?.startTime!!
                Log.e("TAG", "startTime=$startTime " )
                val isSuccessful = passengerTripDetailsRepo.cancelTrip(id,startTime)
                if (isSuccessful)
                    _cancelRequest.value = CancelTripUiState.Success
                else
                    _cancelRequest.value =
                        CancelTripUiState.Error(context.getString(R.string.cancel_failed))
            } catch (ex: Exception) {
                _cancelRequest.value = CancelTripUiState.Error(ex.message!!)
            }
        }
    }


}