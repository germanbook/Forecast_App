package com.example.forecastapp.ui.weather.future.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.forecastapp.data.db.entity.sevendays.SevenDaysWeather
import com.example.forecastapp.data.provider.LocationProvider
import com.example.forecastapp.data.provider.UnitProvider
import com.example.forecastapp.data.repository.ForecastRepository
import com.example.forecastapp.data.viewdata.sevendaysweather.SevenDaysWeatherListItemViewData
import com.example.forecastapp.ui.base.WeatherViewModel

class FutureDetailViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider,
    locationProvider: LocationProvider,
    private val context: Context,
) : WeatherViewModel(forecastRepository, unitProvider, locationProvider, context) {
    fun getSevenDaysItemWeather(id: Int): SevenDaysWeather {
        return forecastRepository.getSevenDaysItemWeather(id)
    }
}