package com.wassallni.utils

import com.google.android.gms.maps.model.LatLng

class LatLngUseCase {

    companion object {
        fun formatLatLng(latLng: LatLng): String {
            return "${latLng.latitude},${latLng.longitude}"

        }
    }
}