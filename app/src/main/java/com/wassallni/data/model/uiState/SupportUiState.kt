package com.wassallni.data.model.uiState

sealed class SupportUiState{
    object InitialState: SupportUiState()
    object Loading: SupportUiState()
    object Success: SupportUiState()
    data class Error(val errorMsg:String) :SupportUiState()
}
