package com.example.forecastapp.data.db.entity.sevendays


import com.google.gson.annotations.SerializedName

data class Daily(
    val time: List<String>,
    @SerializedName("temperature_2m_max")
    val temperature2mMax: List<Double>,
    @SerializedName("temperature_2m_min")
    val temperature2mMin: List<Double>,
    val weathercode: List<Int>
)