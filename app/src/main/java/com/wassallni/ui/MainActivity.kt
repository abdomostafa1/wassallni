package com.wassallni.ui
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.wassallni.data.model.LoggedInUser
import com.wassallni.databinding.ActivityMainBinding
import com.wassallni.ui.viewmodel.RouteViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    private val TAG = "MainActivity"
    private lateinit var binding:ActivityMainBinding
    @Inject lateinit var loggedInUser: LoggedInUser

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token =loggedInUser.getToken()
        val name =loggedInUser.getName()
        Log.e(TAG, "Token:$token" )
        Log.e(TAG, "Token:$name" )
        Toast.makeText(this,"Name:$name",Toast.LENGTH_SHORT).show()
        Toast.makeText(this,"Token:$token",Toast.LENGTH_LONG).show()
    }

}

