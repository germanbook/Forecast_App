package com.example.forecastapp.data.db.entity.sevendays


import com.google.gson.annotations.SerializedName

data class DailyUnits(
    val time: String,
    @SerializedName("temperature_2m_max")
    val temperature2mMax: String,
    @SerializedName("temperature_2m_min")
    val temperature2mMin: String,
    val weathercode: String
)