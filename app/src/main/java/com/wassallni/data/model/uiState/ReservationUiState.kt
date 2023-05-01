package com.wassallni.data.model.uiState

sealed class ReservationUiState {
    object InitialState : ReservationUiState()
    object Loading : ReservationUiState()
    data class Error(val errorMsg: String) : ReservationUiState()
    object Success: ReservationUiState()
}
