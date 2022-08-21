package com.wassallni

import com.google.firebase.auth.PhoneAuthProvider

interface VerificationObserver {

    abstract fun codeSent(code:String,token: PhoneAuthProvider.ForceResendingToken)
    abstract fun loginCompleted()
    abstract fun verificationFailed()

}