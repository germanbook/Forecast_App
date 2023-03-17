package com.example.forecastapp.ui.weather.current

import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    ): View {
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
                if (hasLocationPermission())
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
                val location: LiveData<out DownloadedCurrentWeatherLocation> =
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

    private fun getWeatherLocation(): Deferred<Location?> {
        return viewModel.deviceLocation
    }

    private fun isUseDeviceLocation(): Boolean {
        return viewModel.isUseDeviceLocation
    }

    private fun updateWeatherUI(latitude: Double, longitude: Double) = launch {

        val downloadedCurrentWeatherLocation: LiveData<out DownloadedCurrentWeatherLocation> =
            viewModel.getDownloadedCurrentWeatherLocation()

        val currentWeather: LiveData<out CurrentWeather> =
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
                    viewModel.getConditionIcon(it.weathercode!!),

                )

            })
        }
    }

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

}