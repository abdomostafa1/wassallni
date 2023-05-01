package com.wassallni.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wassallni.ui.fragment.main_graph.MainFragmentDirections
import androidx.navigation.findNavController
import com.wassallni.data.model.Trip
import com.wassallni.databinding.TripItemRowBinding
import com.wassallni.utils.DateUseCase
import okhttp3.internal.immutableListOf

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class TripsAdapter : RecyclerView.Adapter<TripsAdapter.ViewHolder>() , View.OnClickListener{

    private var trips= immutableListOf<Trip>()
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

        holder.binding.tripView.start.text=trip.start
        holder.binding.tripView.destination.text=trip.destination
        holder.binding.tripView.tvPrice.text=trip.price.toString()

        holder.binding.tripView.date.text=DateUseCase.fromMillisToString3(trip.startTime)
        holder.binding.tripView.startTime.text=DateUseCase.fromMillisToString1(trip.startTime)
        holder.binding.tripView.endTime.text=DateUseCase.fromMillisToString1(trip.endTime)
        val id =trip.id
        holder.itemView.setOnClickListener {

            val action=MainFragmentDirections.actionMainToTripGraph(id)
            it.findNavController().navigate(action)
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


    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}