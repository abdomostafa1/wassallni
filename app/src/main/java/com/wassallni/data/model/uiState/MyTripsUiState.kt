package com.wassallni.data.model.uiState

import com.wassallni.data.model.BookedTrip


sealed class MyTripsUiState {
    object Loading: MyTripsUiState()
    data class Success(val bookedTrips:List<BookedTrip>): MyTripsUiState()
    data class Error(val msg:String): MyTripsUiState()
    object EmptyState: MyTripsUiState()
}
