package com.example.forecastapp.ui.weather.future.list.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.forecastapp.data.viewdata.sevendaysweather.SevenDaysWeatherListItemViewData
import com.example.forecastapp.databinding.ItemSevendaysListBinding

class FutureListViewHolder(
    private val binding: ItemSevendaysListBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(sevenDaysWeatherListItem: SevenDaysWeatherListItemViewData) {
        binding.sevenDaysWeatherListItem = sevenDaysWeatherListItem
    }
}