package com.example.forecastapp.ui.weather.current

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.forecastapp.data.db.entity.current.CurrentWeather
import com.example.forecastapp.data.provider.UnitProvider
import com.example.forecastapp.data.repository.ForecastRepository
import com.example.forecastapp.internal.UnitSystem
import com.example.forecastapp.internal.lazyDeferred
import kotlinx.coroutines.Deferred

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider,
) : ViewModel() {

    private val unitSystem = unitProvider

    val isMetric: Boolean
        get() = unitSystem.getUnitSystem() == UnitSystem.METRIC

    val weather by lazyDeferred {
        forecastRepository.getCurrentWeather(isMetric)
    }

    suspend fun updateWeather(): LiveData<out CurrentWeather> {
        return forecastRepository.getCurrentWeather(isMetric)
    }
}