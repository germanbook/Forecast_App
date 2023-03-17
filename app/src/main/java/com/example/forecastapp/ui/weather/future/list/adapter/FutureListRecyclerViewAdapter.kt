package com.example.forecastapp.ui.weather.future.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.forecastapp.data.viewdata.sevendaysweather.SevenDaysWeatherListItemViewData
import com.example.forecastapp.databinding.ItemSevendaysListBinding

class FutureListRecyclerViewAdapter(
    private val sevenDaysWeatherListItem: List<SevenDaysWeatherListItemViewData>,
) : RecyclerView.Adapter<FutureListViewHolder>() {

    private lateinit var binding: ItemSevendaysListBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FutureListViewHolder {
        binding = ItemSevendaysListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FutureListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return sevenDaysWeatherListItem.size
    }

    override fun onBindViewHolder(holder: FutureListViewHolder, position: Int) {
        holder.bind(sevenDaysWeatherListItem.get(position))
    }

}