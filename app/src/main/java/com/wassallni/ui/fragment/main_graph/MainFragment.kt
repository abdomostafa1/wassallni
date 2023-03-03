package com.wassallni.ui.fragment.main_graph

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.wassallni.R
import com.wassallni.adapter.TripRecyclerViewAdapter
import com.wassallni.data.datasource.MainUiState
import com.wassallni.databinding.FragmentMainBinding
import com.wassallni.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class MainFragment : Fragment() {

    lateinit var binding:FragmentMainBinding
    lateinit var adapter: TripRecyclerViewAdapter
    private val mainViewModel :MainViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentMainBinding.inflate(layoutInflater)
        adapter=TripRecyclerViewAdapter()
        binding.recyclerView.adapter=adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            mainViewModel.getTrips()
        }

        lifecycleScope.launchWhenStarted {
            mainViewModel.state.collect{ state ->
                when (state){
                    is MainUiState.Loading -> {

                    }
                    is MainUiState.Success -> {
                        val trips=state.trips
                        adapter.setData(trips)
                    }
                    is MainUiState.Error -> {
                        Toast.makeText(requireContext(),state.errorMsg,Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }
}