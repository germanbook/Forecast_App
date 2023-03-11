package com.example.forecastapp.ui.weather.current

import android.content.Context
import android.content.res.Resources
import android.location.Location
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.forecastapp.R
import com.example.forecastapp.data.db.entity.current.CurrentWeather
import com.example.forecastapp.data.db.entity.current.DownloadedCurrentWeatherLocation
import com.example.forecastapp.data.repository.IMPERIAL_UNIT_MPH
import com.example.forecastapp.data.repository.METRIC_UNIT_KMH
import com.example.forecastapp.databinding.FragmentCurrentWeatherBinding
import com.example.forecastapp.ui.base.ScopedFragment
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

const val METRIC_UNIT_CELSIUS_SIGN = "°C"
const val IMPERIAL_UNIT_FAHRENHEIT_SIGN = "°F"

class   CurrentWeatherFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private lateinit var binding: FragmentCurrentWeatherBinding
    private val viewModelFactory: CurrentWeatherViewModelFactory by instance()
    private lateinit var viewModel: CurrentWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrentWeatherBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CurrentWeatherViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = launch {

        if (isOnline()) {
            if (isUseDeviceLocation())
                updateWeatherUI(getWeatherLocation().await()!!.latitude, getWeatherLocation().await()!!.longitude)
            else {
                if (viewModel.getCustomLocationCoordinates() == null)
                    emptyLocationEnteredAlert()
                else
                    updateWeatherUI(viewModel.getCustomLocationCoordinates()!![0], viewModel.getCustomLocationCoordinates()!![1])
            }
        }
        else {
            if (viewModel.isWeatherDataDownloaded()) {
                var location: LiveData<out DownloadedCurrentWeatherLocation> =
                    viewModel.getDownloadedCurrentWeatherLocation()

                location.observe(viewLifecycleOwner, Observer {
                    if (it == null) return@Observer
                    updateWeatherUI(it.latitude, it.longitude)
                })
            }
            internetConnectionAlert()
        }
    }

    private fun chooseLocalizedUnitAbbreviation(metric: String, imperial: String): String {
        return if (viewModel.isMetric) metric else imperial
    }

    private fun updateLocation(location: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
    }

    private fun updateDateToToday() {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = getText(R.string.current_fragment_actionbar_subtitle)
    }

    private fun updateTemperature(temperature: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(METRIC_UNIT_CELSIUS_SIGN, IMPERIAL_UNIT_FAHRENHEIT_SIGN)
        binding.textViewTemperature.text = "$temperature$unitAbbreviation"
    }

    private fun updateCondition(conditionCode: Int) {
        var resourceStr: String = "@string/c"+conditionCode
        var str: String? = getResourceString(resourceStr)
        binding.textViewCondition.text = str
    }

    private fun updateWindSpeed(windSpeed: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(METRIC_UNIT_KMH, IMPERIAL_UNIT_MPH)
        binding.textViewWindSpeed.text = getText(R.string.current_fragment_wind_speed) as String + " $windSpeed$unitAbbreviation"
    }

    private fun updateWindDirection(windDirection: Double) {
        binding.textViewWindDirection.text = getText(R.string.current_fragment_wind_direction) as String + " $windDirection °"
    }

    private fun updateConditionIcon(conditionCode: Int) {
        binding.imageViewConditionIcon.setImageResource(getConditionIcon(conditionCode))
    }

    private fun updateTemperatureMax(temperatureMax: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(METRIC_UNIT_CELSIUS_SIGN, IMPERIAL_UNIT_FAHRENHEIT_SIGN)
        binding.textViewTemperatureMax.text = getText(R.string.current_fragment_temperature_max) as String + " $temperatureMax$unitAbbreviation"
    }

    private fun updateTemperatureMin(temperatureMin: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(METRIC_UNIT_CELSIUS_SIGN, IMPERIAL_UNIT_FAHRENHEIT_SIGN)
        binding.textViewTemperatureMin.text = getText(R.string.current_fragment_temperature_min) as String + " $temperatureMin$unitAbbreviation"
    }

    private fun updateSunriseTime(sunrise: String) {
        binding.textViewSunrise.text = getText(R.string.current_fragment_sunrise) as String +
                " ${sunrise.substring(sunrise.length - 5, sunrise.length)} " +
                getText(R.string.current_fragment_am)
    }

    private fun updateSunsetTime(sunset: String) {
        binding.textViewSunset.text = getText(R.string.current_fragment_sunset) as String +
                " ${sunset.substring(sunset.length - 5, sunset.length)} " +
                getText(R.string.current_fragment_pm)
    }

    private fun updateUVIndexMax(uvIndexMax: Double) {
        binding.textViewUvIndexMax.text = getText(R.string.current_fragment_uv_index_max) as String + " $uvIndexMax"
    }

    private fun getResourceString(resName: String): String? {
        return try {
            context?.resources?.let { context?.resources!!.getString(it.getIdentifier(resName, "String", context?.packageName)) }
        } catch (e: Resources.NotFoundException) {
            null
        }
    }

    private fun getConditionIcon(conditionCode: Int): Int {
        var weatherResource: Int = 0
        when(conditionCode) {
            0, 1 -> weatherResource = R.drawable.weather_sun
            2 -> weatherResource = R.drawable.weather_news
            3 -> weatherResource = R.drawable.weather_cloud
            4, 5, 10, 11, 20, in 30..35 ->
                weatherResource = R.drawable.weather_fog
            12 -> weatherResource = R.drawable.weather_storm_01
            18, 26 -> weatherResource = R.drawable.weather_storm
            21 -> weatherResource = R.drawable.weather_rainy_day
            22 -> weatherResource = R.drawable.weather_raining
            23, in 40..68 -> weatherResource = R.drawable.weather_rain
            24, 25, in 70..79, in 80..87 ->
                weatherResource = R.drawable.weather_snow
            27, 28, 29 -> weatherResource = R.drawable.weather_wind_01
            89 -> weatherResource = R.drawable.weather_hail
            in 90..96 -> weatherResource = R.drawable.weather_storm_02
            99 -> weatherResource = R.drawable.weather_sandstorm
        }
        return weatherResource
    }


    private fun getWeatherLocation(): Deferred<Location?> {
        return viewModel.deviceLocation
    }

    private fun isUseDeviceLocation(): Boolean {
        return viewModel.isUseDeviceLocation
    }

    private fun updateWeatherUI(latitude: Double, longitude: Double) = launch {

        var downloadedCurrentWeatherLocation: LiveData<out DownloadedCurrentWeatherLocation> =
            viewModel.getDownloadedCurrentWeatherLocation()

        var currentWeather: LiveData<out CurrentWeather> =
            viewModel.updateWeather(latitude, longitude)

        downloadedCurrentWeatherLocation.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            updateLocation(it.addressString)
        })

        currentWeather.observe(viewLifecycleOwner, Observer {
            if(it == null) return@Observer
            binding.groupLoading.visibility = View.GONE
            updateDateToToday()
            updateTemperature(it.temperature)
            updateCondition(it.weathercode)
            updateConditionIcon(it.weathercode)
            updateTemperatureMax(it.temperature2mMax)
            updateTemperatureMin(it.temperature2mMin)
            updateSunriseTime(it.sunrise)
            updateSunsetTime(it.sunset)
            updateUVIndexMax(it.uvIndexMax)
            updateWindSpeed(it.windspeed)
            updateWindDirection(it.winddirection)
        })
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun internetConnectionAlert() {
        val builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle(R.string.no_internet_alert_title)
        builder.setMessage(R.string.no_internet_alert_message)
        builder.setPositiveButton(android.R.string.ok) { dialog, which -> }
        builder.show()
    }

    private fun emptyLocationEnteredAlert() {
        val builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle(R.string.no_location_entered_alert_title)
        builder.setMessage(R.string.no_location_entered_alert_message)
        builder.setPositiveButton(android.R.string.ok) { dialog, which -> }
        builder.show()
    }
}