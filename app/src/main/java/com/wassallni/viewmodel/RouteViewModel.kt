package com.wassallni.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.wassallni.data.PlaceInfo
import com.wassallni.repository.RouteRepository

class RouteViewModel (val context: Context, val origin: PlaceInfo, val destination: PlaceInfo):ViewModel(){

    private val routeRepository=RouteRepository(context,origin,destination)
    val points=MutableLiveData<List<LatLng>>()

    fun drawRoute(){
        routeRepository.drawRoute { points :List<LatLng>->
           this.points.value=points
        }

    }

    fun getOriginLatLng() :LatLng?{
        return routeRepository.getOriginLatLng()
    }
    fun getDestinationLatLng() :LatLng?{
        return routeRepository.getDestinationLatLng()
    }
    fun getLatLngBounds(): LatLngBounds {
        return routeRepository.getLatLngBounds()
    }

}