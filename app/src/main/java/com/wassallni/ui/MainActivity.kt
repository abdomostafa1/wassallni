package com.wassallni.ui

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.wassallni.R
import com.wassallni.data.model.LoggedInUser
import com.wassallni.data.model.Trip
import com.wassallni.databinding.ActivityMainBinding
import com.wassallni.ui.fragment.main_graph.MainFragmentDirections
import com.wassallni.ui.fragment.main_graph.TripFragment
import com.wassallni.ui.viewmodel.RouteViewModel
import com.wassallni.utils.Permissions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    @Inject
    lateinit var loggedInUser: LoggedInUser

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var permission: Permissions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment=supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment
        navController=navHostFragment.navController
        val intent=intent
        if (intent.getBooleanExtra("rateDriverIntent",false)){
            Log.e(TAG, "onCreate: yeas new intent", )
            openRateDriverScreen(intent)
        }
        else
            Log.e(TAG, "onCreate: no Old intent", )

        val fcmToken = preferences.getString("fcmToken", "")
        if (fcmToken == "")
            cacheFcmToken()

        if (fcmToken != null) {
            Log.e("fcmToken:", fcmToken)
        }

        //FirebaseMessaging.getInstance().send()

    }

    private fun cacheFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val fcmToken = task.result
                Toast.makeText(this, "fcmToken:$fcmToken", Toast.LENGTH_SHORT).show()
                Log.e("fcmToken:", fcmToken)
                val editor = preferences.edit()
                editor.putString("fcmToken", fcmToken)
                editor.apply()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val rateDriverIntent=intent?.getBooleanExtra("rateDriverIntent",false)
        Log.e(TAG, "onNewIntent: $rateDriverIntent", )
        openRateDriverScreen(intent!!)
    }

    private fun openRateDriverScreen(intent: Intent){
        val tripId=intent.getStringExtra("tripId")
        val driverId=intent.getStringExtra("driverId")

        Log.e(TAG, "onNewIntent: $tripId", )
        Log.e(TAG, "onNewIntent: $driverId", )

        val action=MainFragmentDirections.actionMainFragmentToRateDriverFragment(tripId!!,driverId!!)
        navController.navigate(action)
    }
}

