package com.wassallni.data.repository

import com.wassallni.data.datasource.LoginDataSource
import javax.inject.Inject


class LoginRepository @Inject constructor (private val loginDataStore: LoginDataSource){

    val loginUiState=loginDataStore.loginUiState

    suspend fun verifyWithFirebase(smsCode:String){
        loginDataStore.verifyWithFirebase(smsCode)
    }

    suspend fun sendVerificationCode(phoneNumber: String){
        loginDataStore.sendVerificationCode(phoneNumber)
    }

     suspend fun makeLoginRequest(name: String, phoneNumber: String) {
        val params=HashMap<String,Any>()
        params["name"]=name
        params["phone"]=phoneNumber
        loginDataStore.makeLoginRequest(params=params)
    }

    fun resetUiState() {

        loginDataStore.resetUiState()
    }


}
