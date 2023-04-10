package com.wassallni.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.wassallni.R
import com.wassallni.data.model.FullTrip
import com.wassallni.data.model.uiState.CancelTripUiState
import com.wassallni.data.repository.BookedTripRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookedTripVM @Inject constructor(
    private val bookedTripRepo: BookedTripRepo,
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
            _fullTrip.value = bookedTripRepo.getTripDetails(id)

            getPolyLine1()
        }
    }

    private fun getPolyLine1() {
        viewModelScope.launch(Dispatchers.IO) {
            _polyline1.value = bookedTripRepo.getPolyLine1(fullTrip.value?.stations!!)
        }
    }

    fun cancelTrip(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _cancelRequest.value = CancelTripUiState.Loading
                val isSuccessful = bookedTripRepo.cancelTrip(id,fullTrip.value?.startTime!!)
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