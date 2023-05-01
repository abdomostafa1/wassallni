package com.wassallni.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
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
import com.wassallni.data.model.uiState.SupportUiState
import com.wassallni.databinding.ActivityTempBinding
import com.wassallni.databinding.ChatMessageBinding
import com.wassallni.ui.viewmodel.BookedTripVM
import com.wassallni.ui.viewmodel.SupportVM
import com.wassallni.utils.DateUseCase
import com.wassallni.utils.Permissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.*


private const val TAG = "TempActivity"

@AndroidEntryPoint
class TempActivity : AppCompatActivity() {

    lateinit var binding: ActivityTempBinding
    private lateinit var mapFragment: SupportMapFragment

    @Inject
    lateinit var permission: Permissions

    @Inject
    lateinit var loggedInUser: LoggedInUser
    private val viewModel: SupportVM by viewModels()
    var latestView:ChatMessageBinding?=null
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

        binding.sendMessage.setOnClickListener {
            sendMessage()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.supportUiState.collect { state ->
                    when (state) {
                        is SupportUiState.Loading -> {
                            showLoadingState()
                        }
                        is SupportUiState.Success -> {
                            showSuccessState()
                        }
                        is SupportUiState.Error -> {
                            showErrorState()
                            Toast.makeText(this@TempActivity, state.errorMsg, Toast.LENGTH_LONG)
                                .show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun sendMessage() {
        val message=binding.chatEditText.text.toString()
        if (message=="")
            return

        viewModel.sendFeedback(message)

        val timeStamp=System.currentTimeMillis()/1000
        val currentTime=DateUseCase.fromMillisToString1(timeStamp)
        val chatView=ChatMessageBinding.inflate(layoutInflater)
        chatView.message.text=message
        chatView.time.text=currentTime
        binding.linearLayout.addView(chatView.root)
        latestView=chatView
        binding.chatEditText.setText("")
        scrollToLatestView()
    }

    private fun scrollToLatestView() {
        lifecycleScope.launch (Dispatchers.IO) {
            delay(500)
            val latestView = binding.linearLayout.getChildAt(binding.linearLayout.childCount - 1)
            binding.scrollView2.scrollTo(0, latestView.bottom)

        }
    }

    private fun showLoadingState(){
        binding.sendMessage.visibility=View.GONE
        binding.loader.visibility=View.VISIBLE

    }

    private fun showSuccessState(){
        binding.loader.visibility=View.GONE
        binding.sendMessage.visibility=View.VISIBLE
        latestView?.seenIcon?.visibility=View.VISIBLE

    }
    private fun showErrorState(){
        binding.loader.visibility=View.GONE
        binding.sendMessage.visibility=View.VISIBLE
        binding.linearLayout.removeViewAt(binding.linearLayout.childCount-1)
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
