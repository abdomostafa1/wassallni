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
import com.wassallni.databinding.FragmentRegisterBinding
import com.wassallni.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private val TAG = "RegisterFragment"

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by activityViewModels() {
        LoginViewModel.getFactory(
            requireActivity()
        )
    }
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

        loginViewModel.usernameError.observe(viewLifecycleOwner) {
            binding.nameEdt.error = getString(it)
        }

        loginViewModel.phoneNumberError.observe(viewLifecycleOwner) {
            binding.phoneEdt.error = getString(it)
        }


        loginViewModel.loading.observe(viewLifecycleOwner) {
            if (it)
                binding.loader.visibility = View.VISIBLE
            else
                binding.loader.visibility = View.INVISIBLE
        }

        loginViewModel.codeSent.observe(viewLifecycleOwner) {
            if (it == true) {
                navController.navigate(R.id.action_register_to_verificationFragment)
            }
        }
        loginViewModel.onFailure.observe(viewLifecycleOwner) {
            binding.loader.visibility=View.INVISIBLE
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            Log.e("login error",it)
        }


        binding.verify.setOnClickListener {
            val name = binding.nameEdt.text.toString()
            val number = binding.phoneEdt.text.toString()
            val phoneNumber = "+${binding.ccp.selectedCountryCode}${number}"
            Log.e(TAG, "$phoneNumber ")
            lifecycleScope.launch (Dispatchers.IO){
                loginViewModel.verifyPhoneNumber(name, phoneNumber)
            }
        }
    }
    override fun onResume() {
        super.onResume()
    //    binding.loader.visibility = View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}