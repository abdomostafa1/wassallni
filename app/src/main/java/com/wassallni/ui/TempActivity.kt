package com.wassallni.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wassallni.R
import com.wassallni.databinding.ActivityTempBinding
import com.wassallni.databinding.StationDialogBinding
import com.wassallni.ui.viewmodel.ReservationVM
import com.wassallni.utils.Permissions
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.managers.FragmentComponentManager
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class TempActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityTempBinding
    private lateinit var mapFragment: SupportMapFragment
    lateinit var map: GoogleMap
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var permission: Permissions
    private var polyline: Polyline? = null
    private val viewModel: ReservationVM by viewModels()

    val origin = LatLng(29.343262, 31.203258)
    val destination = LatLng(29.043484, 31.109482)
    val station = LatLng(29.327294, 31.197011)
    val Dash: PatternItem = Dash(20f)
    val DOT: PatternItem = Dot()
    val GAP: PatternItem = Gap(20F)
    val PATTERN_POLYLINE_DASHED = Arrays.asList(GAP, Dash)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTempBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapFragment = supportFragmentManager.findFragmentById(R.id.temp_map) as SupportMapFragment

        mapFragment.getMapAsync(this)
        permission = Permissions(this)
        viewModel.getTripDetails("id")
        binding.changeStation.setOnClickListener {
            showDialog()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.nearestStations.collect {
                    if (it != null) {
                        binding.firstStation.text = it[0].name
                        binding.secondStation.text = it[1].name
                        binding.textView10.text = it[0].time
                        binding.textView11.text = it[1].time
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tripUiState.collect {
                    if (it != null) {
                        getLocation()
                    }
                }
            }


        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.polyline2.collect { points ->
                    if (points != null) {
                        if (polyline != null) { //update polyline
                            polyline?.points = points
                            setMapBounds(points[0], points[points.size - 1])
                            return@collect
                        }
                        Log.e("TAG", "polyline2: ")
                        val polylineOptions = PolylineOptions()
                        polylineOptions.addAll(points)
                        polylineOptions.color(getColor(R.color.green_500))
                        polylineOptions.width(7f)
                        polyline = map.addPolyline(polylineOptions)
                        setMapBounds(points[0], points[points.size - 1])
                        drawCircle()

                    }
                }

            }

        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        Log.e("TAG", "language:${Locale.getDefault().language} ")

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun drawCircle() {
        val circleOptions = CircleOptions()
            .center(viewModel.userLocation!!)
            .radius(40.0) // radius in meters
            .fillColor(getColor(R.color.dot_marker_color)) //this is a half transparent blue, change "88" for the transparency
            .strokeColor(Color.WHITE) //The stroke (border) is blue
            .strokeWidth(6F) // The width is in pixel, so try it!

        map.addCircle(circleOptions)
    }


    override fun onMapReady(p0: GoogleMap) {
        map = p0
        val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.custom_map_style)
        map.setMapStyle(mapStyleOptions)

    }

    private fun drawMarker(point1: LatLng, point2: LatLng) {
        map.addMarker(MarkerOptions().position(point1))
        map.addMarker(MarkerOptions().position(point2))
        setMapBounds(point1, point2)

    }

    private fun setMapBounds(point1: LatLng, point2: LatLng) {

        val north = maxOf(point1.latitude, point2.latitude)
        val east = maxOf(point1.longitude, point2.longitude)
        val south = minOf(point1.latitude, point2.latitude)
        val west = minOf(point1.longitude, point2.longitude)
        val bounds = LatLngBounds(
            LatLng(south, west),
            LatLng(north, east)
        )
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.center,14f))
        //map1.animate
        // map2.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 60))
    }

    fun showDialog() {
        val dialog = MaterialAlertDialogBuilder(this)
        val binding = StationDialogBinding.inflate(layoutInflater)
        addDialogInfo(binding)
        dialog.setView(binding.root)

        dialog.setTitle("Change ride destination ?")
        dialog.setMessage("we provide you the two nearest stations according to your location")
        dialog.setPositiveButton("choose") { dialog, which ->
            val checkedRadioButtonId = binding.radioGroup.checkedRadioButtonId
            dialog.dismiss()
            checkSelection(checkedRadioButtonId)
        }
        dialog.setNegativeButton("cancel") { dialog, which ->
            dialog.dismiss()
        }
        dialog.setIcon(R.drawable.ic_language_24)
        dialog.show()
    }

    private fun addDialogInfo(binding: StationDialogBinding) {
        val text1 = viewModel.nearestStations.value?.get(0)?.name
        val text2 = viewModel.nearestStations.value?.get(1)?.name
        val time1 = viewModel.nearestStations.value?.get(0)?.time
        val time2 = viewModel.nearestStations.value?.get(1)?.time
        binding.station1.text = text1
        binding.station2.text = text2
        if (viewModel.selectedStation == 0)
            binding.radioGroup.check(binding.station1.id)
        else
            binding.radioGroup.check(binding.station2.id)

    }

    private fun checkSelection(newCheckedRadioButtonId: Int) {
        if (viewModel.selectedStation == 0 && newCheckedRadioButtonId != R.id.station1) {
            viewModel.selectedStation = 1
            viewModel.updatePolyline2()
        } else if (viewModel.selectedStation == 1 && newCheckedRadioButtonId != R.id.station2) {
            viewModel.selectedStation = 0
            viewModel.updatePolyline2()
        } else;
    }

    fun haversine(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        // distance between latitudes and longitudes
        var lat1 = lat1
        var lat2 = lat2
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        // convert to radians
        lat1 = Math.toRadians(lat1)
        lat2 = Math.toRadians(lat2)

        // apply formulae
        val a = Math.pow(Math.sin(dLat / 2), 2.0) +
                Math.pow(Math.sin(dLon / 2), 2.0) *
                Math.cos(lat1) *
                Math.cos(lat2)
        val rad = 6371.0
        val c = 2 * Math.asin(Math.sqrt(a))
        return rad * c * 1000
    }


    private fun isLocationSetup(action: () -> Unit): Boolean {

        if (permission.isLocationPermissionEnabled()) {
            if (permission.isGpsOpen())
                return true
            else {
                permission.openGps(action, false)
            }
        } else {
            permission.requestLocationPermission(action)
        }
        return false
    }

    val getLocationFun: () -> Unit = {
        getLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (!isLocationSetup(getLocationFun))
            return

        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )

            return

        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location? ->
                if (location == null)
                    Toast.makeText(this, "error location = null", Toast.LENGTH_SHORT).show()
                else {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    viewModel.callDistanceMatrixApi(userLocation)
                    Log.e("TAG", "myLocation:${userLocation} ")
                }

            }

    }


}

