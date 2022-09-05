package com.wassallni

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wassallni.databinding.ActivityLocationSelectionBinding
import java.util.*
import kotlin.collections.ArrayList

class LocationSelectionActivity : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var binding:ActivityLocationSelectionBinding
    private lateinit var mMap: GoogleMap
    lateinit var mapFragment: SupportMapFragment
    private lateinit var token:AutocompleteSessionToken
    private val predictedPlaces=ArrayList<PredictedPlaceInfo>()
    lateinit var bottomSheetBehavior:BottomSheetBehavior<View>
    lateinit var placesClient:PlacesClient
    var query :String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLocationSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mapFragment = supportFragmentManager
            .findFragmentById(R.id.selection_map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        bottomSheetBehavior= BottomSheetBehavior.from(binding.standardBottomSheet)
        bottomSheetBehavior.state=BottomSheetBehavior.STATE_HIDDEN
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_map_api_key), Locale.US);
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)

        setOnclickListeners()

//
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_SETTLING
        token = AutocompleteSessionToken.newInstance()
        placesClient=Places.createClient(this)
        binding.edtWhereTo.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                query=p0.toString()
                requestPlaces()
            }
        })
        binding.edtYourLocation.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                query=p0.toString()
                requestPlaces()
            }
        })


    }

    override fun onMapReady(p0: GoogleMap) {
        mMap=p0


    }

    private fun setOnclickListeners(){
        binding.place1.setOnClickListener {
            showDialog(predictedPlaces[0])
        }
        binding.place2.setOnClickListener {
            showDialog(predictedPlaces[1])
        }
        binding.place3.setOnClickListener {
            showDialog(predictedPlaces[2])
        }
        binding.place4.setOnClickListener {
            showDialog(predictedPlaces[3])
        }
        binding.place5.setOnClickListener {
                showDialog(predictedPlaces[4])
        }

    }

    private fun showDialog(place:PredictedPlaceInfo){
        val builder = AlertDialog.Builder(this)

        builder.setMessage(place.secondaryName)
            .setTitle(place.primaryName)

        builder.setPositiveButton("ok", DialogInterface.OnClickListener(){ dialog, which ->

            Toast.makeText(this,"Ok man",Toast.LENGTH_LONG).show()

        })

        builder.show()
    }
    fun requestPlaces(){
        Log.e("requestPlaces ","")

        val request =
            FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().

                .setTypeFilter(TypeFilter.ADDRESS)
                //.setCountries("EG")
                .setSessionToken(token)
                .setQuery(query)
                .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->

                binding.place1.visibility= View.GONE
                binding.place2.visibility= View.GONE
                binding.place3.visibility= View.GONE
                binding.place4.visibility= View.GONE
                binding.place5.visibility= View.GONE

                bottomSheetBehavior.state=BottomSheetBehavior.STATE_HALF_EXPANDED
                predictedPlaces.clear()
                for (prediction in response.autocompletePredictions) {
                    Log.e("prediction.toString() ", prediction.toString())
                    Log.e("PrimaryText ", prediction.getPrimaryText(null).toString())
                    val primaryName=prediction.getPrimaryText(null).toString()
                    val secondaryName=prediction.getSecondaryText(null).toString()
                    val placeId=prediction.placeId
                    val predictedPlaceInfo=PredictedPlaceInfo(primaryName,secondaryName,placeId)


                    predictedPlaces.add(predictedPlaceInfo)
                }
                showPredictedPlaces()
            }.addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    Log.e("Place not found:", " " + exception.statusCode)
                }
            }

    }

    fun showPredictedPlaces(){

        binding.plc1PrimaryTxt.text=predictedPlaces[0].primaryName
        binding.plc1SecondaryTxt.text=predictedPlaces[0].secondaryName
        binding.place1.visibility= View.VISIBLE

        binding.plc2PrimaryTxt.text=predictedPlaces[1].primaryName
        binding.plc2SecondaryTxt.text=predictedPlaces[1].secondaryName
        binding.place2.visibility= View.VISIBLE

        binding.plc3PrimaryTxt.text=predictedPlaces[2].primaryName
        binding.plc3SecondaryTxt.text=predictedPlaces[2].secondaryName
        binding.place3.visibility= View.VISIBLE

        binding.plc4PrimaryTxt.text=predictedPlaces[3].primaryName
        binding.plc4SecondaryTxt.text=predictedPlaces[3].secondaryName
        binding.place4.visibility= View.VISIBLE

        binding.plc5PrimaryTxt.text=predictedPlaces[4].primaryName
        binding.plc5SecondaryTxt.text=predictedPlaces[4].secondaryName
        binding.place5.visibility= View.VISIBLE


    }
}