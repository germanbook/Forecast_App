package com.example.forecastapp.data.db.entity.current

import com.google.gson.annotations.SerializedName

data class Daily(
    val time: List<String>,
    @SerializedName("temperature_2m_max")
    val temperature2mMax: List<Double>,
    @SerializedName("temperature_2m_min")
    val temperature2mMin: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    @SerializedName("uv_index_max")
    val uvIndexMax: List<Double>,
    @SerializedName("precipitation_sum")
    val precipitationSum: List<Double>
)