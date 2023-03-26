package com.wassallni.ui
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.wassallni.R
import com.wassallni.data.model.LoggedInUser
import com.wassallni.data.model.Trip
import com.wassallni.databinding.ActivityMainBinding
import com.wassallni.ui.fragment.main_graph.TripFragment
import com.wassallni.ui.viewmodel.RouteViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    private val TAG = "MainActivity"
    private lateinit var binding:ActivityMainBinding
    lateinit var navController: NavController
    @Inject lateinit var loggedInUser: LoggedInUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController


        //val config= AppBarConfiguration(navController.graph)
        //binding.topAppBar.setupWithNavController(navController,config)

    }

    val recyclerOnClick={trip:Trip ->
        //val data=TripFragmentDirections

    }

}

