package com.wassallni.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.facebook.*
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.wassallni.*
import com.wassallni.R
import com.wassallni.databinding.FragmentLoginBinding


class LoginFragment(context: Context) : Fragment(R.layout.fragment_login), LoginObserver {

    private lateinit var binding: FragmentLoginBinding
    private var presenter: LoginPresenter? =null
    private lateinit var callbackManager:CallbackManager
    val auth=FirebaseAuth.getInstance()
    lateinit var  phoneNumber:String

    private var launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    val account=task.result
                    presenter?.google?.firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    presenter?.onSignInWIthGoogleFailed(e.toString())
                }
            } else {
                Log.w("Google Exception ", "${exception.toString()}")
                presenter?.onSignInWIthGoogleFailed(exception.toString())
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //facebookBtn = view.findViewById(R.id.facebook_login_button)
        presenter=LoginPresenter.getInstance()
        activity?.let {  presenter?.initializePresenter(it) }
        binding.facebookLogo.setOnClickListener {
            //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
            presenter?.signInWithFacebook(this)
        }
        binding.signIn.setOnClickListener {
            checkDataValidation()
        }
        binding.googleLogo.setOnClickListener{

            val signInIntent =presenter?.google?.getSignInIntent()
            launcher.launch(signInIntent)
        }

    }

    private fun checkDataValidation() {
          phoneNumber = binding.numberEditText.text.toString()
                if(presenter?.isNumberInValid(phoneNumber) == true)
                    binding.numberEditText.error = "*Required"

            else {
                phoneNumber="+${binding.ccp.selectedCountryCode}${phoneNumber}"
                binding.progressIndicator.visibility=View.VISIBLE

                presenter?.sendVerificationCode(phoneNumber)
            }
        }

    override fun onCodeSent() {
        val number= binding.numberEditText.text.toString().trim()
        val countryCode="+${binding.ccp.selectedCountryCode}"
        val transaction = activity?.supportFragmentManager?.beginTransaction();
        transaction?.replace(R.id.fr_layout, VerificationFragment(number,countryCode));
        transaction?.addToBackStack(null)
        transaction?.commit();
    }

    override fun onVerificationFailed(message: String) {
        binding.progressIndicator.visibility=View.INVISIBLE
        Toast.makeText(activity,message,Toast.LENGTH_LONG).show()
    }

    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        onLoginCompleted()
    }

    override fun onSignInWIthGoogleSuccessed() {
        ifUserHasPhoneNumber()
    }

    override fun onSignInWIthFacebookSuccessed() {
        ifUserHasPhoneNumber()
    }

    override fun onSignInWIthPhoneFailed(message: String) {
        Toast.makeText(activity,message,Toast.LENGTH_LONG).show()
    }

    override fun onSignInWIthGoogleFailed(message: String) {
        Log.e("it fails 😒😒 ", " sad " )
        Toast.makeText(activity,message,Toast.LENGTH_LONG).show()
    }

    override fun onSignInWIthFacebookFailed(message: String) {
        Toast.makeText(activity,message,Toast.LENGTH_LONG).show()
    }

    override fun onLoginCompleted() {
        val activity=LoginActivity.context as AppCompatActivity
        val count=activity.supportFragmentManager.backStackEntryCount
        for (i in 1..count){
            activity.supportFragmentManager.popBackStack()
        }
        val transaction = activity.supportFragmentManager.beginTransaction();
        transaction.replace(R.id.fr_layout,SuccessfulLoginFragment());
        transaction.commit();
    }

    override fun onLinkPhoneNumberFailed(message: String) {
    }

    public fun onSignInWIthGoogleSuccess() {
        ifUserHasPhoneNumber()

    }
    public fun onSignInWIthFacebookSuccess() {
        ifUserHasPhoneNumber()

    }

    private fun ifUserHasPhoneNumber() {
        val user=FirebaseAuth.getInstance().currentUser
        if (user?.phoneNumber != null)
                onLoginCompleted()
            else
                addPhoneNumberForUser()
    }

    private fun addPhoneNumberForUser() {
        val transaction = activity?.supportFragmentManager?.beginTransaction();
        transaction?.replace(R.id.fr_layout, PhoneFragment());
        transaction?.addToBackStack(null)
        transaction?.commit();
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter?.onActivityResult(requestCode,resultCode,data)
    }
    override fun onStart() {
        super.onStart()
        presenter?.setActiveFragment(this)
    }

    override fun onResume() {
        super.onResume()
        presenter?.setActiveFragment(this)

    }
    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroyView"," LoginFragment " )
        presenter?.google?.signOut()
        LoginManager.getInstance().logOut()
    }

}
