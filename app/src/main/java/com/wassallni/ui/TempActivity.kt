package com.wassallni.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.wassallni.R
import com.wassallni.data.model.FullTrip
import com.wassallni.data.model.LoggedInUser
import com.wassallni.data.model.uiState.CancelTripUiState
import com.wassallni.databinding.ActivityTempBinding
import com.wassallni.ui.viewmodel.BookedTripVM
import com.wassallni.utils.DateUseCase
import com.wassallni.utils.Permissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.*


private const val TAG = "TempActivity"

@AndroidEntryPoint
class TempActivity : AppCompatActivity() ,OnMapReadyCallback {

    lateinit var binding: ActivityTempBinding
    private lateinit var mapFragment: SupportMapFragment

    @Inject
    lateinit var permission: Permissions

    @Inject
    lateinit var loggedInUser: LoggedInUser
    private val viewModel: BookedTripVM by viewModels()
    lateinit var map: GoogleMap
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


        mapFragment = supportFragmentManager
            .findFragmentById(R.id.bookedTripMap) as SupportMapFragment

        mapFragment.getMapAsync(this)

        binding.centerView.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)

        viewModel.getTripDetails("args.tripId")

        setOnClickListeners()

        Log.e(TAG, "token:${loggedInUser.getToken()} ")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fullTrip.collect {
                    if (it != null)
                        showUiState(it)

                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.polyline1.collect { points ->
                    if (points != null) {
                        Log.e("TAG", "new polyline1: ")
                        val polyline1 = PolylineOptions().addAll(points!!)
                        polyline1.color(getColor(R.color.blue))
                        polyline1.width(7f)
                        map.addPolyline(polyline1)
                        drawMarker(points[0], points[points.size - 1])
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cancelRequest.collect { state ->
                    when (state) {
                        is CancelTripUiState.Loading -> {
                            showLoadingState()
                        }
                        is CancelTripUiState.Success -> {
                            onCancelSuccess()
                        }
                        is CancelTripUiState.Error -> {
                            onCancelFailed(state.errorMsg)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.cancelTrip.setOnClickListener {
            viewModel.cancelTrip("id")
        }

        binding.call.setOnClickListener {
            val phoneIntent=Intent(Intent.ACTION_DIAL)
            val phoneNumber="01119499687"
            phoneIntent.data = Uri.parse("tel:$phoneNumber")
            startActivity(phoneIntent)
        }
        binding.toStation.setOnClickListener {
            val location= viewModel.fullTrip.value?.stations?.get(1)?.location!!
            openGoogleMapDirections(LatLng(location.lat,location.lng))
        }
    }

    val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val height = binding.centerView.height
        Log.e("TAG", "height=$height")
        val behaviour = BottomSheetBehavior.from(binding.bottomSheet)
        behaviour.peekHeight = height
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
    }

    private fun openGoogleMapDirections(location: LatLng) {
        val latitude = location.latitude.toString()
        // Create a Uri from an intent string. Use the result to create an Intent.
        val gmmIntentUri =
            Uri.parse("google.navigation:q=${location.latitude},${location.longitude}")

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps")

        mapIntent.resolveActivity(packageManager)?.let {
            startActivity(mapIntent)
        }
        // Attempt to start an activity that can handle the Intent

    }

    private fun showUiState(it: FullTrip) {
        binding.loadingState.root.visibility=View.GONE
        binding.tripView.tvPrice.visibility = View.INVISIBLE
        binding.tripView.start.text = it.start
        binding.tripView.destination.text = it.destination
        binding.tripView.startTime.text = DateUseCase.fromMillisToString1(it.startTime)
        binding.tripView.endTime.text = DateUseCase.fromMillisToString1(it.endTime)
        binding.tripView.date.text = DateUseCase.fromMillisToString3(it.endTime)

        binding.rideStation.text = it.stations[1].name
        binding.rideTime.text = DateUseCase.fromMillisToString1(it.stations[1].time)
        binding.seatsNum.text = "2"
        binding.price.text = "${it.price}"
        binding.totalPrice.text = "${it.price*2}"
    }

    private fun showLoadingState(){
        binding.loadingState.root.visibility=View.VISIBLE
    }

    private fun onCancelSuccess() {
        Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.cancel_success), Snackbar.LENGTH_LONG
        ).show()

    }

    private fun onCancelFailed(msg:String) {
        binding.loadingState.root.visibility=View.GONE
        Snackbar.make(
            findViewById(android.R.id.content),
            msg, Snackbar.LENGTH_LONG
        ).show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun drawCircle() {
        val circleOptions = CircleOptions()
            //.center(viewModel.userLocation!!)
            .radius(40.0) // radius in meters
            .fillColor(getColor(R.color.dot_marker_color)) //this is a half transparent blue, change "88" for the transparency
            .strokeColor(Color.WHITE) //The stroke (border) is blue
            .strokeWidth(6F) // The width is in pixel, so try it!

        //map.addCircle(circleOptions)
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
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.center, 8f))
        //map1.animate
        // map2.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 60))
    }

    private fun drawMarker(point1: LatLng, point2: LatLng) {
        map.addMarker(MarkerOptions().position(point1))
        //map2.addMarker(MarkerOptions().position(origin).title("الواسطي"))
        map.addMarker(MarkerOptions().position(point2))
        //map2.addMarker(MarkerOptions().position(destination).title("تعليم صناعي"))
        setMapBounds(point1, point2)

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
        val a = sin(dLat / 2).pow(2.0) +
                sin(dLon / 2).pow(2.0) *
                cos(lat1) *
                cos(lat2)
        val rad = 6371.0
        val c = 2 * asin(sqrt(a))
        return rad * c * 1000
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
