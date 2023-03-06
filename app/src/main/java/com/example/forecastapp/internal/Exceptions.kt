package com.example.forecastapp.internal

import java.io.IOException


class NoConnectivityExceptions: IOException()
class LocationPermissionNotGrantedException: Exception()
class DateNotFoundException: Exception()