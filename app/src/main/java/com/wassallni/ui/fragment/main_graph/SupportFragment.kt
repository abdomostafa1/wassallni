package com.wassallni.ui.fragment.main_graph

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.wassallni.R
import com.wassallni.data.model.uiState.SupportUiState
import com.wassallni.databinding.ChatMessageBinding
import com.wassallni.databinding.FragmentBookedTripBinding
import com.wassallni.databinding.FragmentSupportBinding
import com.wassallni.ui.viewmodel.SupportVM
import com.wassallni.utils.DateUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SupportFragment : Fragment() {

    private val viewModel: SupportVM by viewModels()
    var latestView: ChatMessageBinding? = null
    lateinit var binding: FragmentSupportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSupportBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.sendMessage.setOnClickListener {
            sendMessage()
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.supportUiState.collect { state ->
                    when (state) {
                        is SupportUiState.Loading -> {
                            showLoadingState()
                        }
                        is SupportUiState.Success -> {
                            showSuccessState()
                        }
                        is SupportUiState.Error -> {
                            showErrorState()
                            val message=requireActivity().getString(R.string.error_occurred)
                            Toast.makeText(requireActivity(),message , Toast.LENGTH_SHORT)
                                .show()
                        }
                        else -> Unit
                    }
                }
            }
        }

    }

    private fun sendMessage() {
        val message = binding.chatEditText.text.toString()
        if (message == "")
            return

        val timeStamp = System.currentTimeMillis() / 1000
        val currentTime = DateUseCase.fromMillisToString1(timeStamp)
        val chatView = ChatMessageBinding.inflate(layoutInflater)
        chatView.message.text = message
        chatView.time.text = currentTime
        binding.linearLayout.addView(chatView.root)
        latestView = chatView
        binding.chatEditText.setText("")
        scrollToLatestView()

        viewModel.sendFeedback(message)

    }

    private fun scrollToLatestView() {
        lifecycleScope.launch(Dispatchers.IO) {
            delay(500)
            val latestView = binding.linearLayout.getChildAt(binding.linearLayout.childCount - 1)
            binding.scrollView2.scrollTo(0, latestView.bottom)

        }
    }

    private fun showLoadingState() {
        binding.sendMessage.visibility = View.GONE
        binding.loader.visibility = View.VISIBLE

    }

    private fun showSuccessState() {
        binding.loader.visibility = View.GONE
        binding.sendMessage.visibility = View.VISIBLE
        latestView?.seenIcon?.visibility = View.VISIBLE

    }

    private fun showErrorState() {
        binding.loader.visibility = View.GONE
        binding.sendMessage.visibility = View.VISIBLE
        binding.linearLayout.removeViewAt(binding.linearLayout.childCount - 1)
    }


}