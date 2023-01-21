package com.wassallni.data.repository

import com.wassallni.data.datasource.LoginDataStore


class LoginRepository (private val loginDataStore: LoginDataStore){

    val verificationCompleted = loginDataStore.verificationCompleted
    val verificationFailed = loginDataStore.verificationFailed
    val codeSent=loginDataStore.codeSent

    fun signInWithFirebase(smsCode:String){
        loginDataStore.signInWithFirebase(smsCode)
    }

    fun verifyPhoneNumber(phoneNumber: String){
        loginDataStore.verifyPhoneNumber(phoneNumber)
    }




}
