package com.example.forecastapp.data.provider

import android.location.Location
import kotlinx.coroutines.Deferred

interface LocationProvider {
    suspend fun hasLocationChanged(): Boolean
    suspend fun getPreferredLocationString(): String?
    fun getDeviceLocation(): Deferred<Location?>
    fun isUseDeviceLocation(): Boolean
}