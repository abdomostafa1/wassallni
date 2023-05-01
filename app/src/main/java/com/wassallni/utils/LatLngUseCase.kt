package com.wassallni.utils

import com.google.android.gms.maps.model.LatLng
import java.text.DecimalFormat

class LatLngUseCase {

    companion object {
        fun formatLatLng(latLng: LatLng): String {
            val decimalFormat = DecimalFormat("0.000000")
            val latitude = decimalFormat.format(latLng.latitude)
            val longitude = decimalFormat.format(latLng.longitude)

            return "${latLng.latitude},${latLng.longitude}"

        }
    }
}