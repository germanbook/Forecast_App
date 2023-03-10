package com.example.forecastapp.data.repository

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.forecastapp.data.db.CurrentWeatherDao
import com.example.forecastapp.data.db.DownloadedCurrentWeatherLocationDao
import com.example.forecastapp.data.db.entity.current.*
import com.example.forecastapp.data.network.WeatherNetworkDataSource
import com.example.forecastapp.data.network.response.CurrentWeatherResponse
import com.example.forecastapp.data.provider.LocationProvider
import kotlinx.coroutines.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import kotlin.properties.Delegates

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val deviceLastLocationDao: DownloadedCurrentWeatherLocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    locationProvider: LocationProvider,
) : ForecastRepository {

    private val locationSystem = locationProvider

    init {
        weatherNetworkDataSource.downloadedCurrentWeather.observeForever { newCurrentWeather ->
            persistFetchedCurrentWeather(newCurrentWeather)
        }
    }

    override suspend fun getCurrentWeather(metric: Boolean, latitude: Double, longitude: Double):
            LiveData<out CurrentWeather> {
        return withContext(Dispatchers.IO) {
            initWeatherData(latitude, longitude)
            return@withContext if(metric) currentWeatherDao.getWeatherMetric() else
                currentWeatherDao.getWeatherImperial()
        }
    }

    override fun getDownloadedCurrentWeatherLocation(): LiveData<DownloadedCurrentWeatherLocation> {
        return deviceLastLocationDao.getDownloadedCurrentWeatherLocation()
    }

    override fun isCurrentWeatherDownloaded(): Boolean {
        return currentWeatherDao.isCurrentWeatherDownloaded()
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {

            var id: Int = if(fetchedWeather.dailyUnits.precipitationSum == "mm")
                CURRENT_WEATHER_METRIC_ID else CURRENT_WEATHER_IMPERIAL_ID

            currentWeatherDao.updateInsert(CurrentWeather(
                fetchedWeather.currentWeather.temperature,
                fetchedWeather.currentWeather.windspeed,
                fetchedWeather.currentWeather.winddirection,
                fetchedWeather.currentWeather.weathercode,
                fetchedWeather.currentWeather.time,
                fetchedWeather.daily.temperature2mMax[0],
                fetchedWeather.daily.temperature2mMin[0],
                fetchedWeather.daily.sunrise[0],
                fetchedWeather.daily.sunset[0],
                fetchedWeather.daily.uvIndexMax[0],
                id,
            ))

            deviceLastLocationDao.updateDeviceLastLocation(DownloadedCurrentWeatherLocation(
                locationSystem.getLocationString(fetchedWeather.latitude, fetchedWeather.longitude)!!,
                fetchedWeather.latitude,
                fetchedWeather.longitude,
            ))
        }
    }

    private suspend fun initWeatherData(latitude: Double, longitude: Double) {
        if(isFetchCurrentNeeded(ZonedDateTime.now().minusHours(1))) {
            fetchCurrentWeather(latitude, longitude)
        }
    }

    private suspend fun fetchCurrentWeather(latitude: Double, longitude: Double) {

        var today = getCurrentDate()

        weatherNetworkDataSource.fetchCurrentWeather(
            latitude,
            longitude,
            today,
            today,
            "celsius",
            "kmh",
            "mm",
        )

        weatherNetworkDataSource.fetchCurrentWeather(
            latitude,
            longitude,
            today,
            today,
            "fahrenheit",
            "mph",
            "inch",
        )

    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }

    private fun getCurrentDate(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

}