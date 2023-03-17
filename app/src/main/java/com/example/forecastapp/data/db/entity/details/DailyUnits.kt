package com.example.forecastapp.data.db.entity.details

import com.google.gson.annotations.SerializedName

data class DailyUnits(
    val time: String,
    @SerializedName("temperature_2m_max")
    val temperature2mMax: String,
    @SerializedName("temperature_2m_min")
    val temperature2mMin: String,
    val sunrise: String,
    val sunset: String,
    @SerializedName("uv_index_max")
    val uvIndexMax: String,
    @SerializedName("precipitation_sum")
    val precipitationSum: String,
    @SerializedName("windspeed_10m_max")
    val windspeed10mMax: String,
    @SerializedName("winddirection_10m_dominant")
    val winddirection10mDominant: String,
    val weathercode: String
)