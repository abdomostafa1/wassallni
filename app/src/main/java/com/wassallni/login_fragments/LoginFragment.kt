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
import androidx.fragment.app.Fragment
import com.facebook.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.wassallni.*
import com.wassallni.R
import com.wassallni.databinding.FragmentLoginBinding


class LoginFragment(context: Context) : Fragment(R.layout.fragment_login) ,VerificationObserver , SignInCompletionObserver{

    private lateinit var binding: FragmentLoginBinding
    lateinit var controller: LoginController
    private lateinit var callbackManager:CallbackManager
    lateinit var facebookAuth: FacebookAuth
    var google=GoogleAuth(this)
    val auth=FirebaseAuth.getInstance()
    lateinit var handler:Handler
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

        controller=LoginController.getInstance()

        facebookAuth= context?.let { FacebookAuth(it) }!!
        facebookAuth.addSubscriber(this)

        binding.facebookLogo.setOnClickListener {
            //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
            facebookAuth.signIn(this)
        }
        binding.signIn.setOnClickListener {
            checkDataValidation()
        }
        binding.googleLogo.setOnClickListener{
            google.signIn()
        }

    }

    private fun checkDataValidation() {
         var phoneNumber = binding.numberEditText.text.toString()

                if(controller.isNumberValid(phoneNumber))
                    binding.numberEditText.error = "*Required"

            else {
                phoneNumber="+${binding.ccp.selectedCountryCode}${phoneNumber}"
                binding.progressIndicator.visibility=View.VISIBLE
                Log.e(TAG, "phoneNumber: $phoneNumber" )

                phoneAuth.sendVerificationCode(phoneNumber,null)
            }
        }

    }

    override fun loginCompleted() {
    }

    override fun codeSent(code: String,token: PhoneAuthProvider.ForceResendingToken) {

        val number= binding.numberEditText.text.toString().trim()
        val countryCode="+${binding.ccp.selectedCountryCode}"
            val transaction = activity?.supportFragmentManager?.beginTransaction();
            transaction?.replace(R.id.fr_layout, VerificationFragment(number,countryCode,code,token));
            transaction?.addToBackStack(null)
            transaction?.commit();
    }

    override fun verificationFailed() {
        binding.progressIndicator.visibility=View.INVISIBLE

    }

    override fun completeSignInWIthGoogle() {
       ifUserHasPhoneNumber()
    }

    override fun completeSignInWIthFacebook() {
        ifUserHasPhoneNumber()
    }

    private fun ifUserHasPhoneNumber(){
        val user=FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.e(TAG, "completeSignInWIthGoogle: ${user.phoneNumber}" )
            if(user.phoneNumber!=null) {

                val count=activity?.supportFragmentManager?.backStackEntryCount
                for (i in 1..count!!){
                    activity?.supportFragmentManager?.popBackStack()
                }
                val transaction = activity?.supportFragmentManager?.beginTransaction();
                transaction?.replace(R.id.fr_layout,SuccessfulLoginFragment());
                transaction?.commit();
            }
            else
                addPhoneNumberForUser()
        }
    }

    private fun addPhoneNumberForUser() {
        val transaction = activity?.supportFragmentManager?.beginTransaction();
        transaction?.replace(R.id.fr_layout, PhoneFragment());
        transaction?.addToBackStack(null)
        transaction?.commit();
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        FacebookAuth.callbackManager.onActivityResult(requestCode,resultCode,data)
    }

    override fun onDestroyView() {
        Log.e("onDestroyView"," onDestroyView " )

        super.onDestroyView()
    }
}