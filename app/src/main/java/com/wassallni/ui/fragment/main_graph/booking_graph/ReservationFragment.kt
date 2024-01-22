package com.wassallni.ui.fragment.main_graph.booking_graph

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.wassallni.R
import com.wassallni.data.model.uiState.ReservationUiState
import com.wassallni.databinding.FragmentReservationBinding
import com.wassallni.databinding.StationDialogBinding
import com.wassallni.ui.viewmodel.BookVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReservationFragment : Fragment(), OnMapReadyCallback {


    lateinit var binding: FragmentReservationBinding
    private lateinit var mapFragment: SupportMapFragment
    lateinit var map: GoogleMap
    private var stationMarker :Marker?=null

    private var polyline: Polyline? = null
    private val viewModel: BookVM by navGraphViewModels(R.id.trip_graph) { defaultViewModelProviderFactory }

    val getContent = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { it
        // Handle the returned Uri

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReservationBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment = childFragmentManager.findFragmentById(R.id.book_map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        viewModel.callDistanceMatrixApi()

        binding.changeStation.setOnClickListener {
            showDialog()
        }
        binding.submit.setOnClickListener {
            viewModel.bookTrip()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.nearestStations.collect { it ->
                    if (it != null) {
                        binding.calculatingLayout.visibility = View.GONE
                        binding.responseLayout.visibility = View.VISIBLE
                        binding.changeStation.visibility = View.VISIBLE
                        if (viewModel.movementMode=="driving")
                            binding.icDriving.visibility = View.VISIBLE
                        else
                            binding.icWalk.visibility = View.VISIBLE
                        binding.time.text = it[0].time
                        binding.station.text = it[0].name

                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.polyline2.collect { points ->
                    if (points != null) {
                        if (polyline != null) { //update polyline
                            polyline?.points = points
                            setMapBounds(points[0], points[points.size - 1], 13f)
                            return@collect
                        }
                        drawPolyline2(points)

                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.reservationUiState.collect {
                    when (it) {
                        is ReservationUiState.Loading -> {
                            showLoadingState()
                        }
                        is ReservationUiState.Error -> {
                            showErrorState()
                            Log.e("TAG", "it.errorMsg:${it.error}" )
                            Toast.makeText(requireActivity(), it.error.msg, Toast.LENGTH_LONG).show()
                        }
                        is ReservationUiState.Success -> {
                            showSuccessState()
                            Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                getString(R.string.trip_booked_successfully), Snackbar.LENGTH_LONG
                            ).show();

                            findNavController().navigate(R.id.action_reservationFragment_to_mainFragment)
                        }
                        else -> {}
                    }
                }
            }

        }
    }

    private fun drawPolyline2(points: List<LatLng>) {
        Log.e("TAG", "polyline2: ")
        val polylineOptions = PolylineOptions()
        polylineOptions.addAll(points)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            polylineOptions.color(requireActivity().getColor(R.color.start_circle))
        }
        polylineOptions.width(7f)
        polyline = map.addPolyline(polylineOptions)
        setMapBounds(points[0], points[1], 13f)
        drawCircle(points[0], points[points.size - 1])
    }


    private fun drawCircle(point1: LatLng, point2: LatLng) {

        map.addMarker(MarkerOptions().position(point1).icon(bitmapDescriptorFromVector(requireActivity(),R.drawable.custom_rec)))
        stationMarker=map.addMarker(MarkerOptions().position(point2).icon(bitmapDescriptorFromVector(requireActivity(),R.drawable.custom_rec)))
    }


    override fun onMapReady(p0: GoogleMap) {
        map = p0
        val mapStyleOptions =
            MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.custom_map)
        map.setMapStyle(mapStyleOptions)

        val points = viewModel.polyline1.value
        if (points != null) {
            Log.e("TAG", "new polyline1: ")
            val polylineOptions = PolylineOptions().addAll(points!!)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                polylineOptions.color(requireActivity().getColor(R.color.blue))
            }
            polylineOptions.width(8f)
            map.addPolyline(polylineOptions)
            drawMarker(points[0], points[points.size - 1])
        }

    }

    private fun drawMarker(point1: LatLng, point2: LatLng) {
        map.addMarker(MarkerOptions().position(point1))
        map.addMarker(MarkerOptions().position(point2))
        setMapBounds(point1, point2, 10f)

    }

    private fun setMapBounds(point1: LatLng, point2: LatLng, zoomLevel: Float) {

        val north = maxOf(point1.latitude, point2.latitude)
        val east = maxOf(point1.longitude, point2.longitude)
        val south = minOf(point1.latitude, point2.latitude)
        val west = minOf(point1.longitude, point2.longitude)
        val bounds = LatLngBounds(
            LatLng(south, west),
            LatLng(north, east)
        )
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.center, zoomLevel))
        //map1.animate
        // map2.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 60))
    }

    private fun showDialog() {
        val dialog = MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.MaterialAlertDialog_MaterialComponents
        )
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
        dialog.show()
    }

    private fun addDialogInfo(binding: StationDialogBinding) {
        val text1 = viewModel.nearestStations.value?.get(0)?.name
        val text2 = viewModel.nearestStations.value?.get(1)?.name
        val time1 = viewModel.nearestStations.value?.get(0)?.time
        val time2 = viewModel.nearestStations.value?.get(1)?.time
        binding.station1.text = text1
        binding.station2.text = text2
        binding.time1.text=time1
        binding.time2.text=time2

        if (viewModel.movementMode=="driving")
            binding.walkLayout.visibility = View.INVISIBLE
        else
            binding.drivingLayout.visibility = View.INVISIBLE

        Log.e("TAG", "selectedStation:${viewModel.selectedStation} ", )
        if (viewModel.selectedStation == 0)
            binding.radioGroup.check(binding.station1.id)

        else
            binding.radioGroup.check(binding.station2.id)

    }

    private fun checkSelection(newCheckedRadioButtonId: Int) {
        if (viewModel.selectedStation == 0 && newCheckedRadioButtonId != R.id.station1) {
            viewModel.selectedStation = 1
            viewModel.updatePolyline2()
            updateUi(1)
            updateMarkers()

        } else if (viewModel.selectedStation == 1 && newCheckedRadioButtonId != R.id.station2) {
            viewModel.selectedStation = 0
            viewModel.updatePolyline2()
            updateUi(0)
            updateMarkers()
            binding.station.text=viewModel.nearestStations.value?.get(0)?.name
        } else;
    }

    private fun updateUi(index:Int){
        val name=viewModel.nearestStations.value?.get(index)?.name
        val time=viewModel.nearestStations.value?.get(index)?.time
        binding.station.text=name
        binding.time.text=time
    }

    private fun updateMarkers(){
        val newPosition= viewModel.nearestStations.value?.get(viewModel.selectedStation)?.location
        Log.e("TAG", "finishMarker.position before:${stationMarker?.position} ", )

        if (newPosition != null) {
            stationMarker?.position=LatLng(newPosition.lat,newPosition.lng)
            Log.e("TAG", "finishMarker.position after:${stationMarker?.position} ", )
        }
    }

    fun bitmapDescriptorFromVector(
        context: Context,
        vectorResId: Int
    ): BitmapDescriptor? {

        // retrieve the actual drawable
        val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        val bm = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // draw it onto the bitmap
        val canvas = android.graphics.Canvas(bm)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bm)
    }

    private fun showLoadingState(){
        binding.loadingState.root.visibility=View.VISIBLE
    }

    private fun showSuccessState(){
        binding.loadingState.root.visibility=View.GONE
    }
    private fun showErrorState(){
        binding.loadingState.root.visibility=View.GONE

    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onDestroyReservationFragment()

    }
}