package com.wassallni.firebase.authentication

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.wassallni.R
import com.wassallni.data.repository.VerificationCallbacks
import dagger.hilt.android.qualifiers.ActivityContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

open class PhoneAuth @Inject constructor (@ActivityContext private val context: Context) {

    private val TAG = "PhoneAuth"
    private var notifier: VerificationCallbacks? = null
    private var storedVerificationCode: String? = null
    private var token: PhoneAuthProvider.ForceResendingToken? = null
    private val auth = FirebaseAuth.getInstance()

    fun setNotifier(verificationCallbacks: VerificationCallbacks) {
        notifier = verificationCallbacks
    }

    fun sendVerificationCode(
        phoneNumber: String
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(context as Activity)
            // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token!!) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            notifier?.onVerificationCompleted()
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            var error = ""

            error = if (p0 is FirebaseTooManyRequestsException)
                context.getString(R.string.sign_in_not_available_today)
            else
                p0.message ?: context.getString(R.string.error_occurred)

            notifier?.onVerificationFailed(error)
        }

        override fun onCodeSent(
            verificationId: String,
            p1: PhoneAuthProvider.ForceResendingToken
        ) {
            storedVerificationCode = verificationId
            token = p1
            notifier?.onCodeSent()
        }
    }


    fun getCredential(smsCode: String): PhoneAuthCredential {
        return PhoneAuthProvider.getCredential(storedVerificationCode!!, smsCode)

    }

    fun verifyWithFirebase(credential: PhoneAuthCredential) {

         auth.signInWithCredential(credential).addOnCompleteListener(context as Activity) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.e(TAG, "signInWithCredential:success")
                notifier?.onVerificationCompleted()
            } else {
                Log.e(TAG, "signInWithCredential:failure", task.exception)

                var error = ""
                error = if (task.exception is FirebaseAuthInvalidCredentialsException)
                    context.getString(R.string.verification_code_invalid)
                else
                    task.exception?.message ?: context.getString(R.string.error_occurred)

                notifier?.onVerificationFailed(error)
            }
        }
    }

}