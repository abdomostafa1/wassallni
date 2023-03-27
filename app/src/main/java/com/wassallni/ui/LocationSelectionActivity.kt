package com.wassallni.ui

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.wassallni.R
import com.wassallni.data.model.PlaceInfo
import com.wassallni.databinding.ActivityLocationSelectionBinding
import com.wassallni.ui.viewmodel.LocationSelectionViewModel
import com.wassallni.utils.BroadcastObserver
import com.wassallni.utils.BroadcastReceiver
import com.wassallni.utils.Permissions
import java.io.Serializable
import java.util.*


class LocationSelectionActivity : AppCompatActivity(), OnMapReadyCallback, BroadcastObserver {

    private lateinit var binding: ActivityLocationSelectionBinding
    lateinit var myViewModel: LocationSelectionViewModel
    private lateinit var googleMap: GoogleMap
    lateinit var mapFragment: SupportMapFragment
    lateinit var permissions: Permissions
    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    var autocompletePredictionsMode: Boolean = true
    val FOCUS_IN_ORIGIN_EDT: Int = 1
    val FOCUS_IN_Destination_EDT: Int = 2
    val FOCUS_IN_Parent: Int = 3

    lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var intentFilter: IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.selection_map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_map_api_key), Locale.US);
        }

        myViewModel = LocationSelectionViewModel(this)

        myViewModel.yourLocationEdt.observe(this) {
            autocompletePredictionsMode = false
            if (it != null) {
                binding.edtYourLocation.setText(it)
                binding.edtYourLocation.setSelection(it.length)
            }
        }
        myViewModel.whereToEdt.observe(this) {
            autocompletePredictionsMode = false
            if (it != null) {
                binding.edtWhereTo.setText(it)
                binding.edtWhereTo.setSelection(it.length)
            }
        }
        myViewModel.isAvailable.observe(this) {
            if (it == true)
                submitOrder()
            else
                onServiceNotAvailable()
        }
        myViewModel.mapIsMoving.observe(this) {

            autocompletePredictionsMode = false
            (currentFocus as TextInputEditText).setText("Loading")
        }

        permissions = Permissions(this)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.edtWhereTo.addTextChangedListener(whereToTextWatcher)
        binding.edtYourLocation.addTextChangedListener(yourLocationTextWatcher)
        broadcastReceiver = BroadcastReceiver(this)
        intentFilter = IntentFilter("android.location.PROVIDERS_CHANGED")
        setOnclickListeners()


    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            return
        //googleMap.isMyLocationEnabled = true

        setMapListeners()
    }

    private fun setOnclickListeners() {
        binding.submit.setOnClickListener {
            validateSubmit()
        }

        binding.navigationFab.setOnClickListener {
            finish()
        }
        binding.place1.setOnClickListener {
            pickLocation(0)
        }
        binding.place2.setOnClickListener {
            pickLocation(1)
        }
        binding.place3.setOnClickListener {
            pickLocation(2)
        }
        binding.place4.setOnClickListener {
            pickLocation(3)
        }
        binding.place5.setOnClickListener {
            pickLocation(4)
        }

        //var r=SharedPreferences

    }

    private fun setMapListeners() {
        googleMap.setOnCameraIdleListener {
            var centerLatLang = googleMap.projection.visibleRegion.latLngBounds.center
            try {
                myViewModel.getAddressFromLatLng(centerLatLang, getFocus())
            } catch (throwable: Throwable) {
                Toast.makeText(this, throwable.message, Toast.LENGTH_LONG).show()
                Log.e("Toast :", " 2" )
            }
        }

        googleMap.setOnCameraMoveStartedListener {
            myViewModel.onMapMoving(getFocus())
        }
    }

    private fun getFocus(): Int {
        return if (binding.edtYourLocation.hasFocus())
            FOCUS_IN_ORIGIN_EDT
        else if(binding.edtWhereTo.hasFocus())
            FOCUS_IN_Destination_EDT
        else
            FOCUS_IN_Parent
    }

    private fun pickLocation(index: Int) {
        myViewModel.pickLocation(index, getFocus())
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onGpsBroadcastResponse() {
      //  if (!permissions.isGpsOpen())
         //   permissions.openGps(false)
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.unavailable_dialog)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val button = dialog.findViewById<MaterialButton>(R.id.ok_btn)
        button.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun findAutocompletePredictions(query: String) {
        if (query.isEmpty()) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            return
        }

        binding.place1.visibility = View.GONE
        binding.place2.visibility = View.GONE
        binding.place3.visibility = View.GONE
        binding.place4.visibility = View.GONE
        binding.place5.visibility = View.GONE

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        myViewModel.findAutocompletePredictions(query) {
            showPredictedPlaces(it)
        }
    }

    fun showPredictedPlaces(predictedPlaces: List<PlaceInfo>) {

        if (predictedPlaces.isEmpty())
            return
        binding.plc1PrimaryTxt.text = predictedPlaces[0].primaryName
        binding.plc1SecondaryTxt.text = predictedPlaces[0].address
        binding.place1.visibility = View.VISIBLE

        if (predictedPlaces.size == 1)
            return

        binding.plc2PrimaryTxt.text = predictedPlaces[1].primaryName
        binding.plc2SecondaryTxt.text = predictedPlaces[1].address
        binding.place2.visibility = View.VISIBLE

        if (predictedPlaces.size == 2)
            return

        binding.plc3PrimaryTxt.text = predictedPlaces[2].primaryName
        binding.plc3SecondaryTxt.text = predictedPlaces[2].address
        binding.place3.visibility = View.VISIBLE

        if (predictedPlaces.size == 3)
            return

        binding.plc4PrimaryTxt.text = predictedPlaces[3].primaryName
        binding.plc4SecondaryTxt.text = predictedPlaces[3].address
        binding.place4.visibility = View.VISIBLE

        if (predictedPlaces.size == 4)
            return

        binding.plc5PrimaryTxt.text = predictedPlaces[4].primaryName
        binding.plc5SecondaryTxt.text = predictedPlaces[4].address
        binding.place5.visibility = View.VISIBLE
    }

    private fun validateSubmit() {
        if (isFieldEmpty())
            return

        try {
            myViewModel.isServiceAvailable()
        } catch (exception: Throwable) {
            if (exception.message!=null)
            Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            Log.e("Toast :", " 1" )
        }
    }

    private fun isFieldEmpty(): Boolean {
        if (myViewModel.isOriginEmpty())
            binding.edtYourLocation.requestFocus()
        else if (myViewModel.isDestinationEmpty())
            binding.edtWhereTo.requestFocus()
        else
            return false

        return true
    }

    private fun submitOrder() {
        Log.e("TAG", "onServiceAvailable: ")
        val origin = myViewModel.retrieveOrigin()
        val destination = myViewModel.retrieveDestination()
        val intent = Intent(this, DriverRequest::class.java)
        intent.putExtra("origin", origin as Serializable)
        intent.putExtra("destination", destination as Serializable)
        startActivity(intent)
    }

    private fun onServiceNotAvailable() {
        Log.e("TAG", "onServiceUnavailable ")
        showDialog()
    }

    private val yourLocationTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            if (autocompletePredictionsMode) {
                val query = p0.toString()
                findAutocompletePredictions(query)
            }
            autocompletePredictionsMode = true
        }
    }
    private val whereToTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            if (autocompletePredictionsMode) {
                val query = p0.toString()
                findAutocompletePredictions(query)
            }
            autocompletePredictionsMode = true
        }
    }

    private fun registerReceiver() {
        registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun unRegisterReceiver() {
        unregisterReceiver(broadcastReceiver)
    }

    override fun onStart() {
        super.onStart()
        registerReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterReceiver()
    }
}
