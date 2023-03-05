package com.example.forecastapp.data.provider

import com.example.forecastapp.internal.UnitSystem

interface UnitProvider {
    fun getUnitSystem(): UnitSystem
}