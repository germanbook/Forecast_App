package com.example.forecastapp.data.repository

import androidx.lifecycle.LiveData
import com.example.forecastapp.data.db.CurrentWeatherDao
import com.example.forecastapp.data.db.DownloadedCurrentWeatherLocationDao
import com.example.forecastapp.data.db.SevenDaysWeatherDao
import com.example.forecastapp.data.db.entity.current.*
import com.example.forecastapp.data.db.entity.sevendays.SevenDaysWeather
import com.example.forecastapp.data.network.WeatherNetworkDataSource
import com.example.forecastapp.data.network.response.CurrentWeatherResponse
import com.example.forecastapp.data.network.response.SevenDaysWeatherResponse
import com.example.forecastapp.data.network.response.WeatherResponse
import com.example.forecastapp.data.provider.LocationProvider
import kotlinx.coroutines.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

const val METRIC_UNIT_CELSIUS = "celsius"
const val METRIC_UNIT_KMH = "kmh"
const val METRIC_UNIT_MM = "mm"
const val IMPERIAL_UNIT_FAHRENHEIT = "fahrenheit"
const val IMPERIAL_UNIT_MPH = "mph"
const val IMPERIAL_UNIT_INCH = "inch"
const val DATE_FORMAT_IN_SYSTEM = "yyyy-MM-dd"
const val DATE_FORMAT_DISPLAY = "dd-MM-yyyy"
const val THIRTY_MINUTES = 30 as Long
const val FORECAST_DAYS_COUNT = 14
const val METRIC_UNITS_IDENTIFIER = "METRIC"
const val IMPERIAL_UNITS_IDENTIFIER = "IMPERIAL"


