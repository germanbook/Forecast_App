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

    override suspend fun getCurrentWeather(metric: Boolean): LiveData<out CurrentWeather> {
        return withContext(Dispatchers.IO) {
            initWeatherData(metric)
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

    private suspend fun initWeatherData(metric: Boolean) {
        if(isFetchCurrentNeeded(ZonedDateTime.now().minusHours(1))) {
//            if(metric)
//                fetchCurrentWeatherMetric()
//            else
//                fetchCurrentWeatherImperial()
            fetchCurrentWeatherMetric()
            fetchCurrentWeatherImperial()
        }
    }

    private suspend fun fetchCurrentWeatherMetric() {

        weatherNetworkDataSource.fetchCurrentWeather(
            -41.29,
            174.77,
            "2023-03-05",
            "2023-03-05",
            "celsius",
            "kmh",
            "mm",
        )

    }

    private suspend fun fetchCurrentWeatherImperial() {

        weatherNetworkDataSource.fetchCurrentWeather(
            -41.29,
            174.77,
            "2023-03-04",
            "2023-03-04",
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