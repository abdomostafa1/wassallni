package com.wassallni.login_fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.facebook.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.wassallni.*
import com.wassallni.R
import com.wassallni.databinding.FragmentLoginBinding


class LoginFragment(context: Context) : Fragment(R.layout.fragment_login){

    private lateinit var binding: FragmentLoginBinding
    private var controller: LoginController? =null
    private lateinit var callbackManager:CallbackManager
    val googleAuth=GoogleAuth.getInstance()
    val auth=FirebaseAuth.getInstance()
    lateinit var handler:Handler
    lateinit var  phoneNumber:String
    companion object {
        lateinit var token:PhoneAuthProvider.ForceResendingToken
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
        controller=LoginController.getInstance(LoginActivity.context)
        controller?.addSubscriber(this)
        Log.e("subs.size(LoginFr)",""+ LoginController.subscribers.size)
        binding.facebookLogo.setOnClickListener {
            //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
            controller?.signInWithFacebook(this)
        }
        binding.signIn.setOnClickListener {
            checkDataValidation()
        }
        binding.googleLogo.setOnClickListener{
            controller?.signInWithGoogle()
        }

    }

    private fun checkDataValidation() {
          phoneNumber = binding.numberEditText.text.toString()
                if(controller?.isNumberValid(phoneNumber) == true)
                    binding.numberEditText.error = "*Required"

            else {
                phoneNumber="+${binding.ccp.selectedCountryCode}${phoneNumber}"
                binding.progressIndicator.visibility=View.VISIBLE

                controller?.sendVerificationCode(phoneNumber)
            }
        }

    fun onCodeSent(){
        val number= binding.numberEditText.text.toString().trim()
        val countryCode="+${binding.ccp.selectedCountryCode}"
            val transaction = activity?.supportFragmentManager?.beginTransaction();
            transaction?.replace(R.id.fr_layout, VerificationFragment(number,countryCode));
            transaction?.addToBackStack(null)
            transaction?.commit();

    }
    fun onVerificationFailed() {
        binding.progressIndicator.visibility=View.INVISIBLE
    }

    private fun onLoginSuccess() {

            val activity=LoginActivity.context as AppCompatActivity
            val count=activity.supportFragmentManager.backStackEntryCount
            for (i in 1..count){
                activity.supportFragmentManager.popBackStack()
            }
            val transaction = activity.supportFragmentManager.beginTransaction();
            transaction.replace(R.id.fr_layout,SuccessfulLoginFragment());
            transaction.commit();
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
                onLoginSuccess()
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
        controller?.onActivityResult(requestCode,resultCode,data)
    }
    fun removeFromControllerSubscriber(){
        val subscribers=LoginController.subscribers
        subscribers.removeAt(subscribers.size-1)

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroyView"," LoginFragment " )
        removeFromControllerSubscriber()
    }

}
