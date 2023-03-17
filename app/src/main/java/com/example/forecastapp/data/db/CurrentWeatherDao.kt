package com.example.forecastapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecastapp.data.db.entity.current.*

@Dao
interface CurrentWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateInsert(currentWeather: CurrentWeather)

    @Query("select * from current_weather where id = $METRIC_ID")
    fun getWeatherMetric(): LiveData<CurrentWeather>

    @Query("select * from current_weather where id = $IMPERIAL_ID")
    fun getWeatherImperial(): LiveData<CurrentWeather>

    @Query("select (select COUNT(*) from current_weather) != 0")
    fun isCurrentWeatherDownloaded(): Boolean

}