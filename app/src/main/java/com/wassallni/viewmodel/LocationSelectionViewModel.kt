package com.wassallni.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.wassallni.data.PlaceInfo
import com.wassallni.repository.PlaceRepository
import org.json.JSONException

class LocationSelectionViewModel (val context: Context):ViewModel() {
    private val placeRepository=PlaceRepository(context)
    var yourLocationEdt=MutableLiveData<String?>()
    var whereToEdt=MutableLiveData<String?>()
    var isAvailable=MutableLiveData<Boolean>()
    var mapIsMoving=MutableLiveData<Boolean>()
    var origin= PlaceInfo(null,null,null,null,null)
    var destination= PlaceInfo(null,null,null,null,null)

    val FOCUS_IN_ORIGIN_EDT:Int=1
    val FOCUS_IN_Destination_EDT:Int=2
    val FOCUS_IN_Parent: Int = 3

    fun getAddressFromLatLng(latLng: LatLng,currentFocus:Int){

        if(currentFocus==FOCUS_IN_Parent)
            return
        try {
            placeRepository.getAddressFromLatLng(latLng, currentFocus) { address: String? ->
                if (currentFocus == FOCUS_IN_ORIGIN_EDT)
                    yourLocationEdt.value = address
                else
                    whereToEdt.value = address
            }
        }
        catch (JsonException: JSONException){
            if(currentFocus==FOCUS_IN_ORIGIN_EDT)
                yourLocationEdt.value="unknow"
            else if (currentFocus==FOCUS_IN_Destination_EDT)
                whereToEdt.value="unknow"
            else
                ;
        }
    }

    fun getLatLngFromPlaceId(placeId: String){
        placeRepository.getLatLngFromPlaceId(placeId){}

    }

    fun findAutocompletePredictions(query: String,callback: (predictedPlaces: List<PlaceInfo>) -> Unit) {

        placeRepository.findAutocompletePredictions(query){ predictedPlaces: List<PlaceInfo> ->
            callback.invoke(predictedPlaces)
        }
    }

    fun pickLocation(index:Int,currentFocus:Int){
        val primaryName=placeRepository.pickLocation(index,currentFocus)
        if(currentFocus==FOCUS_IN_ORIGIN_EDT)
            yourLocationEdt.value=primaryName
        else
            whereToEdt.value=primaryName
    }

    fun isServiceAvailable(){
        placeRepository.isServiceAvailable { isAvailable :Boolean ->
            this.isAvailable.value=isAvailable
        }

    }
    fun isOriginEmpty():Boolean{
        return placeRepository.isOriginEmpty()
    }
    fun isDestinationEmpty():Boolean{
        return placeRepository.isDestinationEmpty()
    }

    fun retrieveOrigin() : PlaceInfo {
        return placeRepository.retrieveOrigin()
    }
    fun retrieveDestination() : PlaceInfo {
        return placeRepository.retrieveDestination()
    }
    fun onMapMoving(currentFocus: Int){
        if(currentFocus!=FOCUS_IN_Parent)
            mapIsMoving.value=true
    }
}