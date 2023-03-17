package com.example.forecastapp.ui.weather.current

import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.forecastapp.data.viewdata.currentweather.CurrentWeatherViewData
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

        if (viewModel.isOnline()) {
            if (isUseDeviceLocation()) {
                updateWeatherUI(getWeatherLocation().await()!!.latitude, getWeatherLocation().await()!!.longitude)
            }
            else {
                if (viewModel.getCustomLocationCoordinates() == null)
                // NO INTERNET CONNECTION
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
            // NO INTERNET CONNECTION
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
        if (view != null) {

            downloadedCurrentWeatherLocation.observe(viewLifecycleOwner, Observer {
                if (it == null) return@Observer
                updateLocation(it.addressString)
            })

            currentWeather.observe(viewLifecycleOwner, Observer {
                if(it == null) return@Observer
                updateDateToToday()

                binding.currentWeatherViewData = CurrentWeatherViewData(
                    it.temperature.toString() + chooseLocalizedUnitAbbreviation(METRIC_UNIT_CELSIUS_SIGN, IMPERIAL_UNIT_FAHRENHEIT_SIGN),
                    getText(R.string.current_fragment_wind_speed) as String +
                            " " + it.windspeed.toString() +
                            chooseLocalizedUnitAbbreviation(METRIC_UNIT_KMH, IMPERIAL_UNIT_MPH),
                    getText(R.string.current_fragment_wind_direction) as String +
                            " " + it.winddirection.toString() + "°",
                    getResourceString("@string/c"+it.weathercode)!!,
                    getText(R.string.current_fragment_temperature_max) as String +
                            " " + it.temperature2mMax.toString() +
                            chooseLocalizedUnitAbbreviation(METRIC_UNIT_CELSIUS_SIGN, IMPERIAL_UNIT_FAHRENHEIT_SIGN),
                    getText(R.string.current_fragment_temperature_min) as String +
                            " " + it.temperature2mMin.toString() +
                            chooseLocalizedUnitAbbreviation(METRIC_UNIT_CELSIUS_SIGN, IMPERIAL_UNIT_FAHRENHEIT_SIGN),
                    getText(R.string.current_fragment_sunrise) as String + " " +
                            it.sunrise?.substring(it.sunrise.length - 5, it.sunrise.length) +
                            " " + getText(R.string.current_fragment_am),
                    getText(R.string.current_fragment_sunset) as String + " " +
                            it.sunset?.substring(it.sunset.length - 5, it.sunset.length) +
                            " " + getText(R.string.current_fragment_pm),
                    getText(R.string.current_fragment_uv_index_max) as String + " " + if (it.uvIndexMax == null) "--" else it.uvIndexMax.toString(),
                    View.GONE,
                    View.GONE,
                    getConditionIcon(it.weathercode!!),

                )

            })
        }
    }

}