class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val sevenDaysWeatherDao: SevenDaysWeatherDao,
    private val deviceLastLocationDao: DownloadedCurrentWeatherLocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    locationProvider: LocationProvider,
) : ForecastRepository {

    private val locationSystem = locationProvider

    init {
        weatherNetworkDataSource.apply {
            downloadedCurrentWeather.observeForever { newCurrentWeather ->
                persistFetchedCurrentWeather(newCurrentWeather)
            }
            downloadedSevenDaysWeather.observeForever { newSevenDaysWeather ->
                persistFetchedSevenDaysWeather(newSevenDaysWeather)
            }
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

    override suspend fun getSevenDaysWeatherList(metric: Boolean, latitude: Double, longitude: Double):
            LiveData<out List<SevenDaysWeather>> {
        return withContext(Dispatchers.IO) {
            initWeatherData(latitude, longitude)
            return@withContext getSevenDaysWeatherList(metric)
        }
    }

    override fun getDownloadedCurrentWeatherLocation(): LiveData<DownloadedCurrentWeatherLocation> {
        return deviceLastLocationDao.getDownloadedCurrentWeatherLocation()
    }

    override fun isCurrentWeatherDownloaded(): Boolean {
        return currentWeatherDao.isCurrentWeatherDownloaded()
    }

    override fun isSevenDaysWeatherDownloaded(): Boolean {
        return sevenDaysWeatherDao.isSevenDaysWeatherDownloaded()
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {

            var id: Int = if(fetchedWeather.dailyUnits.precipitationSum == METRIC_UNIT_MM)
                METRIC_ID else IMPERIAL_ID

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

            deviceLastLocationDao.updateDeviceLastLocation(updateDownloadedCurrentWeatherLocation(fetchedWeather))
        }
    }

    private fun persistFetchedSevenDaysWeather(sevenDaysWeather: SevenDaysWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {

            var unitsIdentifier: String = if(sevenDaysWeather.dailyUnits.precipitationSum == METRIC_UNIT_MM)
                METRIC_UNITS_IDENTIFIER else IMPERIAL_UNITS_IDENTIFIER

            updateSevenDaysWeather(sevenDaysWeather, unitsIdentifier)

            deviceLastLocationDao.updateDeviceLastLocation(updateDownloadedCurrentWeatherLocation(sevenDaysWeather))
        }
    }

    private suspend fun initWeatherData(latitude: Double, longitude: Double) {
        if(isFetchCurrentNeeded(ZonedDateTime.now().minusHours(1))) {
            fetchCurrentWeather(latitude, longitude)
            fetchSevenDaysWeather(latitude, longitude)
        }
    }

    private suspend fun fetchCurrentWeather(latitude: Double, longitude: Double) {

        var today = getCurrentDate()

        weatherNetworkDataSource.fetchCurrentWeather(
            latitude,
            longitude,
            today,
            today,
            METRIC_UNIT_CELSIUS,
            METRIC_UNIT_KMH,
            METRIC_UNIT_MM,
        )

        weatherNetworkDataSource.fetchCurrentWeather(
            latitude,
            longitude,
            today,
            today,
            IMPERIAL_UNIT_FAHRENHEIT,
            IMPERIAL_UNIT_MPH,
            IMPERIAL_UNIT_INCH,
        )

    }

    private suspend fun fetchSevenDaysWeather(latitude: Double, longitude: Double) {

        weatherNetworkDataSource.fetchSevenDaysWeather(
            latitude,
            longitude,
            METRIC_UNIT_CELSIUS,
            METRIC_UNIT_KMH,
            METRIC_UNIT_MM,
        )

        weatherNetworkDataSource.fetchSevenDaysWeather(
            latitude,
            longitude,
            IMPERIAL_UNIT_FAHRENHEIT,
            IMPERIAL_UNIT_MPH,
            IMPERIAL_UNIT_INCH,
        )

    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(THIRTY_MINUTES)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }

    private fun isFetchSevenDaysWeatherNeeded(): Boolean {
        val today = getCurrentDate()
        val sevenDaysWeatherCount = sevenDaysWeatherDao.countSevenDaysWeather(today)
        return sevenDaysWeatherCount < FORECAST_DAYS_COUNT
    }

    private fun getCurrentDate(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_IN_SYSTEM))
    }

    private suspend fun updateDownloadedCurrentWeatherLocation(weather: WeatherResponse): DownloadedCurrentWeatherLocation {
        return DownloadedCurrentWeatherLocation(

            if (locationSystem.isUseDeviceLocation()){
                locationSystem.getLocationString(
                    locationSystem.getDeviceLocation().await()!!.latitude,
                    locationSystem.getDeviceLocation().await()!!.longitude) as String
            } else {
                locationSystem.getLocationString(
                    locationSystem.getCustomLocationCoordinates()!![0],
                    locationSystem.getCustomLocationCoordinates()!![1]) as String
            },
            weather.latitude,
            weather.longitude,
        )
    }

    private suspend fun updateSevenDaysWeather( sevenDaysWeatherResponse: SevenDaysWeatherResponse, unitsIdentifier: String) {

        for (i in 0..6) {
            var sevenDaysWeather  = SevenDaysWeather(
                sevenDaysWeatherResponse.sevenDaysWeatherEntry.time[i],
                sevenDaysWeatherResponse.sevenDaysWeatherEntry.temperature2mMax[i],
                sevenDaysWeatherResponse.sevenDaysWeatherEntry.temperature2mMin[i],
                sevenDaysWeatherResponse.sevenDaysWeatherEntry.weatherCode[i],
                sevenDaysWeatherResponse.sevenDaysWeatherEntry.sunRise[i],
                sevenDaysWeatherResponse.sevenDaysWeatherEntry.sunSet[i],
                sevenDaysWeatherResponse.sevenDaysWeatherEntry.uvIndexMax[i],
                sevenDaysWeatherResponse.sevenDaysWeatherEntry.precipitationSum[i],
                sevenDaysWeatherResponse.sevenDaysWeatherEntry.windSpeed[i],
                sevenDaysWeatherResponse.sevenDaysWeatherEntry.windDirection[i],
                unitsIdentifier,
                if (sevenDaysWeatherResponse.dailyUnits.precipitationSum == METRIC_UNIT_MM) i else i + 7
            )
            sevenDaysWeatherDao.updateInsert(sevenDaysWeather)
        }
    }

    private fun getSevenDaysWeatherList(metric: Boolean): LiveData<List<SevenDaysWeather>> {
        return if (metric)
            sevenDaysWeatherDao.getSevenDaysWeatherMetric() else sevenDaysWeatherDao.getSevenDaysWeatherImperial()
    }

    override fun getSevenDaysItemWeather(id: Int): SevenDaysWeather {
        return sevenDaysWeatherDao.getSevenDaysItemWeather(id)
    }

}