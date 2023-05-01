package com.wassallni.data.model.uiState

import com.wassallni.data.model.Trip

sealed class MainUiState{
    object Loading:MainUiState()
    data class Success(val trips: List<Trip>) : MainUiState()
    data class Error(val errorMsg: String) : MainUiState()
    object Empty:MainUiState()
}
