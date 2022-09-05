package com.wassallni

class PredictedPlaceInfo {


     var primaryName:String="Al Wasta"
    get() = field
    set(value) {
        field=value
    }
     var secondaryName:String="ahmed oraby street"
     var placeId:String="UDUJMNSNLK%EKLD"

    constructor(primaryName: String, secondaryName: String, placeId: String) {
        this.primaryName = primaryName
        this.secondaryName = secondaryName
        this.placeId = placeId
    }

}
