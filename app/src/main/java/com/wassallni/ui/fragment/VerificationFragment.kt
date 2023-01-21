package com.wassallni.ui.fragment

import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.jakode.verifycodeedittext.CodeCompleteListener
import com.wassallni.*
import com.wassallni.databinding.FragmentVerificationBinding
import com.wassallni.ui.viewmodel.LoginViewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VerificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VerificationFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private  val TAG = "VerificationFragment"
    private lateinit var binding: FragmentVerificationBinding
    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVerificationBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val phoneNumber = loginViewModel.phoneNumber!!
        var encryptedNumber = ""

        for (i in 1..phoneNumber.length - 3)
            encryptedNumber += "*"
        encryptedNumber += phoneNumber.subSequence(phoneNumber.length - 3, phoneNumber.length)

        val text =
            "<font color=#757575>SMS verification code sent to </font> <font color=#000000>${encryptedNumber}</font>"
        binding.tvGuide.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY);

        loginViewModel.counter.observe(viewLifecycleOwner) {
            binding.tvCountDown.text = it
        }

        loginViewModel.timeOut.observe(viewLifecycleOwner) {
            if (it) {
                binding.tvCountDown.visibility = View.INVISIBLE
                binding.tvResendCode.visibility = View.VISIBLE
            }
        }

        loginViewModel.codeSent.observe(viewLifecycleOwner) {
            binding.progressIndicator.visibility = View.INVISIBLE

            loginViewModel.startCounter()
            binding.tvResendCode.visibility = View.INVISIBLE
            binding.tvCountDown.visibility = View.VISIBLE

        }

        loginViewModel.verificationCompleted.observe(viewLifecycleOwner) {
            Log.e(TAG, "onView: $it")
            if (it==true) {
                findNavController().navigate(R.id.action_verificationFragment_to_successfulLoginFragment)
            }
            Log.e(TAG, "verificationCompleted ", )
        }

        loginViewModel.verificationFailed.observe(viewLifecycleOwner) {
            binding.progressIndicator.visibility = View.INVISIBLE
            binding.verifyCodeEditText.setCodeItemErrorLineDrawable()
            Toast.makeText(requireActivity(), it, Toast.LENGTH_LONG).show()
        }

        loginViewModel.startCounter()

        binding.tvResendCode.paintFlags =
            binding.tvResendCode.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        setOnClickListener()

    }


    private fun setOnClickListener() {

        binding.verifyCodeEditText.setCompleteListener(object : CodeCompleteListener {
            override fun complete(complete: Boolean) {
                binding.verifyBtn.isEnabled = complete
            }
        })

        binding.verifyBtn.setOnClickListener {
            binding.verifyBtn.isEnabled = false
            val smsCode = binding.verifyCodeEditText.text

            Log.e("timeOut.value", "${(loginViewModel.timeOut.value)}" )
            if (loginViewModel.timeOut.value == true)
                binding.verifyCodeEditText.setCodeItemErrorLineDrawable()
            else {
                binding.progressIndicator.visibility = View.VISIBLE
                loginViewModel.signInWithFirebase(smsCode)
            }
        }

        binding.tvResendCode.setOnClickListener {
            binding.progressIndicator.visibility = View.VISIBLE
            loginViewModel.resendVerificationCode()
        }
    }

}
