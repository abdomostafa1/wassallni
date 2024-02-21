package com.wassallni.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wassallni.data.model.Driver
import com.wassallni.data.model.Rating
import com.wassallni.data.model.uiState.RateDriverUiState
import com.wassallni.data.repository.RateDriverRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RateDriverVM"

@HiltViewModel
class RateDriverVM @Inject constructor(private val rateDriverRepo: RateDriverRepo) : ViewModel() {

    private val _driverInfo = MutableStateFlow<Driver?>(null)
    val driverInfo = _driverInfo.asStateFlow()
    private val _rateUiState = MutableStateFlow<RateDriverUiState>(RateDriverUiState.InitialState)
    val rateUiState = _rateUiState.asStateFlow()

    fun getDriverInfo(driverId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val driver = rateDriverRepo.getDriverInfo(driverId)
                _driverInfo.emit(driver)
            } catch (error: Throwable) {
                val errorMsg: String = if (error.message != null) error.message!! else "error"
                Log.e(TAG, "retrieveDriverData: errorMsg=$errorMsg")
            }
        }
    }

    fun rateDriver(rating: Rating) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _rateUiState.value = RateDriverUiState.Loading
                rateDriverRepo.rateDriver(rating)
                _rateUiState.value = RateDriverUiState.Success
            } catch (e: Exception) {
                _rateUiState.value = RateDriverUiState.Error(e.message.toString())
            }
        }
    }
}