package com.wassallni.data.model.uiState

sealed class CancelTripUiState {
    object Default : CancelTripUiState()
    object Loading : CancelTripUiState()
    object Success : CancelTripUiState()
    data class Error(val errorMsg: String) : CancelTripUiState()
}