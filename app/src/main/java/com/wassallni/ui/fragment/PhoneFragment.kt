package com.wassallni.ui.fragment

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
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

    lateinit var binding: FragmentPhoneBinding

    //private  var presenter= LoginPresenter.getInstance()
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

        binding.navigationFab.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val auth = FirebaseAuth.getInstance()
        binding.tvUserName.text = "Hi, ${auth.currentUser?.displayName} "

        binding.signIn.setOnClickListener {
            checkDataValidation()
        }

        binding.numberEditText.addTextChangedListener(textWatcher)

        // ViewModelProvider(requireActivity()).get()
    }


    private fun checkDataValidation() {
        var phoneNumber = binding.numberEditText.text.toString()

//        if (presenter?.isNumberInValid(phoneNumber) == true)
//            binding.numberEditText.error = "*Required"
//        else {
//            phoneNumber = "+${binding.ccp.selectedCountryCode}${phoneNumber}"
//            binding.progressIndicator.visibility = View.VISIBLE
//
//            presenter?.sendVerificationCode(phoneNumber)
//        }
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

 //   override fun onCodeSent() {

//            val number = binding.numberEditText.text.toString().trim()
//            val countryCode = "+${binding.ccp.selectedCountryCode}"
//            val transaction = activity?.supportFragmentManager?.beginTransaction();
//            transaction?.replace(
//                R.id.fr_layout,
//                VerificationFragment(number, countryCode)
//            );
//            transaction?.addToBackStack(null)
//            transaction?.commit();
    }

//    override fun onVerificationFailed(message: String) {
//        binding.progressIndicator.visibility = View.INVISIBLE
//        Toast.makeText(activity,message, Toast.LENGTH_LONG).show()
//    }
//    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//        onLoginCompleted()
//    }
//
//    override fun onSignInWIthGoogleSuccessed() {
//
//    }
//
//    override fun onSignInWIthFacebookSuccessed() {
//    }
//
//    override fun onSignInWIthPhoneFailed(message: String) {
//        Toast.makeText(activity,message,Toast.LENGTH_LONG).show()
//    }
//
//    override fun onSignInWIthGoogleFailed(message: String) {
//
//    }
//
//    override fun onSignInWIthFacebookFailed(message: String) {
//
//    }
//
//    override fun onLoginCompleted() {
//
//    }
//
//    override fun onLinkPhoneNumberFailed(message: String) {
//
//    }
//
//    override fun onStart() {
//        super.onStart()
//        presenter?.setActiveFragment(this)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        presenter?.setActiveFragment(this)
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.e("onDestroyView"," PhoneFragment " )
//            LoginManager.getInstance().logOut()
//            presenter?.google?.signOut()
//        }
//    }
