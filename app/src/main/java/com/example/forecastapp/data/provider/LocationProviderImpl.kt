package com.example.forecastapp.data.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.forecastapp.internal.LocationPermissionNotGrantedException
import com.example.forecastapp.internal.UnitSystem
import com.example.forecastapp.internal.asDeferred
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Deferred

const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"
const val CUSTOM_LOCATION = "CUSTOM_LOCATION"

class LocationProviderImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    context: Context,
) : PreferenceProvider(context), LocationProvider {

    private val appContext = context.applicationContext

    override suspend fun getPreferredLocationString(): String? {
        return preferences.getString(CUSTOM_LOCATION, null)
    }

    override fun isUseDeviceLocation(): Boolean {
        return preferences.getBoolean(USE_DEVICE_LOCATION, true)
    }

    @SuppressLint("MissingPermission")
    override fun getDeviceLocation(): Deferred<Location?> {

//        var _location: Location = Location(LocationManager.NETWORK_PROVIDER)
//
//        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location ->
//            _location = location
//            var a = location.latitude
//            var b = location.longitude
//        }
//        return _location

        return if (hasLocationPermission())
            fusedLocationProviderClient.lastLocation.asDeferred()
        else
            throw LocationPermissionNotGrantedException()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun hasLocationChanged(): Boolean {
        return true
    }

}