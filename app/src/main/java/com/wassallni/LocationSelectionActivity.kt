package com.wassallni

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.wassallni.databinding.ActivityLocationSelectionBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.*
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.Serializable
import java.text.DecimalFormat
import java.util.*


class LocationSelectionActivity : AppCompatActivity(), OnMapReadyCallback , BroadcastObserver {

    private lateinit var binding: ActivityLocationSelectionBinding
    private lateinit var googleMap: GoogleMap
    private var currentLocation = PlaceInfo()
    private var destination = PlaceInfo()
    var marker: Marker? = null
    lateinit var mapFragment: SupportMapFragment
    lateinit var permissions: Permissions
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var token: AutocompleteSessionToken
    private val predictedPlaces = ArrayList<PlaceInfo>()
    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    lateinit var placesClient: PlacesClient
    var query: String = ""
    var predictedPlacesSelectionMode: Boolean = true
    var geocoder: Geocoder? = null

    val maximumLng: Double = 31.212882
    val minimumLng: Double = 31.188709
    val minimumLat: Double = 29.308123
    val maximumLat: Double = 29.350525

    lateinit var broadcastReceiver:BroadcastReceiver
    private lateinit var intentFilter: IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.selection_map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        permissions = Permissions(this)
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        if (!Places.isInitialized()) {
            Places.initialize(
                applicationContext,
                getString(R.string.google_map_api_key),
                Locale.US
            );
        }

        setOnclickListeners()

//
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_SETTLING
        token = AutocompleteSessionToken.newInstance()
        placesClient = Places.createClient(this)
        binding.edtWhereTo.addTextChangedListener(whereToTextWatcher)
        binding.edtYourLocation.addTextChangedListener(yourLocationTextWatcher)
        //binding.edtWhereTo.requestFocus()
        broadcastReceiver=BroadcastReceiver(this)
        intentFilter= IntentFilter("android.location.PROVIDERS_CHANGED")

    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        Log.e("TAG", "onMapReady: ")
//        getLocation { latlng: LatLng ->
//            showUserLocationOnMap(latlng, 3500)
//        }

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            return
        googleMap.isMyLocationEnabled = true

        googleMap.setOnCameraIdleListener {

            predictedPlacesSelectionMode = false
            var centerLatLang = googleMap.projection.visibleRegion.latLngBounds.center
            val latlng = "${centerLatLang.latitude},${centerLatLang.longitude}"

//            binding.edtYourLocation.setText(latlng)
            val activePlace=getActiveEdtText()
            activePlace.setLtLng(centerLatLang)

            getAddressFromLatlng(centerLatLang)
        }


