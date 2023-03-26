package com.wassallni.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class DateUseCase {

    companion object {
        fun fromMillisToString1(millis: Long): String {

            val date = Date(millis*1000) // create a Date object from the millisecond value

            val sdf = SimpleDateFormat("hh:mm a") // create a date format

            return sdf.format(date)

        }

        fun fromMillisToString2(millis: Long): String {

            val date = Date(millis*1000) // create a Date object from the millisecond value
            val sdf = SimpleDateFormat("E d/MM/yyyy hh:mm a") // create a date format

            return sdf.format(date)

        }
    }
}
