package com.wassallni.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.wassallni.R
import com.wassallni.data.model.BookedTrip
import com.wassallni.databinding.ReservedTripItemBinding
import com.wassallni.ui.fragment.main_graph.PassengerTripsFragment.Companion.UPCOMING_TRIP
import com.wassallni.ui.fragment.main_graph.PassengerTripsFragmentDirections
import com.wassallni.utils.DateUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class BookedTripsAdapter @Inject constructor(@ApplicationContext val context: Context) :
    RecyclerView.Adapter<BookedTripsAdapter.ViewHolder>() {

    private var trips = emptyList<BookedTrip>()
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


        holder.binding.trip.tripView.date.text = DateUseCase.convertDateToYyMmDd(trip.endTime)
        holder.binding.trip.tripView.startTime.text =
            DateUseCase.convertDateToHhMma(trip.startTime)
        holder.binding.trip.tripView.endTime.text = DateUseCase.convertDateToHhMma(trip.endTime)

        when (trip.state) {
            -1 -> holder.binding.state.text = context.getString(R.string.cancelled)
            -2 -> {
                holder.binding.state.text = context.getString(R.string.missed)
                holder.binding.state.setTextColor(Color.RED)
            }

            else -> holder.binding.state.text = ""
        }

        holder.binding.trip.cardView.setOnClickListener {

            val action = PassengerTripsFragmentDirections.actionMyTripsFragmentToBookedTripFragment(
                trip._id, trip.tripId, trip.point, trip.ticket, trip.numOfSeat, UPCOMING_TRIP
            )
            it.findNavController().navigate(action)
        }

    }

    override fun getItemCount(): Int = trips.size


    fun setData(newTrips: List<BookedTrip>) {

        trips = newTrips
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ReservedTripItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}