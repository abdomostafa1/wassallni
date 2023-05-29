package com.wassallni.ui.fragment.main_graph.booking_graph

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.wassallni.R
import com.wassallni.adapter.StationsAdapter
import com.wassallni.databinding.FragmentStationsBinding
import com.wassallni.ui.viewmodel.BookVM
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
    lateinit var adapter: StationsAdapter
    val viewModel :BookVM by navGraphViewModels(R.id.trip_graph){ defaultViewModelProviderFactory  }

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
        adapter = StationsAdapter()
        binding.recyclerView.adapter = adapter

        val divider = MaterialDividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL /*or LinearLayoutManager.HORIZONTAL*/)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            divider.dividerColor=requireActivity().getColor(R.color.divider_color)
        }
        binding.recyclerView.addItemDecoration(divider)

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