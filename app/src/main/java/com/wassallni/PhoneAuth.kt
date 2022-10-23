package com.wassallni

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.wassallni.fragments.LoginPresenter
import java.util.concurrent.TimeUnit

  open  class PhoneAuth (private var presenter: LoginPresenter,private var context: Context){


    private var RESEND_VERIFICATION_CODE_STATE:Boolean=false
    private val auth=FirebaseAuth.getInstance()


      public fun sendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(45L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity((context as AppCompatActivity))                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private val callbacks= object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            presenter.onVerificationCompleted(p0)
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            if (p0 is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.e(TAG, "error message "+p0.message )
                presenter.onVerificationFailed(p0.message.toString())

            }
            else if (p0 is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                presenter.onVerificationFailed(p0.message.toString())
            }

        }

        override fun onCodeSent(verificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId, p1)
            presenter.onCodeSent(verificationId,p1)
        }
    }

    public fun resendCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        if(token==null)
        Log.e(TAG, "resendCode: token == abddooooo" )
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(45L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(context as AppCompatActivity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    public fun signIn(credential:PhoneAuthCredential){

        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
               presenter.onLoginCompleted()
            }
            else {
                // Sign in failed, display a message and update the UI
                    // The verification code entered was invalid
                presenter.onSignInWIthPhoneFailed(task.exception.toString())
            }
        }
    }


  }