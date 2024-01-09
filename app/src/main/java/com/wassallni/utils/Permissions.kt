package com.wassallni.utils

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wassallni.R
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class Permissions @Inject constructor(@ActivityContext val context: Context) {

    var action: () -> Unit? = {}
    fun isLocationPermissionEnabled(): Boolean {

        val preciseLocationPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val approximateLocationPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return preciseLocationPermission == PackageManager.PERMISSION_GRANTED || approximateLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    fun isGpsOpen(): Boolean {
        Log.e("checkGps 000: ", " Show me")

        val locationManager: LocationManager =
            context.getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) {
            return true
        }
        // otherwise return false
        return false

    }

}