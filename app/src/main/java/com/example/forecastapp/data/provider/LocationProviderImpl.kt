package com.example.forecastapp.data.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.forecastapp.internal.LocationPermissionNotGrantedException
import com.example.forecastapp.internal.asDeferred
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Deferred
import java.util.*

const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"
const val CUSTOM_LOCATION = "CUSTOM_LOCATION"

class LocationProviderImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val context: Context,
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

        return if (hasLocationPermission()) {
            var a = fusedLocationProviderClient.lastLocation
            a.asDeferred()
        }
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

    override fun getLocationString(latitude: Double, longitude: Double): String? {
        val gc = Geocoder(context, Locale.getDefault())
        val addresses = gc.getFromLocation(latitude, longitude, 1)

        return if (addresses!!.size > 0) {
            return if (addresses[0].locality != null)
                addresses[0].locality + ", " + addresses[0].countryName
            else
                addresses[0].adminArea + ", " + addresses[0].countryName
        } else {
            null
        }
    }


    override suspend fun getCustomLocationCoordinates(): List<Double>? {

        if (getPreferredLocationString()?.isEmpty() == true ||
                getPreferredLocationString()?.isBlank() == true)
        {
            return null
        }else {
            val gc: Geocoder = Geocoder(context)

            val ads: List<Address> = gc.getFromLocationName(getPreferredLocationString() as String, 1) as List<Address>
            var coordinates = ArrayList<Double>(ads.size)
            for ( i in ads) {
                if(i.hasLatitude() && i.hasLongitude()) {

                    var l = i.latitude
                    var long = i.longitude

                    coordinates.add(l)
                    coordinates.add(long)
                }
            }
            return coordinates
        }


    }

}