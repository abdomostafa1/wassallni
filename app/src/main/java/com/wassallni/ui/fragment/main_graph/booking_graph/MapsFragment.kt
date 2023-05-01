package com.wassallni.ui.fragment.main_graph.booking_graph

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import android.Manifest
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wassallni.R
import com.wassallni.databinding.FragmentMapsBinding
import com.wassallni.ui.viewmodel.ReservationVM
import kotlinx.coroutines.launch


class MapsFragment : Fragment(), OnMapReadyCallback {

    lateinit var binding: FragmentMapsBinding
    private lateinit var mapFragment: SupportMapFragment
    lateinit var map: GoogleMap
    private val viewModel: ReservationVM by navGraphViewModels(R.id.trip_graph) { defaultViewModelProviderFactory }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment = childFragmentManager.findFragmentById(R.id.choose_map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.address.collect {
                    binding.address.text = it
                }
            }
        }

        binding.Continue.setOnClickListener {
            findNavController().navigate(R.id.action_mapsFragment_to_reservationFragment)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        val bottomSheetBehavior=BottomSheetBehavior.from(binding.bottomSheet)
        map.setOnCameraIdleListener {
            bottomSheetBehavior.state=BottomSheetBehavior.STATE_EXPANDED
            val center: LatLng = map.cameraPosition.target
            viewModel.userLocation.coordinates = center
            viewModel.callGeocodeApi()
        }

        map.setOnCameraMoveStartedListener {
            bottomSheetBehavior.state=BottomSheetBehavior.STATE_HIDDEN
        }
        addLocationControllers()
    }

    private fun addLocationControllers() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Enable the my location button and layer on the map
            map.isMyLocationEnabled = true

            val uiSettings: UiSettings = map.uiSettings

            // Enable the zoom controls on the map
            // Enable the zoom controls on the map
            uiSettings.isZoomControlsEnabled = true
            // Get the user's current location
            val locationManager =
                requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            val lastKnownLocation: Location? =
                locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                // Move the camera to the user's current location
                val latLng =
                    LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }
}