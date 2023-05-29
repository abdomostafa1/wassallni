package com.wassallni.data.model.uiState

sealed class RateDriverUiState{
    
    object Loading:RateDriverUiState()
    object Success:RateDriverUiState()
    data class Error(val errorMsg:String):RateDriverUiState()
    object InitialState:RateDriverUiState()
    
}
