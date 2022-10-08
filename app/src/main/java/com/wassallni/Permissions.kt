package com.wassallni

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService

class Permissions (context:Context){
    lateinit var context:Context
    init {
        this.context=context
    }
    fun checkLocationPermission():Boolean{

        val preciseLocationPermission= ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val approximateLocationPermission=ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return preciseLocationPermission== PackageManager.PERMISSION_GRANTED||approximateLocationPermission==PackageManager.PERMISSION_GRANTED
    }
    fun isGpsOpen():Boolean{
        Log.e("checkGps 000: ", " Show me" )

        val locationManager: LocationManager =(context as AppCompatActivity).getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            return true;
        }
        // otherwise return false
        return false;

    }
    fun openGps(cancelable:Boolean){
        showDialog(cancelable)
    }
    private fun showDialog(cancelable:Boolean){
        val builder = AlertDialog.Builder(context)

        builder.setMessage("we need to access your location to use App ")
            .setTitle("Enable GPS")

        builder.setPositiveButton("enable", DialogInterface.OnClickListener(){ dialog, which ->

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            gpsSettingActivity.launch(intent)

        })
            .setCancelable(cancelable)

        builder.show()
    }
    private val gpsSettingActivity = (context as AppCompatActivity).registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        isGpsOpen()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun requestLocationPermission(){

        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    val locationPermissionRequest = (context as AppCompatActivity).registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                if(!isGpsOpen())
                    openGps(true)
                // Precise location access granted.
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                if(!isGpsOpen())
                    openGps(true)
                // Only approximate location access granted.
            } else -> {
            Toast.makeText(context,"You must let us access GPS",Toast.LENGTH_SHORT).show()
            // No location access granted.
        }
        }
    }

}