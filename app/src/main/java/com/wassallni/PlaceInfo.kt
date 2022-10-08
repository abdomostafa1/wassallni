package com.wassallni

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class PlaceInfo : Serializable {


     private var primaryName:String?=null
     private var address:String?=null
     private var placeId:String?=null
     private var latitude:Double?=null
     private var longitude:Double?=null

    constructor(){}

    constructor(latLng: LatLng?) {
        latitude = latLng?.latitude
        longitude = latLng?.longitude
    }

    constructor(primaryName: String?, address: String?, placeId: String?) {
        this.primaryName = primaryName
        this.address = address
        this.placeId = placeId
    }


    fun getPrimaryName() : String? {
        return primaryName
    }
    fun setPriName(primaryName: String) {
        this.primaryName=primaryName
    }
    @JvmName("setAddress1")
    fun setAddress(address: String) {
        this.address=address
    }
    fun getAddress(): String? {
        return address
    }
    fun getPlaceID() :String?{
        return placeId
    }
    fun setPlaceID(id: String?) {
        placeId=id
    }
    @JvmName("getLatLng1")
    fun getLtLng() :LatLng?{
        return if(latitude==null||longitude==null)
            null
        else
            LatLng(latitude!!, longitude!!)
    }
    fun setLtLng(latLng: LatLng?) {
        latitude=latLng?.latitude
        longitude=latLng?.longitude
    }



}
