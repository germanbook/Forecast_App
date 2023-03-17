package com.example.forecastapp.ui.weather.current

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.forecastapp.data.db.entity.current.CurrentWeather
import com.example.forecastapp.data.provider.LocationProvider
import com.example.forecastapp.data.provider.UnitProvider
import com.example.forecastapp.data.repository.ForecastRepository
import com.example.forecastapp.ui.base.WeatherViewModel


class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider,
    locationProvider: LocationProvider,
    context: Context,
) : WeatherViewModel(forecastRepository, unitProvider, locationProvider, context) {

    fun isWeatherDataDownloaded(): Boolean {
        return forecastRepository.isCurrentWeatherDownloaded()
    }

    suspend fun updateWeather(latitude: Double, longitude: Double):
            LiveData<out CurrentWeather> {
        return forecastRepository.getCurrentWeather(super.isMetric, latitude, longitude)
    }
}