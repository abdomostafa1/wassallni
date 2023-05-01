package com.wassallni.data.model

import com.squareup.moshi.Json

data class Trip(
    @Json(name = "_id")
    val id: String,
    val start: String,
    val destination: String,
    val startTime: Long,
    val endTime: Long,
    val price: Double
)
