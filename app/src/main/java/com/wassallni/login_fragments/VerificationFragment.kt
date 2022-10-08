package com.wassallni.login_fragments

import android.app.Activity
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.jakode.verifycodeedittext.CodeCompleteListener
import com.wassallni.*
import com.wassallni.databinding.FragmentVerificationBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VerificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VerificationFragment : Fragment ,LoginObserver {
    // TODO: Rename and change types of parameters

    var phoneNumber:String
    lateinit var countryCode:String
    private var viewModel= LoginViewModel()
    private val auth=FirebaseAuth.getInstance()
    private var presenter: LoginPresenter? =null
    lateinit var handler:Handler
    var seconds = 45
    private lateinit var binding:FragmentVerificationBinding

    constructor(phoneNumber: String,countryCode:String)  {
        this.phoneNumber=phoneNumber
        this.countryCode=countryCode

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentVerificationBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter=LoginPresenter.getInstance()
        var encryptedNumber=""

        for(i in 1..phoneNumber.length-3)
            encryptedNumber+="*"
        encryptedNumber+=phoneNumber.subSequence(phoneNumber.length-3,phoneNumber.length)

        val text = "<font color=#757575>SMS verification code sent to </font> <font color=#000000>${encryptedNumber}</font>"
        binding.tvGuide.text= HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY);

        viewModel.mutableDataCounterDown.observe(viewLifecycleOwner, Observer<String> {
            binding.tvCountDown.text=viewModel.mutableDataCounterDown.value
        })

        viewModel.mutableDataTvResendCodeVisibility.observe(viewLifecycleOwner, Observer<Int> {
            val visibility= viewModel.mutableDataTvResendCodeVisibility.value!!
            binding.tvResendCode.visibility=visibility
            if (visibility==View.INVISIBLE)
                binding.tvCountDown.visibility=View.VISIBLE
            else
                binding.tvCountDown.visibility=View.INVISIBLE

        })

        viewModel.startCounter()
        binding.phoneFragmentFab.setOnClickListener{
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.tvResendCode.paintFlags = binding.tvResendCode.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        setOnClickListener()

    }


    private fun setOnClickListener() {

        binding.verifyCodeEditText.setCompleteListener (object : CodeCompleteListener{
            override fun complete(complete: Boolean) {
                binding.verifyBtn.isEnabled = complete
            }
        })

        binding.verifyBtn.setOnClickListener {
            binding.verifyBtn.isEnabled=false
            val userCode = binding.verifyCodeEditText.text

            if (LoginViewModel.time_Out)
                binding.verifyCodeEditText.setCodeItemErrorLineDrawable()
            else
                verifyNumber()
        }
        binding.tvResendCode.setOnClickListener {
            binding.progressIndicator.visibility = View.VISIBLE
            presenter?.resendCode()
        }
    }


    override fun onCodeSent() {
        viewModel.resetCountDown()
        binding.progressIndicator.visibility=View.INVISIBLE
    }

    override fun onVerificationFailed(message: String) {
        binding.progressIndicator.visibility = View.INVISIBLE
        binding.verifyCodeEditText.setCodeItemErrorLineDrawable()
        Toast.makeText(activity,message,Toast.LENGTH_LONG).show()
    }

    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        onLoginCompleted()
    }

    override fun onLoginCompleted() {
        binding.progressIndicator.visibility=View.INVISIBLE

        val count=activity?.supportFragmentManager?.backStackEntryCount
        for (i in 1..count!!){
            activity?.supportFragmentManager?.popBackStack()
        }
        val transaction = activity?.supportFragmentManager?.beginTransaction();
        transaction?.replace(R.id.fr_layout,SuccessfulLoginFragment());
        transaction?.commit();

    }

    override fun onSignInWIthGoogleSuccessed() {
    }

    override fun onSignInWIthFacebookSuccessed() {
    }

    override fun onSignInWIthPhoneFailed(message: String) {
        Toast.makeText(activity,message,Toast.LENGTH_LONG).show()
        binding.progressIndicator.visibility = View.INVISIBLE

    }

    override fun onSignInWIthGoogleFailed(message: String) {
        binding.progressIndicator.visibility = View.INVISIBLE
    }

    override fun onSignInWIthFacebookFailed(message: String) {
        binding.progressIndicator.visibility = View.INVISIBLE

    }

    private fun verifyNumber(){
        binding.progressIndicator.visibility=View.VISIBLE
        val code=binding.verifyCodeEditText.text
        presenter?.verifyNumber(code)
    }


    override fun onLinkPhoneNumberFailed(message: String) {
        Toast.makeText(activity,message,Toast.LENGTH_LONG).show()
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
        Log.e("onDestroyView"," VerificationFragment " )
        presenter?.google?.signOut()
        LoginManager.getInstance().logOut()
    }
}
