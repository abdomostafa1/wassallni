package com.wassallni.ui.fragment.main_graph

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.wassallni.R
import com.wassallni.data.model.uiState.MainUiState
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
    private val rateDriverVM:RateDriverVM by viewModels()
    private val args:RateDriverFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRateDriverBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.ratingBar.onRatingBarChangeListener =
            OnRatingBarChangeListener { _, rating, fromUser ->
                changeRatingText(rating)
            }
        binding.rateBtn.setOnClickListener {
            val message=binding.comment.text.toString()
            val stars=binding.ratingBar.rating
            Log.e(TAG, "stars=$stars " )
            rateDriverVM.rateDriver(stars,message,args.tripId,args.driverId)
        }
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                rateDriverVM.rateUiState.collect { state ->
                    when (state) {
                        is RateDriverUiState.Loading -> {
                            binding.progressBar.visibility=View.VISIBLE
                        }
                        is RateDriverUiState.Success -> {
                            binding.progressBar.visibility=View.GONE
                            findNavController().navigateUp()
                        }
                        is RateDriverUiState.Error -> {
                            binding.progressBar.visibility=View.GONE
                            Toast.makeText(requireActivity(),state.errorMsg,Toast.LENGTH_LONG).show()
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

}