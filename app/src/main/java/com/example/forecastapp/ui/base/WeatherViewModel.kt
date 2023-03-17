package com.example.forecastapp.ui.base

import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
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

    fun getConditionIcon(conditionCode: Int): Int {
        var weatherResource = 0
        when(conditionCode) {
            0, 1 -> weatherResource = R.drawable.weather_sun
            2 -> weatherResource = R.drawable.weather_news
            3 -> weatherResource = R.drawable.weather_cloud
            4, 5, 10, 11, 20, in 30..35 ->
                weatherResource = R.drawable.weather_fog
            12 -> weatherResource = R.drawable.weather_storm_01
            18, 26 -> weatherResource = R.drawable.weather_storm
            21 -> weatherResource = R.drawable.weather_rainy_day
            22 -> weatherResource = R.drawable.weather_raining
            23, in 40..68 -> weatherResource = R.drawable.weather_rain
            24, 25, in 70..79, in 80..87 ->
                weatherResource = R.drawable.weather_snow
            27, 28, 29 -> weatherResource = R.drawable.weather_wind_01
            89 -> weatherResource = R.drawable.weather_hail
            in 90..96 -> weatherResource = R.drawable.weather_storm_02
            99 -> weatherResource = R.drawable.weather_sandstorm
        }
        return weatherResource
    }
}