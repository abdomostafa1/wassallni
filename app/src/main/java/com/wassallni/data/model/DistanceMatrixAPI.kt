package com.wassallni.data.model

data class DistanceAPIResponse(val rows:List<Row>)

data class Row(val elements:List<DistanceItem>)

data class DistanceItem(
    val distance: Distance,
    val duration: Duration,
)


data class Distance(
    val text: String,
    val value: Int
)

data class Duration(
    val text: String,
    val value: Int
)

data class NearestStation(
    val name: String,
    val index: Int,
    val time: String,
    val location:Location
)