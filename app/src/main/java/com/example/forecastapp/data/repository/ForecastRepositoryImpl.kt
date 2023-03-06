package com.example.forecastapp.data.repository

import androidx.lifecycle.LiveData
import com.example.forecastapp.data.db.CurrentWeatherDao
import com.example.forecastapp.data.db.entity.current.CURRENT_WEATHER_IMPERIAL_ID
import com.example.forecastapp.data.db.entity.current.CURRENT_WEATHER_METRIC_ID
import com.example.forecastapp.data.db.entity.current.CurrentWeather
import com.example.forecastapp.data.network.WeatherNetworkDataSource
import com.example.forecastapp.data.network.response.CurrentWeatherResponse
import kotlinx.coroutines.*
import org.threeten.bp.ZonedDateTime
import kotlin.properties.Delegates

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
) : ForecastRepository {

    init {
        weatherNetworkDataSource.downloadedCurrentWeather.observeForever { newCurrentWeather ->
            persistFetchedCurrentWeather(newCurrentWeather)
        }
    }

    override suspend fun getCurrentWeather(metric: Boolean, latitude: Double, longitude: Double):
            LiveData<out CurrentWeather> {
        return withContext(Dispatchers.IO) {
            initWeatherData(metric, latitude, longitude)
            return@withContext if(metric) currentWeatherDao.getWeatherMetric() else
                currentWeatherDao.getWeatherImperial()
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {

            if(fetchedWeather.dailyUnits.precipitationSum == "mm") {
                fetchedWeather.currentWeather.id = CURRENT_WEATHER_METRIC_ID
            } else fetchedWeather.currentWeather.id = CURRENT_WEATHER_IMPERIAL_ID

            currentWeatherDao.updateInsert(fetchedWeather.currentWeather)
        }
    }

    private suspend fun initWeatherData(metric: Boolean, latitude: Double, longitude: Double) {
        if(isFetchCurrentNeeded(ZonedDateTime.now().minusHours(1))) {
            fetchCurrentWeather(latitude, longitude)
        }
    }

    private suspend fun fetchCurrentWeather(latitude: Double, longitude: Double) {

        // location coordinate

        weatherNetworkDataSource.fetchCurrentWeather(
            latitude,
            longitude,
            "2023-03-06",
            "2023-03-06",
            "celsius",
            "kmh",
            "mm",
        )

        weatherNetworkDataSource.fetchCurrentWeather(
            latitude,
            longitude,
            "2023-03-06",
            "2023-03-06",
            "fahrenheit",
            "mph",
            "inch",
        )

    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }

}