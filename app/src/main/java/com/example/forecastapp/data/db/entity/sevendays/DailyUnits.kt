package com.example.forecastapp.data.db.entity.sevendays


import com.google.gson.annotations.SerializedName

data class DailyUnits(
    val time: String,
    @SerializedName("temperature_2m_max")
    val temperature2mMax: String,
    @SerializedName("temperature_2m_min")
    val temperature2mMin: String,
    val weathercode: String,
    val sunrise: String,
    val sunset: String,
    @SerializedName("uv_index_max")
    val uvIndexMax: String,
    @SerializedName("precipitation_sum")
    val precipitationSum: String
)