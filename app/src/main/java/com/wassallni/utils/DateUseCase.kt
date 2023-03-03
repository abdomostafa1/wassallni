package com.wassallni.utils

import java.text.SimpleDateFormat
import java.util.*

class DateUseCase {

    fun fromMillisToString(millis: Long): String {

        val date = Date(millis) // create a Date object from the millisecond value

        val sdf = SimpleDateFormat("hh:mm ") // create a date format

        return sdf.format(date)

    }

    fun calculateLeftTime(){

    }

}
