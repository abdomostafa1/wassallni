package com.wassallni.ui.fragment.login_graph

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.wassallni.R
import com.wassallni.data.datasource.LoginUiState
import com.wassallni.databinding.FragmentVerificationBinding
import com.wassallni.ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val TAG = "VerificationFragment"

@AndroidEntryPoint
class VerificationFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var binding: FragmentVerificationBinding
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVerificationBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        showPhoneNumber()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.timeOut.collect {
                    if (it) {
                        binding.tvCountDown.visibility = View.INVISIBLE
                        binding.tvResendCode.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginUiState.collect {
                    when (it) {
                        is LoginUiState.CodeSent -> {
                            binding.progressIndicator.visibility = View.INVISIBLE
                            loginViewModel.startCounter()
                            binding.tvResendCode.visibility = View.INVISIBLE
                            binding.tvCountDown.visibility = View.VISIBLE
                        }
                        is LoginUiState.Loading -> {
                            binding.progressIndicator.visibility = View.VISIBLE
                        }
                        is LoginUiState.VerificationSuccess -> {
                            Log.e(TAG, "VerificationSuccess: true ")
                            loginViewModel.makeLoginRequest()
                        }
                        is LoginUiState.LoginSuccess -> {
                            findNavController().navigate(R.id.action_verificationFragment_to_successfulLoginFragment)
                        }
                        is LoginUiState.Error -> {

                            binding.progressIndicator.visibility = View.INVISIBLE
                            binding.verifyCodeEditText.setCodeItemErrorLineDrawable()
                            Toast.makeText(requireActivity(), it.errorMsg, Toast.LENGTH_LONG).show()
                            signOut()
                        }
                        else -> Unit
                    }
                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.counter.collect {
                    binding.tvCountDown.text = it
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Default) {
            loginViewModel.startCounter()
        }
        binding.tvResendCode.paintFlags =
            binding.tvResendCode.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        setOnClickListener()

    }


    private fun showPhoneNumber() {
        val phoneNumber = loginViewModel.phoneNumber!!
        var encryptedNumber = ""

        for (i in 1..phoneNumber.length - 3)
            encryptedNumber += "*"
        encryptedNumber += phoneNumber.subSequence(phoneNumber.length - 3, phoneNumber.length)
        val smsCodeSentTo = getString(R.string.sms_code_sent_to)
        val text =
            "<font color=#757575>$smsCodeSentTo</font>\n<font color=#000000>${encryptedNumber}</font>"
        binding.tvGuide.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)

    }

    private fun setOnClickListener() {

        binding.navigateUp.setOnClickListener {
            navController.navigateUp()
        }
        binding.verifyCodeEditText.setCompleteListener { complete ->
            binding.verifyBtn.isEnabled = complete
        }

        binding.verifyBtn.setOnClickListener {
            val smsCode = binding.verifyCodeEditText.text

            if (loginViewModel.timeOut.value)
                binding.verifyCodeEditText.setCodeItemErrorLineDrawable()
            else {
                lifecycleScope.launch {
                    loginViewModel.verifyWithFirebase(smsCode, requireActivity())
                }
            }
        }

        binding.tvResendCode.setOnClickListener {
            lifecycleScope.launch {
                loginViewModel.resendVerificationCode(requireActivity())
            }
        }
    }


    private fun signOut() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance().signOut()
        }
    }


    override fun onStop() {
        loginViewModel.resetUiState()
        super.onStop()
    }
}
