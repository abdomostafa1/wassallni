package com.wassallni.ui.fragment.main_graph

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wassallni.adapter.TripRecyclerViewAdapter
import com.wassallni.databinding.FragmentMainBinding

/**
 * A fragment representing a list of Items.
 */
class MainFragment : Fragment() {

    lateinit var binding:FragmentMainBinding
    lateinit var adapter: TripRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentMainBinding.inflate(layoutInflater)

        adapter=TripRecyclerViewAdapter()
        binding.list.adapter=adapter

        return binding.root
    }


}