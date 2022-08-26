package com.wassallni

import com.google.firebase.auth.PhoneAuthProvider

interface VerificationObserver {

    abstract fun onCodeSent(code:String,token: PhoneAuthProvider.ForceResendingToken)
    abstract fun onLoginCompleted()
    abstract fun onVerificationFailed()

}