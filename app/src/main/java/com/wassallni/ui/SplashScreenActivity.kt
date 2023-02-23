package com.wassallni.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.wassallni.data.model.LoggedInUser
import com.wassallni.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private val TAG = "SplashScreenActivity"
    val auth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivitySplashScreenBinding

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
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (!isLoggedIn) {
            openLoginActivity()
        } else {
            val name=sharedPreferences.getString("name","")
            val token=sharedPreferences.getString("token","")
            val loggedInUser=LoggedInUser.getInstance(displayName =name!!,token=token!!)
            openMainActivity()
        }
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
        { _ ->
            finish()
        }

    override fun onDestroy() {
        super.onDestroy()
        if (auth.currentUser?.phoneNumber == null)
            auth.signOut()

    }
}
