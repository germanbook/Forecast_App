package com.example.forecastapp.data.db.entity.current


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

const val CURRENT_WEATHER_METRIC_ID = 0
const val CURRENT_WEATHER_IMPERIAL_ID = 1

@Entity(tableName = "current_weather")
data class CurrentWeather(
    var temperature: Double,
    val windspeed: Double,
    val winddirection: Double,
    val weathercode: Int,
    val time: String,
    val temperature2mMax: Double,
    val temperature2mMin: Double,
    val sunrise: String,
    val sunset: String,
    val uvIndexMax: Double,
    @PrimaryKey(autoGenerate = false)
    var id: Int,
) {

}