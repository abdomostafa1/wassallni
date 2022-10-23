package com.wassallni

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.wassallni.databinding.ActivityMainBinding
import com.wassallni.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
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
                //binding.btSignOut.visibility = View.VISIBLE
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
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
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
//                 binding.btSignOut.visibility=View.VISIBLE
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        if(auth.currentUser?.phoneNumber==null)
            auth.signOut()
    }
}