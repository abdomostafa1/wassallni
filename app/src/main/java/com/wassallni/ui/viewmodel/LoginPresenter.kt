package com.wassallni.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.wassallni.firebase.authentication.FacebookAuth
import com.wassallni.firebase.authentication.GoogleAuth
import com.wassallni.firebase.authentication.PhoneAuth

class LoginPresenter  {

    private lateinit var mainView: LoginObserver
    lateinit var facebookAuth: FacebookAuth
    private var phoneNumber = "01112345678"
    private  var token: PhoneAuthProvider.ForceResendingToken? =null
    lateinit var verificationCode: String
    private lateinit var phoneAuth: PhoneAuth
    val auth = FirebaseAuth.getInstance()
    lateinit var google: GoogleAuth

    companion object {
        private var presenter: LoginPresenter? =null

        public fun getInstance(): LoginPresenter? {
            if (presenter == null) {
                presenter = LoginPresenter()
            }
            return presenter
        }
    }

    private constructor(){}
       fun initializePresenter(context:Context) {

        phoneAuth= PhoneAuth(context as Activity)
        facebookAuth = FacebookAuth(this,context)
        google= GoogleAuth(context)
        google.notifyPresenter(this)
     }

    fun setActiveFragment(fragment: LoginObserver){
        mainView=fragment
    }

    fun isNumberInValid(phoneNumber: String): Boolean {
        return phoneNumber.trim().isEmpty()
    }

    fun sendVerificationCode(phoneNumber: String) {
        this.phoneNumber = phoneNumber
     //   phoneAuth.sendVerificationCode(phoneNumber, token)
    }

    fun signInWithFacebook(fragment:Fragment) {
        facebookAuth.signIn(fragment)
    }

     fun onSignInWIthGoogleSuccessed() {
         Log.e("onSignInWIthGoogle ", "Success : " )
        mainView.onSignInWIthGoogleSuccessed()
    }

     fun onSignInWIthFacebookSuccessed() {
        mainView.onSignInWIthFacebookSuccessed()

    }

    fun onSignInWIthGoogleFailed(message:String) {
        Log.e("onSignInWIthGoogle ", "Failed : " )
        mainView.onSignInWIthGoogleFailed(message)
    }

    fun onSignInWIthFacebookFailed(message:String) {
        mainView.onSignInWIthFacebookFailed(message)

    }
    fun onSignInWIthPhoneFailed(message:String){
        mainView.onSignInWIthPhoneFailed(message)
    }

    fun onCodeSent(code: String, token: PhoneAuthProvider.ForceResendingToken) {

        this.token = token
        verificationCode = code

        mainView.onCodeSent()

    }

    fun resendCode() {
      //  phoneAuth.resendCode(phoneNumber, token)
    }

    fun verifyNumber(userCode: String) {
        val user = auth.currentUser
        val credential = PhoneAuthProvider.getCredential(verificationCode, userCode)

//        if (user == null)
//            signInWithPhoneNumber(credential)
//        else
//            addPhoneNumber(credential)

    }

//    private fun signInWithPhoneNumber(credential: PhoneAuthCredential) {
//
//        phoneAuth.signIn(credential)
//    }

    private fun addPhoneNumber(credential: PhoneAuthCredential) {
        val user = auth.currentUser
        user?.linkWithCredential(credential)?.addOnCompleteListener { task ->

            if (task.isSuccessful) {
               mainView.onLoginCompleted()
            } else
                mainView.onLinkPhoneNumberFailed(task.exception.toString())
        }
    }

    fun onLoginCompleted() {

        mainView.onLoginCompleted()
    }


     fun onVerificationFailed(message: String) {
         mainView.onVerificationFailed(message)
    }

    fun onVerificationCompleted(credential: PhoneAuthCredential) {

//        if (auth.currentUser == null)
//            signInWithPhoneNumber(credential)
//        else
//            addPhoneNumber(credential)
    }

    fun signOutFromGoogle(){
        google.signOut()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        facebookAuth.onActivityResult(requestCode,resultCode,data)
    }
}
