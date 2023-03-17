package com.example.forecastapp.data.repository

import androidx.lifecycle.LiveData
import com.example.forecastapp.data.db.entity.current.CurrentWeather
import com.example.forecastapp.data.db.entity.current.DownloadedCurrentWeatherLocation
import com.example.forecastapp.data.db.entity.sevendays.SevenDaysWeather

interface ForecastRepository {
    suspend fun getCurrentWeather(metric: Boolean, latitude: Double, longitude: Double) : LiveData<out CurrentWeather>
    fun getDownloadedCurrentWeatherLocation(): LiveData<DownloadedCurrentWeatherLocation>
    fun isCurrentWeatherDownloaded(): Boolean
    fun isSevenDaysWeatherDownloaded(): Boolean
    suspend fun getSevenDaysWeatherList(metric: Boolean, latitude: Double, longitude: Double): LiveData<out List<SevenDaysWeather>>

    fun getSevenDaysItemWeather(int: Int): SevenDaysWeather

}