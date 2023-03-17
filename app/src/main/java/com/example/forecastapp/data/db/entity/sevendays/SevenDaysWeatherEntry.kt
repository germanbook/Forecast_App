package com.example.forecastapp.data.db.entity.sevendays


import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class SevenDaysWeatherEntry(
    val time: List<String>,
    @SerializedName("temperature_2m_max")
    val temperature2mMax: List<Double>,
    @SerializedName("temperature_2m_min")
    val temperature2mMin: List<Double>,
    @SerializedName("weathercode")
    val weatherCode: List<Int>,
    @SerializedName("sunrise")
    val sunRise: List<String>,
    @SerializedName("sunset")
    val sunSet: List<String>,
    @SerializedName("uv_index_max")
    val uvIndexMax: List<Double>,
    @SerializedName("precipitation_sum")
    val precipitationSum: List<Double>,
    @SerializedName("windspeed_10m_max")
    val windSpeed: List<Double>,
    @SerializedName("winddirection_10m_dominant")
    val windDirection: List<Double>,
)