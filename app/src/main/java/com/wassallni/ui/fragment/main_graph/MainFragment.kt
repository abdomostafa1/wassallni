package com.wassallni.ui.fragment.main_graph

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.wassallni.R
import com.wassallni.adapter.TripAdapter
import com.wassallni.data.model.uiState.MainUiState
import com.wassallni.databinding.FragmentMainBinding
import com.wassallni.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class MainFragment : Fragment() , NavigationView.OnNavigationItemSelectedListener {
    private val TAG = "MainFragment"
    lateinit var binding: FragmentMainBinding
    lateinit var adapter: TripAdapter
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMainBinding.inflate(layoutInflater)
        adapter = TripAdapter()
        binding.recyclerView.adapter = adapter

        return binding.root
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navView.setNavigationItemSelectedListener(this)
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }

        mainViewModel.getTrips()
        Log.e(TAG, "onViewCreated: again", )
        binding.errorState.retry.setOnClickListener {
            mainViewModel.getTrips()
        }
        // binding.recycler
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.state.collect { state ->
                    when (state) {
                        is MainUiState.Loading -> {
                            showLoadingState()
                        }
                        is MainUiState.Success -> {
                            showSuccessState()
                            val trips = state.trips
                            adapter.setData(trips)
                        }
                        is MainUiState.Error -> {
                            showErrorState()

                            Toast.makeText(requireContext(), state.errorMsg, Toast.LENGTH_LONG)
                                .show()
                        }
                        is MainUiState.Empty -> {
                            showEmptyState()
                        }
                        else -> Unit
                    }
                }
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.e(TAG, "onNavigationItemSelected: ", )
        when(item.itemId){
            R.id.nav_myTrips ->{
                findNavController().navigate(R.id.action_mainFragment_to_myTripsFragment)
            }
            R.id.nav_balance ->{
                Toast.makeText(requireActivity(),R.string.balance,Toast.LENGTH_LONG).show()
            }
            R.id.nav_settings ->{
                Toast.makeText(requireActivity(),R.string.settings,Toast.LENGTH_LONG).show()
            }
            R.id.nav_complain ->{
                Toast.makeText(requireActivity(),R.string.complain,Toast.LENGTH_LONG).show()
            }
            R.id.nav_logout ->{
                Toast.makeText(requireActivity(),R.string.logout,Toast.LENGTH_LONG).show()
            }
        }

        binding.drawerLayout.close()
        return true
    }

    private fun showLoadingState(){
        binding.errorState.root.visibility=View.GONE
        binding.emptyState.root.visibility=View.GONE
        binding.loadingState.visibility=View.VISIBLE

    }

    private fun showSuccessState(){
        binding.loadingState.visibility=View.GONE
        binding.errorState.root.visibility=View.GONE
        binding.emptyState.root.visibility=View.GONE
        binding.recyclerView.visibility=View.VISIBLE

    }
    private fun showErrorState(){
        binding.loadingState.visibility=View.GONE
        binding.emptyState.root.visibility=View.GONE
        binding.recyclerView.visibility=View.GONE
        binding.errorState.root.visibility=View.VISIBLE
    }

    private fun showEmptyState(){
        binding.loadingState.visibility=View.GONE
        binding.errorState.root.visibility=View.GONE
        binding.recyclerView.visibility=View.GONE
        binding.emptyState.root.visibility=View.VISIBLE
    }
}
