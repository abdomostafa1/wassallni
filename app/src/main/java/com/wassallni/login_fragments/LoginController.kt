package com.wassallni.login_fragments

import com.google.firebase.auth.PhoneAuthProvider
import com.wassallni.PhoneAuth
import com.wassallni.VerificationObserver

class LoginController : VerificationObserver{

    companion object {
        private lateinit var controller: LoginController

        public fun getInstance(): LoginController {
            if (controller == null)
                controller = LoginController()

            return controller
        }

    }

    private var phoneNumber:String="01112345678"
    lateinit var phoneAuth: PhoneAuth

    private constructor(){
        phoneAuth=PhoneAuth()
        phoneAuth.addSubscriber(this)
    }

    public fun isNumberValid(phoneNumber:String):Boolean{
        return phoneNumber.trim().isNotEmpty()
    }

    override fun codeSent(code: String, token: PhoneAuthProvider.ForceResendingToken) {
        TODO("Not yet implemented")
    }

    override fun loginCompleted() {
        TODO("Not yet implemented")
    }

    override fun verificationFailed() {
        TODO("Not yet implemented")
    }
}