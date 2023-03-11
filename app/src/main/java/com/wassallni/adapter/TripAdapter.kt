package com.wassallni.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.wassallni.data.model.Trip
import com.wassallni.databinding.TripItemRowBinding
import com.wassallni.utils.DateUseCase

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class TripAdapter : RecyclerView.Adapter<TripAdapter.ViewHolder>() {

    private var trips= listOf<Trip>()
    private val date=DateUseCase()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            TripItemRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trip = trips[position]

        holder.binding.start.text=trip.start
        holder.binding.destination.text=trip.destination
        holder.binding.price.text=trip.price.toString()

        holder.binding.startTime.text=date.fromMillisToString(trip.startTime)
        holder.binding.endTime.text=date.fromMillisToString(trip.endTime)
        holder.binding.cardView.setOnClickListener {
            //val action=Trip
            //it.findNavController().navigate()
        }

    }

    override fun getItemCount(): Int = trips.size

    fun setData(newTrips:List<Trip>){
        trips=newTrips
        notifyDataSetChanged()
    }
    inner class ViewHolder(binding: TripItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding=binding
    }

}