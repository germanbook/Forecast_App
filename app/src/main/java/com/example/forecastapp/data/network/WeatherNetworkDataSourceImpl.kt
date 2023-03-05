package com.example.forecastapp.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.forecastapp.data.network.response.CurrentWeatherResponse
import com.example.forecastapp.internal.NoConnectivityException

class WeatherNetworkDataSourceImpl(
    private val weatherApiService: WeatherApiService
) : WeatherNetworkDataSource {

    private val _downloadedCurrentWeather = MutableLiveData<CurrentWeatherResponse>()
    override val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
        get() = _downloadedCurrentWeather

    override suspend fun fetchCurrentWeather(
        latitude: Double,
        longitude: Double,
        startDate: String,
        endDate: String,
        temperatureUnit: String,
        windSpeedUnit: String,
        precipitationUnit: String,

    ) {
        try {
            val fetchedCurrentWeather = weatherApiService.getCurrentWeather(
                latitude,
                longitude,
                startDate,
                endDate,
                temperatureUnit,
                windSpeedUnit,
                precipitationUnit,
            ).await()
            _downloadedCurrentWeather.postValue(fetchedCurrentWeather)
        }
        catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection.", e)
        }
    }
}