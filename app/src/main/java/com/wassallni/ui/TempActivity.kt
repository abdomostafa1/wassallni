package com.wassallni.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.wassallni.R
import com.wassallni.databinding.ActivityTempBinding
import com.wassallni.ui.viewmodel.ReservationVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class TempActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityTempBinding
    lateinit var map1: GoogleMap

    //lateinit var map2: GoogleMap
    private lateinit var mapFragment: SupportMapFragment

    //private lateinit var silverFragment: SupportMapFragment
    private val viewModel: ReservationVM by viewModels()

    val origin = LatLng(29.343262, 31.203258)
    val destination = LatLng(29.043484, 31.109482)
    val station = LatLng(29.327294, 31.197011)
    val Dash: PatternItem = Dash(20f)
    val DOT: PatternItem = Dot()
    val GAP: PatternItem = Gap(20F)
    val PATTERN_POLYLINE_DASHED = Arrays.asList(DOT, GAP, Dash, GAP)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTempBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getTripDetails("id")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.nearestStation.collect{
                    if(it!=null){
                        binding.firstStation.text=it[0].name
                        binding.secondStation.text=it[1].name
                        binding.textView10.text=it[0].time
                        binding.textView11.text=it[1].time
                    }
                }
            }
        }

        Log.e("TAG", "language:${Locale.getDefault().language} ", )
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

    }


    override fun onMapReady(p0: GoogleMap) {
        map1 = p0
        val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.custom_map_style)
        map1.setMapStyle(mapStyleOptions)
//            viewModel.getPolyLine1()
//            viewModel.getPolyLine2()
        //  map1.moveCamera(CameraUpdateFactory.newLatLngZoom(stokeOnTrent, someZoomLevel))

    }

    private fun drawMarker(point1: LatLng, point2: LatLng) {
        map1.addMarker(MarkerOptions().position(point1))
        //map2.addMarker(MarkerOptions().position(origin).title("الواسطي"))
        map1.addMarker(MarkerOptions().position(point2))
        //map2.addMarker(MarkerOptions().position(destination).title("تعليم صناعي"))
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
        map1.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.center, 9.9f))
        //map1.animate
        // map2.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 60))
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
        return rad * c*1000
    }
}