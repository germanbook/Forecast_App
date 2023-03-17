package com.example.forecastapp.data.viewdata.currentweather

import androidx.annotation.DrawableRes

data class CurrentWeatherViewData(
    var temperature: String?,
    val windspeed: String?,
    val winddirection: String?,
    val weathercode: String?,
    val temperature2mMax: String?,
    val temperature2mMin: String?,
    val sunrise: String?,
    val sunset: String?,
    val uvIndexMax: String?,
    val isVisibleProgressBarIcon: Int,
    val isVisibleProgressBarText: Int,
    @DrawableRes
    val weatherConditionIcon: Int,
)