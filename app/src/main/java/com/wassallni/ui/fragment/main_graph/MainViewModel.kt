package com.wassallni.ui.fragment.main_graph

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wassallni.data.model.Trip
import com.wassallni.data.repository.MainRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class MainViewModel (val repository: MainRepository) :ViewModel(){

    private val _trips=MutableLiveData<ArrayList<Trip>>()
    val trips:LiveData<ArrayList<Trip>>
    get() = _trips

    private val _error=MutableLiveData<String>()
    val error:LiveData<String>
    get() = _error

    fun fetchTrips(){
        try {
            viewModelScope.launch (Dispatchers.IO){
               // _trips.value=repository.fetchTrips()
            }
        }
        catch (exception:Exception){
            _error.value=exception.message
        }
    }
}