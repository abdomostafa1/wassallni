package com.wassallni.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.wassallni.databinding.ActivitySplashScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAuthentication()
        binding.btSignOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            loginLauncher.launch(intent)
        }
    }

    private fun checkAuthentication() {
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (!isLoggedIn)
            openLoginActivity()
        else
            openMainActivity()

    }


    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        loginLauncher.launch(intent)
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        mainLauncher.launch(intent)
    }

    private val loginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->

            if (result.resultCode == RESULT_CANCELED)
                finish()
            else {
                val intent = Intent(this, MainActivity::class.java)
                mainLauncher.launch(intent)
            }
        }

    private val mainLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->

            if (result.resultCode == RESULT_CANCELED)
                finish()
            else {
               checkAuthentication()
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        if (auth.currentUser?.phoneNumber == null)
            auth.signOut()

    }
}
