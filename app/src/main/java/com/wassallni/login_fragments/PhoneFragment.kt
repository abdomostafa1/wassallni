package com.wassallni.login_fragments

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.wassallni.GoogleAuth
import com.wassallni.PhoneAuth
import com.wassallni.R
import com.wassallni.VerificationObserver
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
class PhoneFragment : Fragment() ,VerificationObserver{

    lateinit var phoneAuth: PhoneAuth
    lateinit var binding:FragmentPhoneBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding=FragmentPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        phoneAuth=PhoneAuth()
        phoneAuth.addSubscriber(this)
        binding.navigationFab.setOnClickListener{
            activity?.supportFragmentManager?.popBackStack()
        }

        val auth=FirebaseAuth.getInstance()
        binding.tvUserName.text="Hi, ${auth.currentUser?.displayName} "

        binding.signIn.setOnClickListener {
            checkDataValidation()
        }

        binding.numberEditText.addTextChangedListener(textWatcher)
    }


    private fun checkDataValidation() {
        var phoneNumber = binding.numberEditText.text.toString()

                phoneNumber="+${binding.ccp.selectedCountryCode}${phoneNumber}"
                binding.progressIndicator.visibility=View.VISIBLE
                phoneAuth.sendVerificationCode(phoneNumber,null)
            }


    private val textWatcher=object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            binding.signIn.isEnabled = s.toString().isNotEmpty()
        }
    }

    override fun codeSent(code: String,p1: PhoneAuthProvider.ForceResendingToken) {
        val number= binding.numberEditText.text.toString().trim()
        val countryCode="+${binding.ccp.selectedCountryCode}"
        val transaction = activity?.supportFragmentManager?.beginTransaction();
        transaction?.replace(R.id.fr_layout, VerificationFragment(number,countryCode,code,p1));
        transaction?.addToBackStack(null)
        transaction?.commit();
    }

    override fun loginCompleted() {
    }

    override fun verificationFailed() {
        binding.progressIndicator.visibility=View.INVISIBLE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoginManager.getInstance().logOut()
        GoogleAuth.googleSignInClient.signOut()

    }
}