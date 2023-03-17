package com.example.forecastapp.ui.weather.future.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forecastapp.data.provider.LocationProvider
import com.example.forecastapp.data.provider.UnitProvider
import com.example.forecastapp.data.repository.ForecastRepository
import com.example.forecastapp.ui.weather.future.list.FutureListWeatherViewModel

class FutureDetailViewModelFactory(
    private val forecastRepository: ForecastRepository,
    private val unitProvider: UnitProvider,
    private val locationProvider: LocationProvider,
    private val context: Context,
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FutureDetailViewModel(forecastRepository, unitProvider, locationProvider, context) as T
    }
}