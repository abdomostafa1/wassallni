package com.wassallni.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wassallni.data.model.Trip
import com.wassallni.databinding.TripItemRowBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class TripRecyclerViewAdapter: RecyclerView.Adapter<TripRecyclerViewAdapter.ViewHolder>() {

    private var trips=ArrayList<Trip>()
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

        holder.binding.from.text=trip.from
        holder.binding.to.text=trip.to
        holder.binding.firstTime.text=trip.firstTime
        holder.binding.finalTime.text=trip.finalTime
    }

    override fun getItemCount(): Int = trips.size

    fun setData(newTrips:ArrayList<Trip>){
        trips.clear()
        trips=newTrips
        notifyDataSetChanged()
    }
    inner class ViewHolder(binding: TripItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding=binding
    }

}