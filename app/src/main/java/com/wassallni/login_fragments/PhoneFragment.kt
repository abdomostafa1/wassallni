package com.wassallni.login_fragments

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.wassallni.GoogleAuth
import com.wassallni.LoginActivity
import com.wassallni.PhoneAuth
import com.wassallni.R
import com.wassallni.databinding.FragmentPhoneBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PhoneFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhoneFragment : Fragment() {

    lateinit var phoneAuth: PhoneAuth
    lateinit var binding: FragmentPhoneBinding
    private var controller: LoginController? =null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller=LoginController.getInstance(LoginActivity.context)
        controller?.addSubscriber(this)
        binding.navigationFab.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val auth = FirebaseAuth.getInstance()
        binding.tvUserName.text = "Hi, ${auth.currentUser?.displayName} "

        binding.signIn.setOnClickListener {
            checkDataValidation()
        }

        binding.numberEditText.addTextChangedListener(textWatcher)
    }


    private fun checkDataValidation() {
        var phoneNumber = binding.numberEditText.text.toString()

        if (controller?.isNumberValid(phoneNumber) == true)
            binding.numberEditText.error = "*Required"
        else {
            phoneNumber = "+${binding.ccp.selectedCountryCode}${phoneNumber}"
            binding.progressIndicator.visibility = View.VISIBLE

            controller?.sendVerificationCode(phoneNumber)
        }
    }

        private val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                binding.signIn.isEnabled = s.toString().isNotEmpty()
            }
        }

         fun onCodeSent() {
            val number = binding.numberEditText.text.toString().trim()
            val countryCode = "+${binding.ccp.selectedCountryCode}"
            val transaction = activity?.supportFragmentManager?.beginTransaction();
            transaction?.replace(
                R.id.fr_layout,
                VerificationFragment(number, countryCode)
            );
            transaction?.addToBackStack(null)
            transaction?.commit();
        }

         fun onVerificationFailed() {
            binding.progressIndicator.visibility = View.INVISIBLE
        }

    fun removeFromControllerSubscriber(){
        val subscribers=LoginController.subscribers
        subscribers.removeAt(subscribers.size-1)

    }
    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroyView"," PhoneFragment " )
        removeFromControllerSubscriber()
            LoginManager.getInstance().logOut()
            GoogleAuth.googleSignInClient.signOut()
            removeFromControllerSubscriber()
        }
    }