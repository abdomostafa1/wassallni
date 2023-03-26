package com.wassallni.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wassallni.data.model.Station
import com.wassallni.databinding.StationItemRowBinding
import com.wassallni.utils.DateUseCase
import okhttp3.internal.immutableListOf

class StationAdapter : RecyclerView.Adapter<StationAdapter.ViewHolder>() , View.OnClickListener{

    private var stations= immutableListOf<Station>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            StationItemRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val station = stations[position]

        holder.binding.name.text=station.name
        val arrivalTime=DateUseCase.fromMillisToString1(station.time)
        holder.binding.arrivalTime.text=arrivalTime

    }

    override fun getItemCount(): Int = stations.size

    fun setData(newTrips:List<Station>){
        stations=newTrips
        Log.e("TAG", "stations.size:${stations.size} ", )
        notifyDataSetChanged()
    }
    inner class ViewHolder(binding: StationItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding=binding
    }


    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}