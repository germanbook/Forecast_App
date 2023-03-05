package com.example.forecastapp.data.network

import androidx.lifecycle.LiveData
import com.example.forecastapp.data.network.response.CurrentWeatherResponse

interface WeatherNetworkDataSource {
    val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>

    suspend fun fetchCurrentWeather(
        latitude: Double,
        longitude: Double,
        startDate: String,
        endDate: String,
        temperatureUnit: String,
        windSpeedUnit: String,
        precipitationUnit: String,
    )
}