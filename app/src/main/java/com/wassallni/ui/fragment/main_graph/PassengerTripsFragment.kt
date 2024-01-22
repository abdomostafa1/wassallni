package com.wassallni.ui.fragment.main_graph

import android.os.Bundle
import android.util.Log
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
import com.wassallni.adapter.ItemDecorator
import com.wassallni.adapter.BookedTripsAdapter
import com.wassallni.data.model.uiState.MyTripsUiState
import com.wassallni.databinding.FragmentMyTripsBinding
import com.wassallni.ui.viewmodel.PassengerTripsVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class
PassengerTripsFragment : Fragment() {

    companion object {
        var UPCOMING_TRIP: Boolean = true
    }

    lateinit var binding: FragmentMyTripsBinding
    private val viewModel by viewModels<PassengerTripsVM>()

    @Inject
    lateinit var adapter: BookedTripsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyTripsBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.recyclerView.adapter = adapter
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen._16sdp)
        binding.recyclerView.addItemDecoration(ItemDecorator(spacingInPixels))

        viewModel.getReservedTrips()

        binding.errorState.retry.setOnClickListener {
            val id = binding.toggleButton.checkedButtonId
            UPCOMING_TRIP = id == binding.upcoming.id

            viewModel.getReservedTrips()
        }

        setBackStackEntryObserver()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    when (it) {
                        is MyTripsUiState.Loading -> {
                            showLoadingState()
                        }
                        is MyTripsUiState.Success -> {
                            showSuccessState()
                            adapter.setData(it.bookedTrips)
                        }
                        is MyTripsUiState.Error -> {
                            showErrorState()
                            Toast.makeText(requireContext(), it.msg, Toast.LENGTH_LONG).show()
                        }
                        is MyTripsUiState.EmptyState -> {
                            showEmptyState()
                        }

                    }
                }
            }
        }

        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->

            if (checkedId == binding.upcoming.id && isChecked) {
                viewModel.fetchUpcomingTrips()
                UPCOMING_TRIP=true
            }

            if (checkedId == binding.past.id && isChecked) {
                viewModel.fetchPastTrips()
                UPCOMING_TRIP=false
            }

            Log.e("TAG", "checkedId:$checkedId ,isChecked:$isChecked")
        }
    }

    private fun setBackStackEntryObserver() {
        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<Boolean>("IS_CANCELLED")
            ?.observe(viewLifecycleOwner) { isCancelled ->
                Log.e("TAG", "setBackStackEntryObserver:$isCancelled " )
                if (isCancelled)
                    viewModel.getReservedTrips()
            }
    }

    private fun showLoadingState() {
        binding.errorState.root.visibility = View.GONE
        binding.emptyState.root.visibility = View.GONE
        binding.loadingState.root.visibility = View.VISIBLE

    }

    private fun showSuccessState() {
        binding.loadingState.root.visibility = View.GONE
        binding.errorState.root.visibility = View.GONE
        binding.emptyState.root.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE

    }

    private fun showErrorState() {
        binding.loadingState.root.visibility = View.GONE
        binding.emptyState.root.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.errorState.root.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        binding.loadingState.root.visibility = View.GONE
        binding.errorState.root.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.emptyState.root.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        UPCOMING_TRIP=true
    }
}