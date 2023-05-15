package com.wassallni.ui.fragment.main_graph

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
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
import com.wassallni.adapter.ItemDecorator
import com.wassallni.adapter.TripsAdapter
import com.wassallni.data.model.uiState.MainUiState
import com.wassallni.databinding.FragmentMainBinding
import com.wassallni.databinding.NavViewHeaderBinding
import com.wassallni.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A fragment representing a list of Items.
 */
private const val TAG = "MainFragment"

@AndroidEntryPoint
class MainFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: FragmentMainBinding
    private
    lateinit var adapter: TripsAdapter
    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainBinding.inflate(layoutInflater)
        adapter = TripsAdapter()
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

        val pixelsSize = resources.getDimensionPixelSize(R.dimen._16sdp)
        binding.recyclerView.addItemDecoration(ItemDecorator(pixelsSize))

        val headerBinding = NavViewHeaderBinding.inflate(layoutInflater)
        binding.navView.addHeaderView(headerBinding.root)
        binding.navView.itemIconTintList = null
        val name = sharedPreferences.getString("name", "")
        headerBinding.userName.text = name

        mainViewModel.getTrips()
        Log.e(TAG, "onViewCreated: again")
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
                            Log.e(TAG, "error:${state.errorMsg} ")
                        }
                        is MainUiState.Empty -> {
                            showEmptyState()
                        }
                    }
                }
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.e(TAG, "onNavigationItemSelected: ")
        when (item.itemId) {
            R.id.nav_myTrips -> {
                findNavController().navigate(R.id.action_mainFragment_to_myTripsFragment)
            }
            R.id.nav_balance -> {
                Toast.makeText(requireActivity(), R.string.balance, Toast.LENGTH_LONG).show()
            }
            R.id.nav_complain -> {
                findNavController().navigate(R.id.action_mainFragment_to_supportFragment)
            }
            R.id.nav_logout -> {
                signOut()
            }
        }

        binding.drawerLayout.close()
        return true
    }

    private fun signOut() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.putString("token", "")
        editor.putString("name", "")
        editor.apply()
        requireActivity().setResult(Activity.RESULT_OK)
        requireActivity().finish()
    }

    private fun showLoadingState() {
        binding.errorState.root.visibility = View.GONE
        binding.emptyState.root.visibility = View.GONE
        binding.loadingState.visibility = View.VISIBLE

    }

    private fun showSuccessState() {
        binding.loadingState.visibility = View.GONE
        binding.errorState.root.visibility = View.GONE
        binding.emptyState.root.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE

    }

    private fun showErrorState() {
        binding.loadingState.visibility = View.GONE
        binding.emptyState.root.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.errorState.root.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        binding.loadingState.visibility = View.GONE
        binding.errorState.root.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.emptyState.root.visibility = View.VISIBLE
    }
}
