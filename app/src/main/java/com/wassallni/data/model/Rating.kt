package com.wassallni.data.model

import com.squareup.moshi.Json

data class Rating(
    @Json(name = "driver")
    val driverId :String,
    val message :String,
    val ratingAverage :Float
)
