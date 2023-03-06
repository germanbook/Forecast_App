package com.example.forecastapp.ui.weather.current

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.forecastapp.data.db.entity.current.CurrentWeather
import com.example.forecastapp.data.provider.LocationProvider
import com.example.forecastapp.data.provider.UnitProvider
import com.example.forecastapp.data.repository.ForecastRepository
import com.example.forecastapp.internal.UnitSystem
import kotlinx.coroutines.Deferred


class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider,
    locationProvider: LocationProvider,
    private val context: Context,
) : ViewModel() {

    private val unitSystem = unitProvider
    private val locationSystem = locationProvider

    val isMetric: Boolean
        get() = unitSystem.getUnitSystem() == UnitSystem.METRIC

    val isUseDeviceLocation: Boolean
        get() = locationSystem.isUseDeviceLocation()

    val deviceLocation: Deferred<Location?>
        get() = locationSystem.getDeviceLocation()
    suspend fun updateWeather(latitude: Double, longitude: Double):
            LiveData<out CurrentWeather> {
        return forecastRepository.getCurrentWeather(isMetric, latitude, longitude)
    }

    suspend fun getCustomLocationCoordinates(): List<Double> {
        val gc: Geocoder = Geocoder(context)
        val ads: List<Address> = gc.getFromLocationName(locationSystem.getPreferredLocationString() as String, 5) as List<Address>
        var coordinates = ArrayList<Double>(ads.size)
        for ( i in ads) {
            if(i.hasLatitude() && i.hasLongitude()) {
                coordinates.add(i.latitude)
                coordinates.add(i.longitude)
            }
        }
        return coordinates
    }
}