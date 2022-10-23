package com.wassallni.data

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class PlaceInfo(var primaryName:String?,
                     var address:String?,
                     var placeId:String?,
                     var latitude:Double?,
                     var longitude:Double?): Serializable {}
