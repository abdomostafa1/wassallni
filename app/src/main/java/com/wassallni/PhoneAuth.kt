package com.wassallni

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.wassallni.login_fragments.LoginFragment
import java.util.concurrent.TimeUnit

  open  class PhoneAuth {


    var subscribers:ArrayList<VerificationObserver> = ArrayList()
      private var RESEND_VERIFICATION_CODE_STATE:Boolean=false

    private val auth=FirebaseAuth.getInstance()
    val activity= LoginActivity.context as AppCompatActivity


      public fun sendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(45L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private val callbacks= object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {

        }

        override fun onVerificationFailed(p0: FirebaseException) {
            if (p0 is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.e(TAG, "error message "+p0.message )

            }
            else if (p0 is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(activity,"Service will be available later", Toast.LENGTH_LONG).show()
            }
            for (i in subscribers)
                i.verificationFailed()

        }

        override fun onCodeSent(verificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId, p1)
            LoginFragment.token=p1
            for (i in subscribers){
                i.codeSent(verificationId,p1)
            }
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
            .setActivity(activity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    public fun signIn(credential:PhoneAuthCredential){

        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
               for (i in subscribers)
                i.loginCompleted()
            }
            else {
                // Sign in failed, display a message and update the UI
                    // The verification code entered was invalid
                    for(i in subscribers)
                        i.verificationFailed()
                Log.e(TAG, "erroreeee: ${task.exception}" )
                Toast.makeText(activity,"${task.exception}",Toast.LENGTH_LONG).show()
            }
        }
    }
      public fun addSubscriber(subscriber:VerificationObserver){

          subscribers.add(subscriber)
      }

  }