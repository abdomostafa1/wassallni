package com.wassallni.login_fragments

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.wassallni.*

class LoginController : VerificationObserver, SignInCompletionObserver {

    lateinit var facebookAuth: FacebookAuth
    public var phoneNumber = "01112345678"
        get() = phoneNumber
    private  var token: PhoneAuthProvider.ForceResendingToken? =null
    lateinit var verificationCode: String
    var phoneAuth: PhoneAuth = PhoneAuth()
    val auth = FirebaseAuth.getInstance()
    val google=GoogleAuth.getInstance()
    lateinit var activity: AppCompatActivity

    companion object {
        private var controller: LoginController? =null

        public fun getInstance(context: Context): LoginController? {
            if (controller == null)
                controller = LoginController(context)

            return controller
        }

        var subscribers = ArrayList<Fragment>()
    }

     private constructor(context: Context) {
        phoneAuth.addSubscriber(this)
        facebookAuth = FacebookAuth(context)
        facebookAuth.addSubscriber(this)
        google?.addSubscriber(this)
        activity = context as AppCompatActivity

     }

    public fun isNumberValid(phoneNumber: String): Boolean {
        return phoneNumber.trim().isEmpty()
    }


    fun sendVerificationCode(phoneNumber: String) {
        this.phoneNumber = phoneNumber
        phoneAuth.sendVerificationCode(phoneNumber, token)

    }

    fun signInWithFacebook(fragment:Fragment) {
        facebookAuth.signIn(fragment)

    }

    fun signInWithGoogle() {
        google?.signIn()
    }

    override fun onSignInWIthGoogleSuccess() {
        val sub = subscribers[subscribers.size-1]
            (sub as LoginFragment).onSignInWIthGoogleSuccess()
    }

    override fun onSignInWIthFacebookSuccess() {
        val sub = subscribers[subscribers.size-1]
            (sub as LoginFragment).onSignInWIthFacebookSuccess()

    }

    override fun onCodeSent(code: String, token: PhoneAuthProvider.ForceResendingToken) {

        this.token = token
        verificationCode = code

        val sub = subscribers[subscribers.size-1]
                when {
                    sub.javaClass.toString().contains("LoginFragment") -> {
                        (sub as LoginFragment).onCodeSent()
                    }
                    sub.javaClass.toString().contains("PhoneFragment") -> {
                        (sub as PhoneFragment).onCodeSent()
                    }
                    else -> {
                        (sub as VerificationFragment).onCodeSent()
                    }
                }
    }

    fun resendCode() {
        phoneAuth.resendCode(phoneNumber, token)
    }

    fun verifyNumber(userCode: String) {
        val user = auth.currentUser
        val credential = PhoneAuthProvider.getCredential(verificationCode, userCode)

        if (user == null)
            signInWithPhoneNumber(credential)
        else
            addPhoneNumber(credential)

    }

    private fun signInWithPhoneNumber(credential: PhoneAuthCredential) {

        phoneAuth.signIn(credential)
    }

    private fun addPhoneNumber(credential: PhoneAuthCredential) {
        val user = auth.currentUser
        user?.linkWithCredential(credential)?.addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val sub = subscribers[subscribers.size-1]
                    (sub as VerificationFragment).onLoginCompleted()
            } else
                Toast.makeText(activity, "error , unable to add phone number ", Toast.LENGTH_LONG)
                    .show()
        }
    }

    override fun onLoginCompleted() {

        Log.e("subscribers.size()()() ",""+subscribers.size)
        val sub = subscribers[subscribers.size-1]
                (sub as VerificationFragment).onLoginCompleted()

    }

    fun addSubscriber(fragment: Fragment) {
        subscribers.add(fragment)
    }

    override fun onVerificationFailed() {

        val sub = subscribers[subscribers.size-1]
            when{
                sub.javaClass.toString().contains("LoginFragment") -> {
                    (sub as LoginFragment).onVerificationFailed()
                }
                sub.javaClass.toString().contains("PhoneFragment") -> {
                    (sub as PhoneFragment).onVerificationFailed()
                }
                else -> {
                    (sub as VerificationFragment).onVerificationFailed()
                }
            }
    }
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        facebookAuth.onActivityResult(requestCode,resultCode,data)
    }
}
