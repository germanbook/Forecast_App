package com.example.forecastapp.data.repository

import androidx.lifecycle.LiveData
import com.example.forecastapp.data.db.entity.current.CurrentWeather

interface ForecastRepository {
    suspend fun getCurrentWeather(metric: Boolean) : LiveData<out CurrentWeather>
}