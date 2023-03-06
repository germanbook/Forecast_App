package com.example.forecastapp.ui.weather.current

import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.forecastapp.R
import com.example.forecastapp.data.db.entity.current.CurrentWeather
import com.example.forecastapp.databinding.FragmentCurrentWeatherBinding
import com.example.forecastapp.ui.base.ScopedFragment
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.lang.reflect.Field

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

        lateinit var currentWeather: LiveData<out CurrentWeather>

        if (isUseDeviceLocation()) {
            currentWeather = viewModel.updateWeather(getWeatherLocation().await()!!.latitude, getWeatherLocation().await()!!.longitude)
            currentWeather.observe(viewLifecycleOwner, Observer {
                if(it == null) return@Observer
                binding.groupLoading.visibility = View.GONE

                updateLocation("Wellington")
                updateDateToToday()
                updateTemperature(it.temperature)
                updateCondition(it.weathercode)
                updateConditionIcon(it.weathercode)
                updateWindSpeed(it.windspeed)
                updateWindDirection(it.winddirection)
            })
        }
        else{
            viewModel.getCustomLocationCoordinates()

            currentWeather = viewModel.updateWeather(viewModel.getCustomLocationCoordinates()[0], viewModel.getCustomLocationCoordinates()[1])
            currentWeather.observe(viewLifecycleOwner, Observer {
                if(it == null) return@Observer
                binding.groupLoading.visibility = View.GONE

                updateLocation("Wellington")
                updateDateToToday()
                updateTemperature(it.temperature)
                updateCondition(it.weathercode)
                updateConditionIcon(it.weathercode)
                updateWindSpeed(it.windspeed)
                updateWindDirection(it.winddirection)
            })


            Toast.makeText(activity, "Please set location manually in settings", Toast.LENGTH_LONG).show()
        }

    }

    private fun chooseLocalizedUnitAbbreviation(metric: String, imperial: String): String {
        return if (viewModel.isMetric) metric else imperial
    }

    private fun updateLocation(location: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
    }

    private fun updateDateToToday() {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Today"
    }

    private fun updateTemperature(temperature: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("°C", "°F")
        binding.textViewTemperature.text = "$temperature$unitAbbreviation"
    }

    private fun updateCondition(conditionCode: Int) {
        var resourceStr: String = "@string/c"+conditionCode
        var str: String? = getResourceString(resourceStr)
        binding.textViewCondition.text = str
    }

    private fun updateWindSpeed(windSpeed: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("kmh", "mph")
        binding.textViewWindSpeed.text = "Wind Speed: $windSpeed$unitAbbreviation"
    }

    private fun updateWindDirection(windDirection: Double) {
        binding.textViewWindDirection.text = "Wind Direction: $windDirection °"
    }

    private fun updateConditionIcon(conditionCode: Int) {
        binding.imageViewConditionIcon.setImageResource(getConditionIcon(conditionCode))
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
            1 -> weatherResource = R.drawable.weather_sun
            0, 2 -> weatherResource = R.drawable.weather_news
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
}