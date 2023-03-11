package com.wassallni.ui.fragment.main_graph

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.wassallni.R
import com.wassallni.databinding.FragmentTripBinding
import com.wassallni.ui.viewmodel.ReservationVM

class TripFragment : Fragment() {

    lateinit var binding:FragmentTripBinding
   val viewModel: ReservationVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentTripBinding.inflate(inflater)
        return binding.root
    }

}