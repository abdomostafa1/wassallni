package com.wassallni.ui.fragment.main_graph

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.wassallni.R
import com.wassallni.data.model.TripUiState
import com.wassallni.data.model.fullTrip
import com.wassallni.databinding.FragmentTripBinding
import com.wassallni.ui.viewmodel.ReservationVM
import kotlinx.coroutines.launch

class TripFragment : Fragment(), OnMapReadyCallback {

    lateinit var binding: FragmentTripBinding
    val viewModel: ReservationVM by activityViewModels()
    private val args: TripFragmentArgs by navArgs()
    lateinit var mapFragment: SupportMapFragment
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTripBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = args.id


        mapFragment = childFragmentManager
            .findFragmentById(R.id.tripMap) as SupportMapFragment

        mapFragment.getMapAsync(this)

        binding.rideStation.setOnClickListener {
            Log.e("TAG", "rideStation.setOnClickListener: ", )
            it.findNavController().navigate(R.id.action_tripFragment_to_stationsFragment)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setFonts()
        }
        viewModel.getTripDetails(id)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tripUiState.collect {
                    Log.e("TAG", "viewModel.fullTrip.collect ")
                    if (it != null) {
                        Log.e("xxxxx", "fullTrip : motherrr fuckeeerrrrr:$fullTrip ")
                        showUiState(it)
                        viewModel.getPolyLine1()
                    }
                }

            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.polyline1.collect { points ->
                    Log.e("TAG", "myyyyyyyyyyy polyline1: ")

                    if (points != null) {
                        Log.e("TAG", "new polyline1: ")
                        val polyline1 = PolylineOptions().addAll(points!!)
//                        polyline1.color(requireActivity().getColor(R.color.blue))
                        polyline1.width(6f)
                        map.addPolyline(polyline1)
                        //map2.addPolyline(polyline1)
                        drawMarker(points[0], points[points.size - 1])
                    }
                }

            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.counter.collect {
                    binding.tripCard.counter.text = String.format(it.toString())
                }
            }
        }
    }

    private fun showUiState(it: TripUiState) {
        binding.tripCard.driver.text=it.driver
        binding.tripCard.date.text=it.fullDate
        binding.tripCard.price.text=it.price.toString()
        binding.shimmerLayout.visibility=View.GONE
        binding.childLayout.visibility=View.VISIBLE
        setOnClickListener()
    }

    fun setOnClickListener() {
        binding.tripCard.addBtn.setOnClickListener {
            viewModel.incrementCounter()
        }
        binding.tripCard.minusBtn.setOnClickListener {
            viewModel.decrementCounter()
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        val mapStyleOptions =
            MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.custom_map_style)
        map.setMapStyle(mapStyleOptions)
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

    @RequiresApi(Build.VERSION_CODES.P)
    fun setFonts() {
        val sfCompactRounded =
            ResourcesCompat.getFont(requireActivity(), R.font.sf_compact_rounded)

//        binding.textView1.typeface = Typeface.create(sfCompactRounded, 100, false)
//        binding.textView2.typeface = Typeface.create(sfCompactRounded, 200, false)
//        binding.textView3.typeface = Typeface.create(sfCompactRounded, 400, false)
//        binding.textView4.typeface = Typeface.create(sfCompactRounded, 700, false)
    }
}