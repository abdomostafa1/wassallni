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
import com.wassallni.login_fragments.LoginPresenter

class FacebookAuth (private val presenter:LoginPresenter,val context: Context){

    var callbackManager: CallbackManager =CallbackManager.Factory.create()
    val auth=FirebaseAuth.getInstance()

    init {

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    Log.e("Success", "Login")
                    if (loginResult != null) {
                        handleFacebookAccessToken(loginResult.accessToken)
                    }
                }

                override fun onCancel() {
                    presenter.onSignInWIthFacebookFailed("Facebook login is Cancelled")
                }

                override fun onError(exception: FacebookException) {
                    exception.message?.let { presenter.onSignInWIthFacebookFailed(it) }
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
                    presenter.onSignInWIthFacebookSuccessed()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(
                        ControlsProviderService.TAG, " signInWithCredential:failure", task.exception)
                    if (credential.provider == "facebook.com")
                        task.exception?.message?.let { presenter.onSignInWIthFacebookFailed(it) }
                    else
                        task.exception?.message?.let { presenter.onSignInWIthGoogleFailed(it) }

                    LoginManager.getInstance().logOut()

                }
            }
    }



    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        callbackManager.onActivityResult(requestCode,resultCode,data)
    }

}