package com.wassallni.ui.fragment.main_graph

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.wassallni.R
import com.wassallni.data.model.FullTrip
import com.wassallni.data.model.uiState.CancelTripUiState
import com.wassallni.databinding.FragmentBookedTripBinding
import com.wassallni.ui.viewmodel.PassengerTripDetailsVM
import com.wassallni.utils.DateUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

@AndroidEntryPoint
class PassengerTripDetailsFragment : Fragment() ,OnMapReadyCallback {


    lateinit var binding: FragmentBookedTripBinding
    private val args:PassengerTripDetailsFragmentArgs by navArgs()
    private val viewModel:PassengerTripDetailsVM by viewModels()
    private lateinit var mapFragment: SupportMapFragment
    lateinit var map: GoogleMap
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding=FragmentBookedTripBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!args.upcomingTrip)
            binding.actionsLayout.visibility=View.GONE

        mapFragment = childFragmentManager
            .findFragmentById(R.id.bookedTripMap) as SupportMapFragment

        mapFragment.getMapAsync(this)

        binding.centerView.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)

        viewModel.getTripDetails(args.tripId)
        setOnClickListeners()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fullTrip.collect {
                    if (it != null)
                        showUiState(it)

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.polyline1.collect { points ->
                    if (points != null) {
                        Log.e("TAG", "new polyline1: ")
                        val polyline1 = PolylineOptions().addAll(points)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            polyline1.color(requireActivity().getColor(R.color.blue))
                        }
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

    override fun onMapReady(p0: GoogleMap) {
        map=p0
    }

    private fun showUiState(it: FullTrip) {

        binding.loadingState.visibility=View.GONE
        binding.tripView.tvPrice.visibility=View.INVISIBLE
        binding.tripView.start.text=it.start
        binding.tripView.destination.text=it.destination
        binding.tripView.startTime.text=DateUseCase.convertDateToHhMma(it.startTime)
        binding.tripView.endTime.text=DateUseCase.convertDateToHhMma(it.endTime)
        binding.tripView.date.text=DateUseCase.convertDateToYyMmDd(it.endTime)

        binding.ticket.text="${args.ticket}"
        binding.rideStation.text= it.stations[args.point].name
        binding.rideTime.text= DateUseCase.convertDateToHhMma(it.stations[args.point].time)
        binding.seatsNum.text="${args.numOfSeat}"
        binding.price.text="${it.price}"
        binding.totalPrice.text="${args.numOfSeat*it.price}"

    }

    private fun drawMarker(point1: LatLng, point2: LatLng) {
        map.addMarker(MarkerOptions().position(point1))
        //map2.addMarker(MarkerOptions().position(origin).title("الواسطي"))
        map.addMarker(MarkerOptions().position(point2))
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
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.center, 9.9f))
        //map1.animate
        // map2.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 60))

    }

    private fun setOnClickListeners() {
        binding.cancelTrip.setOnClickListener {
            viewModel.cancelTrip(args.id)
        }

        binding.call.setOnClickListener {
            val phoneIntent= Intent(Intent.ACTION_DIAL)
            val phoneNumber="01119499687"
            phoneIntent.data = Uri.parse("tel:$phoneNumber")
            startActivity(phoneIntent)
        }
        binding.toStation.setOnClickListener {
            val location= viewModel.fullTrip.value?.stations?.get(args.point)?.location!!
            openGoogleMapDirections(LatLng(location.lat,location.lng))
        }
    }

    private val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val height = binding.centerView.height
        Log.e("TAG", "height=$height")
        val behaviour = BottomSheetBehavior.from(binding.bottomSheet)
        behaviour.peekHeight = height
    }

    private fun openGoogleMapDirections(location: LatLng) {
        // Create a Uri from an intent string. Use the result to create an Intent.
        val gmmIntentUri =
            Uri.parse("google.navigation:q=${location.latitude},${location.longitude}")

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps")

        mapIntent.resolveActivity(requireActivity().packageManager)?.let {
            startActivity(mapIntent)
        }
        // Attempt to start an activity that can handle the Intent

    }

    private fun showLoadingState(){
        binding.loadingState.visibility=View.VISIBLE
    }

    private fun onCancelSuccess() {
        with(findNavController()) {
            previousBackStackEntry?.savedStateHandle?.set("IS_CANCELLED",true)
            navigateUp()
        }
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            getString(R.string.cancel_success), Snackbar.LENGTH_LONG
        ).show()


    }

    private fun onCancelFailed(msg:String) {
        binding.loadingState.visibility=View.GONE
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            msg, Snackbar.LENGTH_LONG
        ).show()
    }
}
