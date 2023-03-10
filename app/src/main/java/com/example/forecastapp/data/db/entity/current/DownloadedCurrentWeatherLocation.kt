package com.example.forecastapp.data.db.entity.current


import androidx.room.Entity
import androidx.room.PrimaryKey

const val DEVICE_LAST_LOCATION_ID = 0

@Entity(tableName = "downloaded_current_weather_location")
data class DownloadedCurrentWeatherLocation(
    var addressString: String,
    var latitude: Double,
    var longitude: Double,
    @PrimaryKey(autoGenerate = false)
    var id: Int = DEVICE_LAST_LOCATION_ID,
) {

}