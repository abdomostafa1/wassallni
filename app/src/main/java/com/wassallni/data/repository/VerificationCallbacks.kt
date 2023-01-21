package com.wassallni.data.repository

interface VerificationCallbacks {

     fun onVerificationCompleted()

     fun onVerificationFailed(error: String)

     fun onCodeSent()

}