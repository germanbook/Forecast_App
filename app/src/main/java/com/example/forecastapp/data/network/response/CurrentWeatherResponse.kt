package com.example.forecastapp.data.network.response


import com.example.forecastapp.data.db.entity.current.CurrentWeather
import com.example.forecastapp.data.db.entity.current.Daily
import com.example.forecastapp.data.db.entity.current.DailyUnits
import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(
    val latitude: Double,
    val longitude: Double,
    @SerializedName("generationtime_ms")
    val generationtimeMs: Double,
    @SerializedName("utc_offset_seconds")
    val utcOffsetSeconds: Int,
    val timezone: String,
    @SerializedName("timezone_abbreviation")
    val timezoneAbbreviation: String,
    val elevation: Double,
    @SerializedName("current_weather")
    val currentWeather: CurrentWeather,
    @SerializedName("daily_units")
    val dailyUnits: DailyUnits,
    val daily: Daily
)