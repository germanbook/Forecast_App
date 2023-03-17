package com.example.forecastapp.data.network

import androidx.lifecycle.LiveData
import com.example.forecastapp.data.network.response.CurrentWeatherResponse
import com.example.forecastapp.data.network.response.SevenDaysWeatherResponse

interface WeatherNetworkDataSource {
    val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
    val downloadedSevenDaysWeather: LiveData<SevenDaysWeatherResponse>

    suspend fun fetchCurrentWeather(
        latitude: Double,
        longitude: Double,
        startDate: String,
        endDate: String,
        temperatureUnit: String,
        windSpeedUnit: String,
        precipitationUnit: String,
    )

    suspend fun fetchSevenDaysWeather(
        latitude: Double,
        longitude: Double,
        temperatureUnit: String,
        windSpeedUnit: String,
        precipitationUnit: String,
    )
}