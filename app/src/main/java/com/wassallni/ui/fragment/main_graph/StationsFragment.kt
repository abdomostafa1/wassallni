package com.wassallni.ui.fragment.main_graph

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.wassallni.R
import com.wassallni.adapter.StationAdapter
import com.wassallni.adapter.TripAdapter
import com.wassallni.databinding.FragmentMainBinding
import com.wassallni.databinding.FragmentStationsBinding
import com.wassallni.ui.viewmodel.MainViewModel
import com.wassallni.ui.viewmodel.ReservationVM
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StationsFragment : Fragment() {

    lateinit var binding: FragmentStationsBinding
    lateinit var adapter: StationAdapter
    private val viewModel: ReservationVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStationsBinding.inflate(inflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = StationAdapter()
        binding.recyclerView.adapter = adapter

        binding.topAppBar.setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stations.collect {
                        if (it!=null){
                            Log.e("TAG", "viewModel.stations:${it.toString()} ", )
                            adapter.setData(it)
                        }
                }
            }
        }
    }
}