package com.example.forecastapp.ui.base

import android.app.Application
import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.forecastapp.R
import com.example.forecastapp.data.db.entity.current.DownloadedCurrentWeatherLocation
import com.example.forecastapp.data.provider.LocationProvider
import com.example.forecastapp.data.provider.UnitProvider
import com.example.forecastapp.data.repository.ForecastRepository
import com.example.forecastapp.internal.UnitSystem
import kotlinx.coroutines.Deferred

abstract class WeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider,
    locationProvider: LocationProvider,
    private val context: Context,
) : ViewModel() {

    private val unitSystem = unitProvider
    private val locationSystem = locationProvider

    val isMetric: Boolean
        get() = unitSystem.getUnitSystem() == UnitSystem.METRIC

    val isUseDeviceLocation: Boolean
        get() = locationSystem.isUseDeviceLocation()

    val deviceLocation: Deferred<Location?>
        get() = locationSystem.getDeviceLocation()

    suspend fun getCustomLocationCoordinates() : List<Double>? {
        return locationSystem.getCustomLocationCoordinates()
    }

    fun getDownloadedCurrentWeatherLocation(): LiveData<DownloadedCurrentWeatherLocation> {
        return forecastRepository.getDownloadedCurrentWeatherLocation()
    }

    fun isOnline(): Boolean {
        val connectivityManager =context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}