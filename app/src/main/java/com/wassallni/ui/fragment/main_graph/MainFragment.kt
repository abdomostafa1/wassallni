package com.wassallni.ui.fragment.main_graph

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.wassallni.adapter.TripAdapter
import com.wassallni.data.datasource.MainUiState
import com.wassallni.databinding.FragmentMainBinding
import com.wassallni.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class MainFragment : Fragment() {

    lateinit var binding:FragmentMainBinding
    lateinit var adapter: TripAdapter
    private val mainViewModel :MainViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentMainBinding.inflate(layoutInflater)
        adapter=TripAdapter()
        binding.recyclerView.adapter=adapter

//        val itemMargin=SpacesItemDecoration(spaceSize = R.dimen._16sdp, orientation = LinearLayout.VERTICAL)
//        binding.recyclerView.addItemDecoration(itemMargin)
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
                        binding.recyclerView.visibility=View.GONE
                        binding.shimmerLayout.visibility=View.VISIBLE
                        binding.shimmerLayout.startShimmer()
                    }
                    is MainUiState.Success -> {
                        binding.shimmerLayout.visibility=View.GONE
                        binding.recyclerView.visibility=View.VISIBLE
                        val trips=state.trips
                        adapter.setData(trips)
                    }
                    is MainUiState.Error -> {
                        binding.recyclerView.visibility=View.GONE
                        binding.shimmerLayout.visibility=View.GONE
                        Toast.makeText(requireContext(),state.errorMsg,Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }
}