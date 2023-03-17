package com.example.forecastapp.ui.weather.future.list

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.findNavController
import com.example.forecastapp.R
import com.example.forecastapp.data.db.entity.sevendays.SevenDaysWeather
import com.example.forecastapp.data.provider.LocationProvider
import com.example.forecastapp.data.provider.UnitProvider
import com.example.forecastapp.data.repository.ForecastRepository
import com.example.forecastapp.internal.lazyDeferred
import com.example.forecastapp.ui.base.WeatherViewModel

class FutureListWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider,
    locationProvider: LocationProvider,
    private val context: Context,
) : WeatherViewModel(forecastRepository, unitProvider, locationProvider, context) {

    suspend fun updateSevenDaysWeather(latitude: Double, longitude: Double):
        LiveData<out List<SevenDaysWeather>>{
        return forecastRepository.getSevenDaysWeatherList(super.isMetric, latitude, longitude)
    }
    fun isSevenDaysWeatherDataDownloaded(): Boolean {
        return forecastRepository.isSevenDaysWeatherDownloaded()
    }
}