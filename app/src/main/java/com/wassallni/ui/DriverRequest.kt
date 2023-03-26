package com.wassallni.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.wassallni.R
import com.wassallni.data.model.PlaceInfo
import com.wassallni.databinding.ActivityDriverRequestBinding
import com.wassallni.ui.viewmodel.RouteViewModel

class DriverRequest : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityDriverRequestBinding
    lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap
    lateinit var routeViewModel: RouteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDriverRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val origin= intent.getSerializableExtra("origin") as PlaceInfo
        val destination= intent.getSerializableExtra("destination") as PlaceInfo
        routeViewModel=RouteViewModel(this,origin,destination)


        mapFragment = supportFragmentManager
            .findFragmentById(R.id.selection_map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        routeViewModel.points.observe(this){
                drawPolyLine(it)
        }
    }

    override fun onMapReady(p0: GoogleMap) {

        googleMap=p0

        googleMap.uiSettings.isZoomControlsEnabled=true
        googleMap.addMarker(MarkerOptions().position(routeViewModel.getOriginLatLng()!!))
        googleMap.addMarker(MarkerOptions().position(routeViewModel.getDestinationLatLng()!!).title("destination"))

        try {
            routeViewModel.drawRoute()
        }
        catch (throwable:Throwable){
            Toast.makeText(this,throwable.message,Toast.LENGTH_LONG).show()
        }

    }

    private fun drawPolyLine(points:List<LatLng>) {

                    var options=PolylineOptions()
                    options.addAll(points)
                    options.width(4.0f)
                    googleMap.addPolyline(options)
                    val latLngBounds=routeViewModel.getLatLngBounds()
                    val cameraUpdateFactory=CameraUpdateFactory.newLatLngBounds(
                        latLngBounds,
                        50
                    )
                    googleMap.animateCamera(cameraUpdateFactory)
    }
}
