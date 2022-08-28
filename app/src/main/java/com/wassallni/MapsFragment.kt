package com.wassallni

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.GpsStatus
import android.location.Location
import android.location.LocationManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

class MapsFragment : Fragment() {

    private lateinit var googleMap:GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    lateinit var broadcastReceiver:MyBroadcastReceiver
    lateinit var intentFilter:IntentFilter
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        this.googleMap=googleMap
//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private val permissions= activity?.let { Permissions(it) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        val locationManager: LocationManager =(context as AppCompatActivity).getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

        if(permissions?.isGpsOpen()==true)
            getLocation()
        broadcastReceiver=MyBroadcastReceiver()
        intentFilter=IntentFilter("android.location.PROVIDERS_CHANGED")
        fusedLocationProviderClient = activity?.let {
            LocationServices.getFusedLocationProviderClient(
                it
            )
        }!!

    }

     fun getLocation() {
        activity?.let {
            if (ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)

                      return

            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

                override fun isCancellationRequested() = false
            })
                .addOnSuccessListener { location: Location? ->
                    if (location == null)
                        Toast.makeText(it, "Cannot get location.", Toast.LENGTH_SHORT).show()
                    else {
                        val lat = location.latitude
                        val lon = location.longitude
                        val latLng=LatLng(lat,lon)
                        showUserLocationOnMap(latLng)
                    }

                }

        }

        }
    fun showUserLocationOnMap(latLng: LatLng) {

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            it.registerReceiver(broadcastReceiver,intentFilter)
        }
        broadcastReceiver.addSubscriber(this)
    }

    override fun onPause() {
        super.onPause()
        activity?.let {
            it.unregisterReceiver(broadcastReceiver)
        }
        broadcastReceiver.removeSubscriber(this)
    }

    override fun onStart() {
        super.onStart()
        activity?.let {
            it.registerReceiver(broadcastReceiver,intentFilter)
        }
        broadcastReceiver.addSubscriber(this)
    }

    override fun onStop() {
        super.onStop()
        activity?.let {
            it.unregisterReceiver(broadcastReceiver)
        }
        broadcastReceiver.removeSubscriber(this)
    }
}