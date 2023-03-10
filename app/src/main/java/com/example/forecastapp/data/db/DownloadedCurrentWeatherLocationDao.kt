package com.example.forecastapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecastapp.data.db.entity.current.*

@Dao
interface DownloadedCurrentWeatherLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateDeviceLastLocation(deviceLastLocation: DownloadedCurrentWeatherLocation)

    @Query("select * from downloaded_current_weather_location where id = $DEVICE_LAST_LOCATION_ID")
    fun getDownloadedCurrentWeatherLocation(): LiveData<DownloadedCurrentWeatherLocation>

}