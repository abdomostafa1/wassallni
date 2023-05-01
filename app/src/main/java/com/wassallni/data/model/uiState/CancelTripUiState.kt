package com.wassallni.data.model.uiState

import com.wassallni.data.model.Trip

sealed class CancelTripUiState {
    object Default : CancelTripUiState()
    object Loading : CancelTripUiState()
    object Success : CancelTripUiState()
    data class Error(val errorMsg: String) : CancelTripUiState()
}