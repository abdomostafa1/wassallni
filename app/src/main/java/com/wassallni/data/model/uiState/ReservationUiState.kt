package com.wassallni.data.model.uiState

sealed class ReservationUiState {
    object InitialState : ReservationUiState()
    object Loading : ReservationUiState()
    data class Error(val error: ReservationError) : ReservationUiState()
    object Success: ReservationUiState()
}

data class ReservationError(val status:Int,val msg:String)