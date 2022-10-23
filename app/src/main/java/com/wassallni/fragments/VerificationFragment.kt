package com.wassallni.fragments

import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
class VerificationFragment : Fragment , LoginObserver {
    // TODO: Rename and change types of parameters

    var phoneNumber:String
    lateinit var countryCode:String
    private var presenter: LoginPresenter? =null
    lateinit var handler:Handler
    var seconds = 45
    var timeOut:Boolean=false
    private lateinit var binding:FragmentVerificationBinding

    constructor(phoneNumber: String,countryCode:String)  {
        this.phoneNumber=phoneNumber
        this.countryCode=countryCode
        handler = Handler(Looper.getMainLooper())
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

        startCounter()
        binding.phoneFragmentFab.setOnClickListener{
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.tvResendCode.paintFlags = binding.tvResendCode.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        setOnClickListener()

    }

     fun countDown() {
        handler.postDelayed(runnable, 1000)
    }
     fun startCounter() {

        handler.postDelayed(runnable, 0)
    }
    private val runnable = Runnable {
        val minutes=0
        binding.tvCountDown.setText(String.format("%02d",minutes)+":"+String.format("%02d",seconds))
        --seconds
        if (seconds != -1)
            countDown()
        else
            notifyTimeOut()
    }
    private fun notifyTimeOut() {
        timeOut=true
        handler.removeCallbacks(runnable)
        binding.tvCountDown.visibility=View.INVISIBLE
        binding.tvResendCode.visibility=View.VISIBLE

    }
    fun resetCountDown() {
        binding.tvResendCode.visibility = View.INVISIBLE
        binding.tvCountDown.visibility = View.VISIBLE
        seconds = 45
        handler.postDelayed(runnable, 0)
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

            if (timeOut)
                binding.verifyCodeEditText.setCodeItemErrorLineDrawable()
            else
                verifyNumber()
            timeOut=false
        }
        binding.tvResendCode.setOnClickListener {
            binding.progressIndicator.visibility = View.VISIBLE
            presenter?.resendCode()
        }
    }

    override fun onCodeSent() {
        resetCountDown()
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
