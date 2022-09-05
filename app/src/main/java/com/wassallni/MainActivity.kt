package com.wassallni

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.auth.FirebaseAuth
import com.wassallni.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val auth = FirebaseAuth.getInstance()

        checkAuthentication()
        binding.btSignOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            loginLauncher.launch(intent)
        }



    }

    private fun checkAuthentication() {
        val user = auth.currentUser
        when {
            user == null ->
                login()
            user.phoneNumber == null -> {
                auth.signOut()
                login()
            }
            else -> {
                binding.btSignOut.visibility = View.VISIBLE
//                val intent = Intent(this, CustomerMainActivity::class.java)
//                startActivity(intent)
            }
        }

    }

    private fun login() {
        val intent: Intent = Intent(this, LoginActivity::class.java)
        loginLauncher.launch(intent)
    }

    private val loginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            val data = result.data

            if (result.resultCode == RESULT_CANCELED)
                finish()
             else {
//                val intent = Intent(this, CustomerMainActivity::class.java)
//                startActivity(intent)
                 binding.btSignOut.visibility=View.VISIBLE
            }
        }

}