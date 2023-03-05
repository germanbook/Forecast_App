package com.example.forecastapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecastapp.data.db.entity.current.CURRENT_WEATHER_IMPERIAL_ID
import com.example.forecastapp.data.db.entity.current.CURRENT_WEATHER_METRIC_ID
import com.example.forecastapp.data.db.entity.current.CurrentWeather

@Dao
interface CurrentWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateInsert(currentWeather: CurrentWeather)

    @Query("select * from current_weather where id = $CURRENT_WEATHER_METRIC_ID")
    fun getWeatherMetric(): LiveData<CurrentWeather>


    @Query("select * from current_weather where id = $CURRENT_WEATHER_IMPERIAL_ID")
    fun getWeatherImperial(): LiveData<CurrentWeather>

}