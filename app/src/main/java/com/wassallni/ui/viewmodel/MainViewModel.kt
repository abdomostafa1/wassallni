package com.wassallni.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.wassallni.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    val state=mainRepository.state
    suspend fun getTrips(){
        mainRepository.getTrips()
    }
}
