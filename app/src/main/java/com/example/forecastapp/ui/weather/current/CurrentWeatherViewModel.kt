package com.example.forecastapp.ui.weather.current

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.forecastapp.data.db.entity.current.CurrentWeather
import com.example.forecastapp.data.db.entity.current.DownloadedCurrentWeatherLocation
import com.example.forecastapp.data.provider.LocationProvider
import com.example.forecastapp.data.provider.UnitProvider
import com.example.forecastapp.data.repository.ForecastRepository
import com.example.forecastapp.internal.UnitSystem
import kotlinx.coroutines.Deferred


class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider,
    locationProvider: LocationProvider,
) : ViewModel() {

    private val unitSystem = unitProvider
    private val locationSystem = locationProvider

    fun getDownloadedCurrentWeatherLocation(): LiveData<DownloadedCurrentWeatherLocation> {
        return forecastRepository.getDownloadedCurrentWeatherLocation()
    }
    fun isWeatherDataDownloaded(): Boolean {
        return forecastRepository.isCurrentWeatherDownloaded()
    }

    val isMetric: Boolean
        get() = unitSystem.getUnitSystem() == UnitSystem.METRIC

    val isUseDeviceLocation: Boolean
        get() = locationSystem.isUseDeviceLocation()

    val deviceLocation: Deferred<Location?>
        get() = locationSystem.getDeviceLocation()

    suspend fun updateWeather(latitude: Double, longitude: Double):
            LiveData<out CurrentWeather> {
        return forecastRepository.getCurrentWeather(isMetric, latitude, longitude)
    }

    suspend fun getCustomLocationCoordinates() : List<Double>? {
        return locationSystem.getCustomLocationCoordinates()
    }

}