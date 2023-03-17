package com.example.forecastapp.data.network.response


import com.example.forecastapp.data.db.entity.sevendays.DailyUnits
import com.example.forecastapp.data.db.entity.sevendays.SevenDaysWeatherEntry
import com.google.gson.annotations.SerializedName

data class SevenDaysWeatherResponse(
    override val latitude: Double,
    override val longitude: Double,
    @SerializedName("generationtime_ms")
    val generationtimeMs: Double,
    @SerializedName("utc_offset_seconds")
    val utcOffsetSeconds: Int,
    val timezone: String,
    @SerializedName("timezone_abbreviation")
    val timezoneAbbreviation: String,
    val elevation: Double,
    @SerializedName("daily_units")
    val dailyUnits: DailyUnits,
    @SerializedName("daily")
    val sevenDaysWeatherEntry: SevenDaysWeatherEntry
) : WeatherResponse