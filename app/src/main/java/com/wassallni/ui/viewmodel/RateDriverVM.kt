package com.wassallni.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wassallni.data.model.Driver
import com.wassallni.data.model.uiState.RateDriverUiState
import com.wassallni.data.repository.RateDriverRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RateDriverVM @Inject constructor(private val rateDriverRepo: RateDriverRepo): ViewModel(){

    private val _driverInfo= MutableStateFlow<Driver?>(null)
    val driverInfo=_driverInfo.asStateFlow()
    private val _rateUiState = MutableStateFlow<RateDriverUiState>(RateDriverUiState.InitialState)
    val rateUiState = _rateUiState.asStateFlow()

     fun getDriverInfo() {
        viewModelScope.launch (Dispatchers.IO) {
            _driverInfo.value=rateDriverRepo.getDriverInfo()
        }
    }

    fun rateDriver(stars:Float,message:String,tripId:String,driverId:String){
        viewModelScope.launch (Dispatchers.IO) {
            _rateUiState.value=RateDriverUiState.Loading
            rateDriverRepo.rateDriver(stars, message,tripId, driverId)
            _rateUiState.value=RateDriverUiState.Success
        }
    }
}