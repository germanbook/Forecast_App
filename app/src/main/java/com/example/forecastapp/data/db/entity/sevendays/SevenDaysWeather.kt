package com.example.forecastapp.data.db.entity.sevendays

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "seven_days_weather")
data class SevenDaysWeather(
    val time: String?,
    val temperature2mMax: Double?,
    val temperature2mMin: Double?,
    val weatherCode: Int?,
    val sunRise: String?,
    val sunSet: String?,
    val uvIndexMax: Double?,
    val precipitationSum: Double?,
    val windSpeed: Double?,
    val windDirection: Double?,
    var units: String,
    @PrimaryKey(autoGenerate = false)
    var id: Int
)