package com.wassallni

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuth {
    var context: Context = LoginActivity.context
    lateinit var subscriber: SignInCompletionObserver
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()


    private val RC_SIGN_IN = 9001

    companion object{
          private val googleSignInOptions: GoogleSignInOptions= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestIdToken((LoginActivity.context as AppCompatActivity).getString(R.string.default_web_client_id))
              .requestEmail()
              .requestProfile()
              .build()

         val googleSignInClient: GoogleSignInClient= GoogleSignIn.getClient(LoginActivity.context, googleSignInOptions)
    }

    constructor(subscriber: SignInCompletionObserver) {
        this.subscriber=subscriber
    }

    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)

    }

    private var launcher = (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result ->
        val data = result.data

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val exception=task.exception
    if(task.isSuccessful) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)
            Log.d("Sign in with Google ", "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w("Sign in with Google ", "Google sign in failed", e)
        }
    }
        else
        Log.w("Google Exception ", "${exception.toString()}")

    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        subscriber.completeSignInWIthGoogle()
                    } else
                        Log.w(TAG, "signInWithCredential:failure", task.exception)

                }
    }


}
