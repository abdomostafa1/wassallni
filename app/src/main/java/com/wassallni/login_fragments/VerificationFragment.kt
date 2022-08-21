package com.wassallni.login_fragments

import android.app.Activity
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
import com.google.firebase.auth.PhoneAuthProvider
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
class VerificationFragment : Fragment  , VerificationObserver{
    // TODO: Rename and change types of parameters

    var phoneNumber:String
    lateinit var countryCode:String
    lateinit var verificationCode:String
    lateinit var token: PhoneAuthProvider.ForceResendingToken
    lateinit var phoneAuth: PhoneAuth
    private var viewModel= LoginViewModel()
    private val auth=FirebaseAuth.getInstance()
    lateinit var handler:Handler
    var seconds = 45
    private lateinit var binding:FragmentVerificationBinding

    constructor(phoneNumber: String,countryCode:String, code: String?, p1: PhoneAuthProvider.ForceResendingToken?)  {
        this.phoneNumber=phoneNumber
        this.countryCode=countryCode

        if (code != null) {
            verificationCode=code
        }
        if (p1 != null) {
            token=p1
        }
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
        phoneAuth=PhoneAuth()
        phoneAuth.addSubscriber(this)
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
            phoneAuth.resendCode(countryCode+phoneNumber, token)
        }
    }

     private fun verifyNumber(){
         binding.progressIndicator.visibility=View.VISIBLE
        val user=auth.currentUser
        val userCode=binding.verifyCodeEditText.text
        val credential = PhoneAuthProvider.getCredential(verificationCode, userCode)

        if(user==null)
            signInWithPhoneNumber(credential)
        else
            addPhoneNumber(credential)

    }

    private fun signInWithPhoneNumber(credential:PhoneAuthCredential){

        phoneAuth.signIn(credential)
    }

    private fun addPhoneNumber(credential:PhoneAuthCredential){
            val user=auth.currentUser
            user?.linkWithCredential(credential)?.addOnCompleteListener { task->

                if(task.isSuccessful)
                    loginCompleted()
                else
                    Toast.makeText(activity,"error , unable to add phone number ", Toast.LENGTH_LONG).show()
            }


    }

    override fun codeSent(code:String,p1: PhoneAuthProvider.ForceResendingToken) {

        // in case of you resend verification code
        verificationCode=code
        token=p1
        viewModel.resetCountDown()
        binding.progressIndicator.visibility=View.INVISIBLE

    }

     override fun loginCompleted() {
         binding.progressIndicator.visibility=View.INVISIBLE

         val count=activity?.supportFragmentManager?.backStackEntryCount
            for (i in 1..count!!){
                activity?.supportFragmentManager?.popBackStack()
            }
         val transaction = activity?.supportFragmentManager?.beginTransaction();
         transaction?.replace(R.id.fr_layout,SuccessfulLoginFragment());
         transaction?.commit();

         handler = Handler(Looper.getMainLooper())
         handler.postDelayed(runnable,5000)

    }

    override fun verificationFailed() {
        binding.progressIndicator.visibility = View.INVISIBLE
        binding.verifyCodeEditText.setCodeItemErrorLineDrawable()
    }

    val runnable=object :Runnable{
        override fun run() {
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
            Log.e("finish activity"," runnable is called " )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        GoogleAuth.googleSignInClient.signOut()
        LoginManager.getInstance().logOut()
    }
}
