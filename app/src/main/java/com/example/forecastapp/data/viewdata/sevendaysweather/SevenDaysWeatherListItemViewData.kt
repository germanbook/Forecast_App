package com.example.forecastapp.data.viewdata.sevendaysweather

import androidx.annotation.DrawableRes

data class SevenDaysWeatherListItemViewData (
    val time: String?,
    val temperature2mMax: String?,
    val temperature2mMin: String?,
    val weatherCode: String?,
    val sunRise: String?,
    val sunSet: String?,
    val uvIndexMax: String?,
    val precipitationSum: String?,
    val windSpeed: String?,
    val windDirection: String?,
    @DrawableRes
    val weatherConditionIcon: Int,
    var id: Int,
    val onClicked: (int: Int) -> Unit,
)