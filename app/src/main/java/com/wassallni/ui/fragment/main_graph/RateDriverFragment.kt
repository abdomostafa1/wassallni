package com.wassallni.ui.fragment.main_graph

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.wassallni.R
import com.wassallni.data.model.Driver
import com.wassallni.data.model.Rating
import com.wassallni.data.model.uiState.RateDriverUiState
import com.wassallni.databinding.FragmentRateDriverBinding
import com.wassallni.ui.viewmodel.RateDriverVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val TAG = "RateDriverFragment"

@AndroidEntryPoint
class RateDriverFragment : Fragment() {

    lateinit var binding: FragmentRateDriverBinding
    private val rateDriverVM: RateDriverVM by viewModels()
    private val args: RateDriverFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRateDriverBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        rateDriverVM.getDriverInfo(args.driverId)
        binding.ratingBar.onRatingBarChangeListener =
            OnRatingBarChangeListener { _, rating, fromUser ->
                changeRatingText(rating)
            }
        binding.rateBtn.setOnClickListener {
            val message = binding.comment.text.toString()
            val ratingAverage = binding.ratingBar.rating
            rateDriverVM.rateDriver(Rating(args.driverId, message, ratingAverage))
        }
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                rateDriverVM.driverInfo.collect {
                    if (it != null) {
                        updateUi(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                rateDriverVM.rateUiState.collect { state ->
                    when (state) {
                        is RateDriverUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is RateDriverUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                getString(R.string.thank_you_for_your_feedback),
                                Snackbar.LENGTH_LONG
                            ).show()
                            findNavController().navigateUp()
                        }

                        is RateDriverUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireActivity(), state.errorMsg, Toast.LENGTH_LONG)
                                .show()
                            Log.e(TAG, "error:${state.errorMsg} ")
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun changeRatingText(rating: Float) {
        if (rating > 4)
            binding.rateTv.text = requireActivity().getString(R.string.rating_excellent)
        else if (rating > 3)
            binding.rateTv.text = requireActivity().getString(R.string.rating_good)
        else if (rating > 2)
            binding.rateTv.text = requireActivity().getString(R.string.rating_average)
        else if (rating > 1)
            binding.rateTv.text = requireActivity().getString(R.string.rating_poor)
        else
            binding.rateTv.text = requireActivity().getString(R.string.rating_bad)
    }

    private fun updateUi(it: Driver) {
        Glide.with(this).load(it.image).circleCrop().into(binding.driverImg)
        binding.yourOpinion.append(" ${it.name}")
    }

}