package com.wassallni.ui.fragment.main_graph

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.wassallni.BuildConfig
import com.wassallni.R
import com.wassallni.databinding.FragmentUserLocationBinding
import com.wassallni.ui.viewmodel.ReservationVM
import com.wassallni.utils.Permissions
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

private const val TAG = "UserLocationFragment"
@AndroidEntryPoint
class UserLocationFragment : Fragment() {

    lateinit var binding:FragmentUserLocationBinding
    val viewModel :ReservationVM by navGraphViewModels(R.id.trip_graph)
    @Inject
    lateinit var permission: Permissions
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var nextAction:()->Unit= { }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentUserLocationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countryCode=getUserCountryCode()
        if (!Places.isInitialized()) {
            Places.initialize(requireActivity().applicationContext, BuildConfig.MAPS_API_KEY, Locale.getDefault());
        }

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setActivityMode(AutocompleteActivityMode.FULLSCREEN)
        autocompleteFragment.setCountry(countryCode)
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                viewModel.userLocation.placeId=place.id
                findNavController().navigate(R.id.action_userLocationFragment_to_reservationFragment)

            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: $status")
            }
        })

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.currentLocationCard.setOnClickListener {
            nextAction=navigateToReservationFragment
            getLocation()
        }
        binding.mapCard.setOnClickListener {
            nextAction=navigateToMapFragment
            getLocation()
        }
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

    private val getLocationFun: () -> Unit = {
        getLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (!isLocationSetup(getLocationFun))
            return

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )

            return

        binding.progressBar.visibility=View.VISIBLE
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                    binding.progressBar.visibility=View.GONE
                    return CancellationTokenSource().token
                }

                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location? ->
                binding.progressBar.visibility=View.GONE
                if (location == null)
                    Toast.makeText(requireActivity(), "error location = null", Toast.LENGTH_SHORT)
                        .show()
                else {
                    val location = LatLng(location.latitude, location.longitude)
                    viewModel.userLocation.coordinates=location
                    Log.e("TAG", "myLocation:${location} ")
                    nextAction.invoke()
                }

            }

    }

    private val navigateToReservationFragment:()->Unit ={
        findNavController().navigate(R.id.action_userLocationFragment_to_reservationFragment)
    }

    val navigateToMapFragment:()->Unit ={
        findNavController().navigate(R.id.action_userLocationFragment_to_mapsFragment)
    }

    private fun getUserCountryCode() :String{
        val telephonyManager = requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val code = telephonyManager.networkCountryIso
        val config = requireActivity().resources.configuration
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.locales.get(0)
        } else {
            config.locale
        }
        var countryCode  = if(code!=null)
            code
        else
            locale.country

        Log.e(TAG, "countryCode:$countryCode ", )
        return countryCode
    }
}