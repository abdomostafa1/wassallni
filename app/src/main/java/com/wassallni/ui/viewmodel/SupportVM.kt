package com.wassallni.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wassallni.data.model.uiState.SupportUiState
import com.wassallni.data.repository.SupportRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupportVM @Inject constructor(private val supportRepo: SupportRepo) : ViewModel() {

    private val _supportUiState = MutableStateFlow<SupportUiState>(SupportUiState.InitialState)
    val supportUiState = _supportUiState.asStateFlow()

    fun sendFeedback(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _supportUiState.value = SupportUiState.Loading
                supportRepo.sendFeedback(message)
                _supportUiState.value = SupportUiState.Success
            } catch (ex: Exception) {
                _supportUiState.value = SupportUiState.Error(ex.message!!)
            }
        }
    }
}