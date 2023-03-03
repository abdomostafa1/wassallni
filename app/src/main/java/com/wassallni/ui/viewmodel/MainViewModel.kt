package com.wassallni.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.wassallni.data.repository.MainRepository

class MainViewModel (private val mainRepository: MainRepository) : ViewModel() {

    val state=mainRepository.state
    suspend fun getTrips(){
        mainRepository.getTrips()
    }
}
