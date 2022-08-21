package com.wassallni

import android.content.Context
import android.content.Intent
import android.service.controls.ControlsProviderService
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.events.Subscriber

class FacebookAuth {
    companion object {
         var callbackManager: CallbackManager =CallbackManager.Factory.create()
    }
    lateinit var context: Context
    val auth=FirebaseAuth.getInstance()
    var subscribers: ArrayList<SignInCompletionObserver> = ArrayList<SignInCompletionObserver>()

    constructor(context: Context) {

        this.context = context
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    Log.e("Success", "Login")
                    if (loginResult != null) {
                        handleFacebookAccessToken(loginResult.accessToken)
                    }
                }

                override fun onCancel() {
                    Toast.makeText(context, "facebook:onCancel.", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: FacebookException) {
                    Toast.makeText(context, "facebook:onError.", Toast.LENGTH_SHORT).show()
                }
            })


    }

    public fun signIn(fragment:Fragment){
        LoginManager.getInstance().logInWithReadPermissions(
            fragment,
            listOf("email", "public_profile")
        );

    }
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(ControlsProviderService.TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener((context as AppCompatActivity)) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    notifySubscribers()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(
                        ControlsProviderService.TAG, " signInWithCredential:failure", task.exception)
                    if (credential.provider == "facebook.com")
                        Toast.makeText(context, "there is a Google account with the same email address", Toast.LENGTH_LONG).show()
                    else
                        Toast.makeText(context, "there is a facebook account with the same email address", Toast.LENGTH_LONG).show()

                    LoginManager.getInstance().logOut()

                }
            }
    }

    fun addSubscriber(subscriber: SignInCompletionObserver) {
        subscribers.add(subscriber)
    }

     private fun notifySubscribers() {
        for (i in subscribers)
            i.completeSignInWIthFacebook()
    }


}