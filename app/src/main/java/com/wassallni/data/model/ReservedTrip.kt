package com.wassallni.data.model

data class ReservedTrip(
    val _id:String,
    val tripId: String,
    val point: Int,
    val state: Int,
    val numOfSeat: Int,
    val start: String,
    val destination: String,
    val startTime: Long,
    val endTime: Long,
    val price:Double=11.0
)