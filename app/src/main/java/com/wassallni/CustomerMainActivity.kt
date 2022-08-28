package com.wassallni

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.wassallni.databinding.ActivityCustomerMainBinding

class CustomerMainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCustomerMainBinding
    val permissions=Permissions(this)
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCustomerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding
        binding.cvTuktuk.setOnClickListener {

            if(permissions.checkLocationPermission()) {
                if (permissions.isGpsOpen()) {
                    val intent = Intent(this, DriverMainActivity::class.java)
                    startActivity(intent)
                }
                else
                    permissions.openGps()
            }
            else
                permissions.requestLocationPermission()


        }


    }
}