        googleMap.setOnCameraMoveStartedListener {
            Log.e("OnCameraMoveListener ", " takeaway ")
            predictedPlacesSelectionMode = false
            var view = currentFocus
            if (view?.id == R.id.edt_where_to)
                binding.edtWhereTo.setText("Loading ...")
            else
                binding.edtYourLocation.setText("Loading ...")
        }

    }

    private fun setOnclickListeners() {
        binding.order.setOnClickListener {
            Log.e("TAG", "order on clickListener ")
            checkValidPlaces()
        }
        binding.myLocation.setOnClickListener {
            getLocation { latlng: LatLng ->
                showUserLocationOnMap(latlng, 2000)
                var activePlace = getActiveEdtText()
                activePlace = PlaceInfo(latlng)

                getAddressFromLatlng(latlng)
            }
        }

        binding.navigationFab.setOnClickListener {
            finish()
        }
        binding.place1.setOnClickListener {
            getLocation(predictedPlaces[0])
        }
        binding.place2.setOnClickListener {
            getLocation(predictedPlaces[1])
        }
        binding.place3.setOnClickListener {
            getLocation(predictedPlaces[2])
        }
        binding.place4.setOnClickListener {
            getLocation(predictedPlaces[3])
        }
        binding.place5.setOnClickListener {
            getLocation(predictedPlaces[4])
        }

    }

    override fun onGpsBroadcastResponse() {
        if(!permissions.isGpsOpen())
            permissions.openGps(false)
    }

    fun getAddressFromLatlng(centerLatLang: LatLng) {
        var url = "https://maps.googleapis.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val apiRetrofit: ApiRetrofit = retrofit.create(ApiRetrofit::class.java)

        val decimalFormat = DecimalFormat("0.000000")
        val latitude = decimalFormat.format(centerLatLang.latitude)
        val longitude = decimalFormat.format(centerLatLang.longitude)

        val latLng: String = "${latitude},${longitude}"
        Log.e(" latLng : ", latLng)

        apiRetrofit.getAddress(latLng, resources.getString(R.string.google_map_api_key))
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {

                    if (response.isSuccessful) {
                        val jsonString = response.body().toString()

                        var address: String = ""

                        try {
                            val root: JSONObject = JSONObject(jsonString)
                            val status = root.getString("status")

                            if (status == "ZERO_RESULTS")
                                return

                            val plusCode = root.getJSONObject("plus_code")
                            val compoundCode = plusCode.getString("compound_code")

                            address = compoundCode
                            Log.e("address equal ", address)

                            if (address != null) {
                                showAddress(address)
                            } else
                                Log.e("Address is ", "null ")
                        } catch (exception: JSONException) {
                            address = "unknown"
                            showAddress(address)
                        }
                    } else {
                        Log.e("errorBody1 ", response.raw().toString())
                        Log.e("errorBody2 ", response.code().toString())
                        Log.e("errorBody3 ", response.headers().toString())
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    t.message?.let { Log.e("onFailure: ", it) }
                }
            })
    }

    fun getLatLngFromPlaceId(placeId: String, callback: (latLng: LatLng?) -> Unit) {

        //  https://maps.googleapis.com/maps/api/geocode/xml?place_id=ChIJeRpOeF67j4AR9ydy_PIzPuM&key=YOUR_API_KEY

        var url = "https://maps.googleapis.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val apiRetrofit: ApiRetrofit = retrofit.create(ApiRetrofit::class.java)

        apiRetrofit.getLatLng(placeId, resources.getString(R.string.google_map_api_key))
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {

                        val jsonString = response.body().toString()
                        Log.e("jsonString: ", jsonString)

                        val root = JSONObject(jsonString)
                        val status = root.getString("status")
                        if (status == "ZERO_RESULTS")
                            return

                        val result = root.getJSONArray("results").getJSONObject(0)

                        val location = result.getJSONObject("geometry").getJSONObject("location")
                        val latitude = location.getDouble("lat")
                        val longitude = location.getDouble("lng")
                        val latLng = LatLng(latitude, longitude)

                        callback.invoke(latLng)
                    } else {
                        Log.e("errorBody3 ", response.headers().toString())
                        callback.invoke(null)
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("onFailure ", t.message.toString())
                    callback.invoke(null)
                }
            })
    }

    private fun showAddress(address: String) {
        predictedPlacesSelectionMode = false
        var activeEdtText = getActiveEdtText()
        activeEdtText?.setAddress(address)

        var view = currentFocus
        if (view?.id == R.id.edt_where_to) {
            binding.edtWhereTo.setText(address)
            binding.edtWhereTo.setSelection(address.length)
        } else {
            binding.edtYourLocation.setText(address)
            binding.edtYourLocation.setSelection(address.length)

        }

    }

    fun Asad(name: String, callback: (latlng: LatLng) -> Unit) {

        var lart = LatLng(3.4, 23.3)
        callback.invoke(lart)
    }

    private fun getActiveEdtText(): PlaceInfo {
        var view = currentFocus
        return if (view?.id == R.id.edt_where_to)
            destination
        else
            currentLocation
    }

    fun putMarkerInCenter() {
        var centerLatLang = googleMap.projection.visibleRegion.latLngBounds.center
        marker?.position = centerLatLang

    }

    fun getLocation(p0: PlaceInfo) {
        predictedPlacesSelectionMode = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val view = currentFocus
        if (view?.id == R.id.edt_where_to)
            modifyDestinationLocation(p0)
        else
            modifyCurrentLocation(p0)
    }

    private fun modifyDestinationLocation(p0: PlaceInfo) {

        destination= PlaceInfo(p0.getPrimaryName(),p0.getAddress(),p0.getPlaceID())
        binding.edtWhereTo.setText(destination.getPrimaryName())
        binding.edtWhereTo.setSelection(destination.getPrimaryName()!!.length)
    }

    private fun modifyCurrentLocation(p0: PlaceInfo) {

        currentLocation= PlaceInfo(p0.getPrimaryName(),p0.getAddress(),p0.getPlaceID())
        binding.edtYourLocation.setText(currentLocation.getPrimaryName())
        binding.edtWhereTo.setSelection(destination.getPrimaryName()!!.length)

    }

    private fun showDialog() {

        val dialog=Dialog(this)
        dialog.setContentView(R.layout.unavailable_dialog)

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        val button=dialog.findViewById<MaterialButton>(R.id.ok_btn)
        button.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()


//        val builder = AlertDialog.Builder(this)
//
//        builder.setMessage("our service isn't available you are out of our range but we will do it soon")
//            .setTitle("Sorry")
//
//        builder.setIcon(R.drawable.ic_unavailable)
//        builder.setPositiveButton("ok", DialogInterface.OnClickListener() { dialog, which ->
//            dialog.dismiss()
//        })
//
//        builder.show()
    }

    fun requestPlaces() {
        Log.e("requestPlaces ", "")

        val request =
            FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().

                .setTypeFilter(TypeFilter.ADDRESS)

                //.setCountries("EG")
                .setSessionToken(token)
                .setCountries("EG")
                .setTypeFilter(TypeFilter.ADDRESS)
                .setTypeFilter(TypeFilter.CITIES)
                .setTypeFilter(TypeFilter.REGIONS)
                .setQuery(query)
                .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->

                binding.place1.visibility = View.GONE
                binding.place2.visibility = View.GONE
                binding.place3.visibility = View.GONE
                binding.place4.visibility = View.GONE
                binding.place5.visibility = View.GONE

                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                predictedPlaces.clear()
                for (prediction in response.autocompletePredictions) {
                    Log.e("prediction.toString() ", prediction.toString())
                    Log.e("PrimaryText ", prediction.getPrimaryText(null).toString())
                    val primaryName = prediction.getPrimaryText(null).toString()
                    val secondaryName = prediction.getSecondaryText(null).toString()
                    val placeId = prediction.placeId
                    val placeInfo = PlaceInfo(primaryName, secondaryName, placeId)


                    predictedPlaces.add(placeInfo)
                }
                showPredictedPlaces()
            }.addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    Log.e("Place not found:", " " + exception.statusCode)
                }
            }

    }

    fun showPredictedPlaces() {

        if (predictedPlaces.size == 0)
            return
        binding.plc1PrimaryTxt.text = predictedPlaces[0].getPrimaryName()
        binding.plc1SecondaryTxt.text = predictedPlaces[0].getAddress()
        binding.place1.visibility = View.VISIBLE

        if (predictedPlaces.size == 1)
            return

        binding.plc2PrimaryTxt.text = predictedPlaces[1].getPrimaryName()
        binding.plc2SecondaryTxt.text = predictedPlaces[1].getAddress()
        binding.place2.visibility = View.VISIBLE

        if (predictedPlaces.size == 2)
            return

        binding.plc3PrimaryTxt.text = predictedPlaces[2].getPrimaryName()
        binding.plc3SecondaryTxt.text = predictedPlaces[2].getAddress()
        binding.place3.visibility = View.VISIBLE

        if (predictedPlaces.size == 3)
            return

        binding.plc4PrimaryTxt.text = predictedPlaces[3].getPrimaryName()
        binding.plc4SecondaryTxt.text = predictedPlaces[3].getAddress()
        binding.place4.visibility = View.VISIBLE

        if (predictedPlaces.size == 4)
            return

        binding.plc5PrimaryTxt.text = predictedPlaces[4].getPrimaryName()
        binding.plc5SecondaryTxt.text = predictedPlaces[4].getAddress()
        binding.place5.visibility = View.VISIBLE


    }

    fun checkUndefinedPlace():Boolean{
        if(currentLocation.getPlaceID()==null&&currentLocation.getLtLng()==null){
            binding.edtYourLocation.requestFocus()
            return true
        }
        else if (destination.getPlaceID()==null&&destination.getLtLng()==null){
            binding.edtWhereTo.requestFocus()
            return true
        }
        else
            return false
    }
    private fun checkValidPlaces() {

        Log.e("TAG", "checkValidPlaces: " )
        if(checkUndefinedPlace())
            return
        else if (currentLocation.getLtLng() == null) {
            getLatLngFromPlaceId(currentLocation.getPlaceID()!!) { latLng: LatLng? ->
                if (latLng == null)
                    Toast.makeText(this, "check your internet connection or GPS", Toast.LENGTH_LONG)
                        .show()
                else {
                    currentLocation.setLtLng(latLng)
                    if (destination.getLtLng() == null)
                        getDestinationLatLng()
                }
            }
        } else {
            if (destination.getLtLng() == null)
                getDestinationLatLng()
            else
                isServiceAvailable()
        }
    }

    fun getDestinationLatLng() {
        Log.e("TAG", "getDestinationLatLng: " )
        getLatLngFromPlaceId(destination.getPlaceID()!!) { latLng: LatLng? ->
            if (latLng == null)
                Toast.makeText(this, "check your internet connection or GPS", Toast.LENGTH_LONG)
                    .show()
            else {
                destination.setLtLng(latLng)
                isServiceAvailable()
            }

        }
//        if(ridingPlace!!.latLng==null)
//            requestLatLng(ridingPlace!!)
//
//
//        if(destination!!.latLng==null)
//            requestLatLng(destination!!)
//
//        if(destination!!.latLng!=null&& ridingPlace!!.latLng!=null)
//            Toast.makeText(this,"Works Correctly",Toast.LENGTH_LONG).show()
    }

    fun isServiceAvailable() {

        Log.e("TAG", "isServiceAvailable " )
        val curLocLatLng=currentLocation.getLtLng()
        val curLocLat=curLocLatLng?.latitude
        val curLocLng=curLocLatLng?.longitude
        val whereToLatLng=destination.getLtLng()
        val whereToLat=whereToLatLng?.latitude
        val whereToLng=whereToLatLng?.longitude

        Log.e("TAG", "curLocLat $curLocLat" )
        Log.e("TAG", "curLocLng $curLocLng " )
        Log.e("TAG", "whereToLat $whereToLat " )
        Log.e("TAG", "whereToLng $whereToLng " )
        if (curLocLat!!>=minimumLat&& curLocLat!!<= maximumLat && curLocLng!!>=minimumLng&& curLocLng!!<= maximumLng ) {
            if (whereToLat!! >= minimumLat && whereToLat!! <= maximumLat &&
                whereToLng!! >= minimumLng && whereToLng!! <= maximumLng
            )
                onServiceAvailable()
            else
                onServiceUnavailable()
        }
        else
            onServiceUnavailable()
    }

    fun onServiceAvailable(){
        Log.e("TAG", "onServiceAvailable: " )
        val intent= Intent(this,DriverRequest::class.java)
        intent.putExtra("currentLocation",currentLocation as Serializable)
        intent.putExtra("destination",destination  as Serializable)
        startActivity(intent)
    }
    fun onServiceUnavailable(){
        Log.e("TAG", "onServiceUnavailable " )
        showDialog()
    }
    fun requestLatLng(placeInfo: PlaceInfo) {
        var placeId = placeInfo.getPlaceID()
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(placeId!!, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                val latLng = place.latLng
                if (latLng != null) {
                    placeInfo.setLtLng(latLng)

                }

            }.addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    Log.e("Place not found", ": ${exception.message}")
                    val statusCode = exception.statusCode
                    TODO("Handle error with given status code")
                }
            }
    }


    private fun getLocation(callback: (latlng: LatLng) -> Unit) {
        Log.e("getLocation() ", " do it ")

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
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
                    Toast.makeText(this, "open your fucking Gps now", Toast.LENGTH_SHORT).show()
                else {
                    val lat = location.latitude
                    val lng = location.longitude

                    val latLng = LatLng(lat, lng)
                    currentLocation= PlaceInfo(latLng)
                    callback.invoke(latLng)
                }

            }

    }

    private fun showUserLocationOnMap(latLng: LatLng, durationMs: Int) {

        Log.e("showUserLocationOnMap ", " do it ")
        //mMap.addMarker(MarkerOptions().position(latLng).title("Marker in zawiaa"))
        var ffll = CameraUpdateFactory.newLatLngZoom(latLng, 15.0f)

        googleMap.animateCamera(ffll, durationMs, object : GoogleMap.CancelableCallback {
            override fun onCancel() {}
            override fun onFinish() {}
        })

    }

    private val yourLocationTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            if (!predictedPlacesSelectionMode) {
                predictedPlacesSelectionMode = true
                return
            }

            if (p0.toString().isEmpty()) {
                currentLocation.setPlaceID(null)
                currentLocation.setLtLng(null)
            }
            query = p0.toString()
            requestPlaces()
        }
    }
    private val whereToTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            if (!predictedPlacesSelectionMode || p0.toString() == "Loading ...") {
                predictedPlacesSelectionMode = true
                return
            }

            if (p0.toString().isEmpty()) {
                destination.setPlaceID(null)
                destination.setLtLng(null)
            }
            query = p0.toString()
            requestPlaces()
        }
    }

//    override fun onStart() {
//        super.onStart()
//        val view = currentFocus
//        val inputMethodManager =
//            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
//    }

    private fun registerReceiver(){
        registerReceiver(broadcastReceiver,intentFilter)
    }
    private fun unRegisterReceiver(){
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
