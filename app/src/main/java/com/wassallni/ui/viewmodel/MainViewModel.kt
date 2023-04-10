package com.wassallni.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wassallni.data.model.uiState.MainUiState
import com.wassallni.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    private val _state = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val state = _state.asStateFlow()

    fun getTrips() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.e("TAG", "getTrips: again!!!!!", )
                _state.emit(MainUiState.Loading)
                val trips = mainRepository.getTrips()
                if (trips.isNotEmpty())
                    _state.value = MainUiState.Success(trips)
                else
                    _state.value = MainUiState.Empty
            } catch (ex: Exception) {
                ex.message?.let { _state.emit(MainUiState.Error(it)) }
            }
        }
    }


}
