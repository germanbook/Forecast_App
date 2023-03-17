package com.example.forecastapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecastapp.data.db.entity.current.*
import com.example.forecastapp.data.db.entity.sevendays.SevenDaysWeather

@Dao
interface SevenDaysWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateInsert(sevenDaysWeather: SevenDaysWeather)

    @Query("select * from seven_days_weather where units = 'METRIC'")
    fun getSevenDaysWeatherMetric() : LiveData<List<SevenDaysWeather>>

    @Query("select * from seven_days_weather where units = 'IMPERIAL'")
    fun getSevenDaysWeatherImperial() : LiveData<List<SevenDaysWeather>>

    @Query("select count(id) from seven_days_weather where date(time) >= date(:today)")
    fun countSevenDaysWeather(today: String) : Int

    @Query("select (select COUNT(*) from seven_days_weather) != 0")
    fun isSevenDaysWeatherDownloaded(): Boolean

    @Query("select * from seven_days_weather where id = :id")
    fun getSevenDaysItemWeather(id: Int): SevenDaysWeather
}