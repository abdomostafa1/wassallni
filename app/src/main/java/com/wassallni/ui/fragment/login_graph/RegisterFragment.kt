package com.wassallni.ui.fragment.login_graph

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.wassallni.R
import com.wassallni.data.datasource.LoginUiState
import com.wassallni.databinding.FragmentRegisterBinding
import com.wassallni.ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val TAG = "RegisterFragment"

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        lifecycleScope.launchWhenStarted {
            loginViewModel.usernameError.collect {
                if (it != 0)
                    binding.nameEdt.error = getString(it)
            }
        }

        lifecycleScope.launchWhenStarted {
            loginViewModel.phoneNumberError.collect {
                if (it != 0)
                    binding.phoneEdt.error = getString(it)
            }
        }

        lifecycleScope.launchWhenStarted {

            loginViewModel.loginUiState.collect {

                when (it) {
                    is LoginUiState.Loading -> {
                        Log.e(TAG, "hygine State ")
                        showLoading()
                    }
                    is LoginUiState.CodeSent -> {
                        loginViewModel.resetUiState()
                        navController.navigate(R.id.action_register_to_verificationFragment)
                    }
                    is LoginUiState.Error -> {
                        Log.e(TAG, "LoginUiState.Error " )
                        hideLoading()
                        Toast.makeText(requireContext(), it.errorMsg, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit

                }
            }

        }


        binding.verify.setOnClickListener {
            val name = binding.nameEdt.text.toString()
            val number = binding.phoneEdt.text.toString()
            val phoneNumber = "+${binding.ccp.selectedCountryCode}${number}"
//            Log.e(TAG, "$phoneNumber ")
//            lifecycleScope.launch(Dispatchers.IO) {
//                loginViewModel.verifyPhoneNumber(name, phoneNumber)
//            }

            loginViewModel.phoneNumber=phoneNumber
            loginViewModel.name=name
            navController.navigate(R.id.action_register_to_verificationFragment)
        }
    }

    private fun showLoading() {
        binding.loader.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.loader.visibility = View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}