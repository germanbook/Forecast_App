package com.example.forecastapp.data.network.response


import com.example.forecastapp.data.db.entity.sevendays.Daily
import com.example.forecastapp.data.db.entity.sevendays.DailyUnits
import com.google.gson.annotations.SerializedName

data class SevenDaysWeatherResponse(
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
    @SerializedName("daily_units")
    val dailyUnits: DailyUnits,
    val daily: Daily
)