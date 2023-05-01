package com.wassallni.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.wassallni.R
import com.wassallni.data.model.ReservedTrip
import com.wassallni.databinding.ReservedTripItemBinding
import com.wassallni.ui.fragment.main_graph.MainFragmentDirections
import com.wassallni.ui.fragment.main_graph.MyTripsFragment.Companion.UPCOMING_TRIP
import com.wassallni.ui.fragment.main_graph.MyTripsFragmentDirections
import com.wassallni.utils.DateUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.internal.immutableListOf
import javax.inject.Inject


class ReservedTripAdapter @Inject constructor(@ApplicationContext val context: Context) :
    RecyclerView.Adapter<ReservedTripAdapter.ViewHolder>() {

    private var trips = emptyList<ReservedTrip>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ReservedTripItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trip = trips[position]

        Log.e("TAG", "adapter trip start $position:${trip.startTime} ")
        Log.e("TAG", "adapter trip end $position:${trip.endTime} ")

        holder.binding.trip.tripView.start.text = trip.start
        holder.binding.trip.tripView.destination.text = trip.destination
        holder.binding.trip.tripView.tvPrice.text = trip.price.toString()


        holder.binding.trip.tripView.date.text = DateUseCase.fromMillisToString3(trip.endTime)
        holder.binding.trip.tripView.startTime.text =
            DateUseCase.fromMillisToString1(trip.startTime)
        holder.binding.trip.tripView.endTime.text = DateUseCase.fromMillisToString1(trip.endTime)

        if (trip.state == -1)
            holder.binding.state.text = context.getString(R.string.cancelled)
        else if (trip.state == -2) {
            holder.binding.state.text = context.getString(R.string.missed)
            holder.binding.state.setTextColor(Color.RED)
        } else
            holder.binding.state.text = ""

        val id = trip.tripId
        holder.binding.trip.cardView.setOnClickListener {

            val action = MyTripsFragmentDirections.actionMyTripsFragmentToBookedTripFragment(
                trip._id, trip.tripId, trip.point, trip.numOfSeat, UPCOMING_TRIP
            )
            it.findNavController().navigate(action)
        }

    }

    override fun getItemCount(): Int = trips.size


    fun setData(newTrips: List<ReservedTrip>) {

        trips = newTrips
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: ReservedTripItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val binding = binding
    }

}