package com.wassallni.data.model.uiState

import com.wassallni.data.model.ReservedTrip


sealed class MyTripsUiState {
    object Loading: MyTripsUiState()
    data class Success(val reservedTrips:List<ReservedTrip>): MyTripsUiState()
    data class Error(val msg:String): MyTripsUiState()
    object EmptyState: MyTripsUiState()
}
