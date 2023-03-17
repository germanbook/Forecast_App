package com.example.forecastapp.ui.weather.future.list.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.forecastapp.data.db.entity.sevendays.SevenDaysWeather
import com.example.forecastapp.databinding.ItemSevendaysListBinding
import com.example.forecastapp.data.viewdata.sevendaysweather.SevenDaysWeatherListItemViewData

class FutureListViewHolder(
    private val binding: ItemSevendaysListBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(sevenDaysWeatherListItem: SevenDaysWeatherListItemViewData) {
        binding.sevenDaysWeatherListItem = sevenDaysWeatherListItem
    }
}