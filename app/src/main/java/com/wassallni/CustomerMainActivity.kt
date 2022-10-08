package com.wassallni

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.wassallni.databinding.ActivityCustomerMainBinding
import org.json.JSONObject

class CustomerMainActivity : AppCompatActivity(),OnMapReadyCallback ,BroadcastObserver{
    private lateinit var binding:ActivityCustomerMainBinding
    lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    lateinit var broadcastReceiver:BroadcastReceiver
    private lateinit var intentFilter:IntentFilter
    val permissions=Permissions(this)
    private lateinit var mMap: GoogleMap
    lateinit var mapFragment:SupportMapFragment
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.e("announcement ", "new update is affected ", )
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapssd) as SupportMapFragment

        mapFragment.getMapAsync(this)
        broadcastReceiver=BroadcastReceiver(this)
        fusedLocationProviderClient= FusedLocationProviderClient(this)
        intentFilter= IntentFilter("android.location.PROVIDERS_CHANGED")
        binding.cvTuktuk.setOnClickListener {
            if(permissions.checkLocationPermission()) {
                if (permissions.isGpsOpen()) {
                    val intent = Intent(this, LocationSelectionActivity::class.java)
                    startActivity(intent)
                }
                else
                    permissions.openGps(true)
            }
            else
                permissions.requestLocationPermission()
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if(permissions.checkLocationPermission())
            if(permissions.isGpsOpen())
                getLocation()
        // Add a marker in Sydney and move the camera


    }

    private fun getLocation() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)

                return

            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

                override fun isCancellationRequested() = false
            })
                .addOnSuccessListener { location: Location? ->
                    if (location == null)
                        Toast.makeText(this, "open your fuckin Gps now", Toast.LENGTH_SHORT).show()
                    else {
                        val lat = location.latitude
                        val lng = location.longitude

                        val latLng=LatLng(lat,lng)
                        showUserLocationOnMap(latLng)
                    }

                }

    }
    fun showUserLocationOnMap(latLng: LatLng) {

        mMap.addMarker(MarkerOptions().position(latLng).title("Marker in zawiaa"))
        var ffll=CameraUpdateFactory.newLatLngZoom(latLng,15.0f)
        //mMap.animateCamera(ffll)
        mMap.animateCamera(ffll,4000,object :GoogleMap.CancelableCallback{
            override fun onCancel() {
                ;
            }

            override fun onFinish() {
                ;
            }
        })
    }


    override fun onGpsBroadcastResponse() {

        getLocation()
    }

    private fun registerReceiver(){
        registerReceiver(broadcastReceiver,intentFilter)
    }
    private fun unRegisterReceiver(){
        unregisterReceiver(broadcastReceiver)
    }
    override fun onStart() {
        super.onStart()
        registerReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterReceiver()
    }
}
