package com.wassallni.firebase.authentication

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.wassallni.R

class GoogleAuth(private var context: Context) {

    //private late init var presenter: LoginPresenter
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient

    init {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken((context as AppCompatActivity).getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
    }


    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    //                presenter.onSignInWIthGoogleSuccessed()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    //              presenter.onSignInWIthGoogleFailed(task.exception.toString())
                }
            }
    }


//    fun notifyPresenter(presenter: LoginPresenter) {
//        this.presenter = presenter
//    }

    fun signOut() {
        googleSignInClient.signOut()
    }

}

