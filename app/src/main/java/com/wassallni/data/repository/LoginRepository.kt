package com.wassallni.data.repository

import com.wassallni.data.datasource.LoginDataSource


class LoginRepository (private val loginDataStore: LoginDataSource){

    val verificationCompleted = loginDataStore.verificationCompleted
    val loginCompleted = loginDataStore.loginCompleted
    val onFailure = loginDataStore.onFailure
    val codeSent=loginDataStore.codeSent

    fun verifyWithFirebase(smsCode:String){
        loginDataStore.verifyWithFirebase(smsCode)
    }

    fun sendVerificationCode(phoneNumber: String){
        loginDataStore.sendVerificationCode(phoneNumber)
    }

     fun makeLoginRequest(name: String, phoneNumber: String) {
        val params=HashMap<String,Any>()
        params["name"]=name
        params["phone"]=phoneNumber
        loginDataStore.makeLoginRequest(params=params)
    }


}
