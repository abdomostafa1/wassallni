package com.wassallni.data.repository

import android.app.Activity
import android.util.Log
import com.wassallni.data.datasource.LoginDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


private const val TAG = "LoginRepository"

class LoginRepository @Inject constructor(private val loginDataStore: LoginDataSource) {

    val loginUiState = loginDataStore.loginUiState

    suspend fun verifyWithFirebase(smsCode: String, activity: Activity) {
        loginDataStore.verifyWithFirebase(smsCode, activity)
    }

    suspend fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        loginDataStore.sendVerificationCode(phoneNumber, activity)
    }

    suspend fun makeLoginRequest(name: String, phoneNumber: String) {
        withContext(Dispatchers.IO) {
            Log.e(TAG, "name: $name")
            Log.e(TAG, "phone: $phoneNumber")
            val params = HashMap<String, Any>()
            params["name"] = name
            params["phone"] = phoneNumber
            loginDataStore.makeLoginRequest(params = params)
        }
    }

    fun resetUiState() {

        loginDataStore.resetUiState()
    }

}
