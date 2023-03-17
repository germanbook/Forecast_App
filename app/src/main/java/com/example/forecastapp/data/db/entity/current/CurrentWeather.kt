package com.example.forecastapp.data.db.entity.current

import androidx.room.Entity
import androidx.room.PrimaryKey

const val METRIC_ID = 0
const val IMPERIAL_ID = 1

@Entity(tableName = "current_weather")
data class CurrentWeather(
    var temperature: Double?,
    val windspeed: Double?,
    val winddirection: Double?,
    val weathercode: Int?,
    val time: String?,
    val temperature2mMax: Double?,
    val temperature2mMin: Double?,
    val sunrise: String?,
    val sunset: String?,
    val uvIndexMax: Double?,
    @PrimaryKey(autoGenerate = false)
    var id: Int,
) {

}