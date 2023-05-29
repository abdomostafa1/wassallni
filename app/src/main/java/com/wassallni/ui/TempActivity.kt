package com.wassallni.ui

// import the following
import android.R.attr.data
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.model.*
import com.paymob.acceptsdk.IntentConstants
import com.paymob.acceptsdk.PayActivity
import com.paymob.acceptsdk.PayActivityIntentKeys
import com.paymob.acceptsdk.PayResponseKeys
import com.paymob.acceptsdk.SaveCardResponseKeys
import com.paymob.acceptsdk.ThreeDSecureWebViewActivty
import com.paymob.acceptsdk.ToastMaker
import com.wassallni.BuildConfig
import com.wassallni.R
import com.wassallni.databinding.ActivityTempBinding
import com.wassallni.ui.viewmodel.BookVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.math.*


// This Activity is only for Testing

private const val TAG = "TempActivity"

@AndroidEntryPoint
class TempActivity : AppCompatActivity() {

   // private val bookVM: BookVM by viewModels()
    lateinit var binding: ActivityTempBinding
    private val ACCEPT_PAYMENT_REQUEST = 10

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTempBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}
