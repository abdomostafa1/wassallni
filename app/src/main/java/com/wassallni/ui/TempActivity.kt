package com.wassallni.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.*
import com.wassallni.R
import com.wassallni.data.model.DriverInfo
import com.wassallni.data.model.uiState.RateDriverUiState
import com.wassallni.databinding.ActivityTempBinding
import com.wassallni.ui.viewmodel.RateDriverVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.*


// This Activity is only for Testing

private const val TAG = "TempActivity"

@AndroidEntryPoint
class TempActivity : AppCompatActivity() {

    lateinit var binding: ActivityTempBinding
    private val rateDriverVM: RateDriverVM by viewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTempBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rateDriverVM.getDriverInfo()
        binding.ratingBar.onRatingBarChangeListener =
            OnRatingBarChangeListener { _, rating, _ ->
                changeRatingText(rating)
            }
        binding.rateBtn.setOnClickListener {
            val message=binding.comment.text.toString()
            val stars=binding.ratingBar.rating
            Log.e(TAG, "stars=$stars " )
            rateDriverVM.rateDriver(stars,message,"args.tripId","args.driverId")
        }
        binding.backButton.setOnClickListener {
            Toast.makeText(this@TempActivity,"backButton",Toast.LENGTH_LONG).show()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                rateDriverVM.driverInfo.collect {
                    if(it!=null){
                        updateUi(it)
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                rateDriverVM.rateUiState.collect { state ->
                    when (state) {
                        is RateDriverUiState.Loading -> {
                            binding.progressBar.visibility=View.VISIBLE
                        }
                        is RateDriverUiState.Success -> {
                            binding.progressBar.visibility=View.GONE
                            Log.e(TAG, "RateDriverUiState.Success " )
                        }
                        is RateDriverUiState.Error -> {
                            binding.progressBar.visibility=View.GONE
                            Toast.makeText(this@TempActivity,state.errorMsg,Toast.LENGTH_LONG).show()
                            Log.e(TAG, "error:${state.errorMsg} ")
                        }

                        else -> {}
                    }
                }
            }
        }

    }

    private fun updateUi(it: DriverInfo) {
        Glide.with(this).load(it.imgUrl).circleCrop().into(binding.driverImg)
        binding.yourOpinion.append(" ${it.driverName}")
    }

    private fun changeRatingText(rating: Float) {
        if (rating > 4)
            binding.rateTv.text = getString(R.string.rating_excellent)
        else if (rating > 3)
            binding.rateTv.text = getString(R.string.rating_good)
        else if (rating > 2)
            binding.rateTv.text = getString(R.string.rating_average)
        else if (rating > 1)
            binding.rateTv.text = getString(R.string.rating_poor)
        else
            binding.rateTv.text = getString(R.string.rating_bad)
    }

}
