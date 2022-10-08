package com.wassallni

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.wassallni.databinding.ActivityDriverRequestBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


class DriverRequest : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDriverRequestBinding
    lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap
    lateinit var currentLocation:PlaceInfo
    lateinit var destination:PlaceInfo

    val maximumLng: Double = 31.212882
    val minimumLng: Double = 31.188709
    val minimumLat: Double = 29.308123
    val maximumLat: Double = 29.350525
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDriverRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mapFragment = supportFragmentManager
            .findFragmentById(R.id.selection_map) as SupportMapFragment

        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(p0: GoogleMap) {

        googleMap=p0
        currentLocation= intent.getSerializableExtra("currentLocation") as PlaceInfo
        destination= intent.getSerializableExtra("destination") as PlaceInfo
        googleMap.addMarker(MarkerOptions().position(currentLocation.getLtLng()!!))
        googleMap.addMarker(MarkerOptions().position(destination.getLtLng()!!).title("destination"))
        googleMap.uiSettings.isZoomControlsEnabled=true
        drawPolyLine()
    }

    private fun drawPolyLine() {
        var url = "https://maps.googleapis.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val apiRetrofit: ApiRetrofit = retrofit.create(ApiRetrofit::class.java)

        val originLatLng= "${currentLocation.getLtLng()?.latitude},${currentLocation.getLtLng()?.longitude}"
        val destinationLatLng= "${destination.getLtLng()?.latitude},${destination.getLtLng()?.longitude}"
        apiRetrofit.getPolyLine(originLatLng,destinationLatLng,resources.getString(R.string.google_map_api_key))
            .enqueue(object :Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val string=response.body().toString()
                    Log.e("string Response:",string )
                    val root=JSONObject(string)
                    val status=root.getString("status")
                    if(status=="ZERO_RESULTS")
                        return

                    val route= root.getJSONArray("routes").getJSONObject(0)
                    //val leg=route.getJSONArray("legs").getJSONObject(0)
                    val overviewPolyline=route.getJSONObject("overview_polyline")
                    val encodesPath=overviewPolyline.getString("points")
                    var points=PolyUtil.decode(encodesPath)

                    var options=PolylineOptions()
                    options.addAll(points)
                    options.width(4.0f)
                    googleMap.addPolyline(options)
                    var builder = LatLngBounds.Builder()
                    builder.include(LatLng(maximumLat,maximumLng))
                    builder.include(LatLng(maximumLat,minimumLng))
                    builder.include(LatLng(minimumLat,maximumLng))
                    builder.include(LatLng(minimumLat,minimumLng))

                    val cameraUpdateFactory=CameraUpdateFactory.newLatLngBounds(
                        builder.build(),
                        50
                    )
                    googleMap.animateCamera(cameraUpdateFactory)

                }

                override fun onFailure(call: Call<String>, t: Throwable) {}
            })
    }

}
