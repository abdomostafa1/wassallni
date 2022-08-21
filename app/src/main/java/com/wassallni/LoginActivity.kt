package com.wassallni

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.wassallni.login_fragments.LoginFragment


class LoginActivity : AppCompatActivity() {


    val auth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        context =this
        frameLayout=findViewById(R.id.fr_layout)
        val transaction = supportFragmentManager.beginTransaction();
        transaction.replace(R.id.fr_layout,LoginFragment(this));
        //transaction.addToBackStack(null)
        transaction.commit();
        auth.addAuthStateListener {
            if(auth.currentUser==null){
                GoogleAuth.googleSignInClient.signOut()
                LoginManager.getInstance().logOut()
            }
        }

    }

    companion object{
        lateinit var frameLayout:FrameLayout
        lateinit var context:Context
    }


}