//        val myLocation = LatLng(29.322664,31.200781 )
//        val elwasta = LatLng(29.343262, 31.203258)
//        val distance=haversine(elwasta.latitude,elwasta.longitude,myLocation.latitude,myLocation.longitude)
//        Log.e("TAG", "elwasta=$distance" )
//
//        val shader = LatLng(29.327294, 31.197011)
//        val distance1=haversine(shader.latitude,shader.longitude,myLocation.latitude,myLocation.longitude)
//        Log.e("TAG", "shader=$distance1" )
//
//        val qmn_el3rous = LatLng(29.302335, 31.186068)
//        val distance2=haversine(qmn_el3rous.latitude,qmn_el3rous.longitude,myLocation.latitude,myLocation.longitude)
//        Log.e("TAG", "qmn_el3rous=$distance2" )
//
//        val maymoon = LatLng(29.238141, 31.191408)
//        val distance3=haversine(maymoon.latitude,maymoon.longitude,myLocation.latitude,myLocation.longitude)
//        Log.e("TAG", "maymoon=$distance3" )
//
//        val Eshmant = LatLng(29.220333, 31.181162)
//        val distance4=haversine(Eshmant.latitude,Eshmant.longitude,myLocation.latitude,myLocation.longitude)
//        Log.e("TAG", "Eshmant=$distance4" )
//
//        val nasser = LatLng(29.145256, 31.137614)
//        val distance5=haversine(nasser.latitude,nasser.longitude,myLocation.latitude,myLocation.longitude)
//        Log.e("TAG", "nasser=$distance5" )
//
//        val faculity_engineer = LatLng(29.039685, 31.132564)
//        val distance6=haversine(faculity_engineer.latitude,faculity_engineer.longitude,myLocation.latitude,myLocation.longitude)
//        Log.e("TAG", "faculity_engineer=$distance6" )
//
//        val faculity_Cs = LatLng(29.043484, 31.109482)
//        val distance7=haversine(faculity_Cs.latitude,faculity_Cs.longitude,myLocation.latitude,myLocation.longitude)
//        Log.e("TAG", "faculity_Cs=$distance7" )
//
//        val faculity_Education = LatLng(29.038052, 31.122425)
//        val distance8=haversine(faculity_Education.latitude,faculity_Education.longitude,myLocation.latitude,myLocation.longitude)
//        Log.e("TAG", "faculity_Education=$distance8" )
//
//        val faculity_Elnahda = LatLng(29.049163, 31.118871)
//        val distance9=haversine(faculity_Elnahda.latitude,faculity_Elnahda.longitude,myLocation.latitude,myLocation.longitude)
//        Log.e("TAG", "faculity_Elnahda=$distance9" )
