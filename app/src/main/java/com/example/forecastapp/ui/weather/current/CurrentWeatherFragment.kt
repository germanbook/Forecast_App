package com.example.forecastapp.ui.weather.current

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.forecastapp.databinding.FragmentCurrentWeatherBinding
import com.example.forecastapp.ui.base.ScopedFragment
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
        var currentWeather = viewModel.updateWeather()

        currentWeather.observe(viewLifecycleOwner, Observer {
            if(it == null) return@Observer
            binding.groupLoading.visibility = View.GONE

            updateLocation("Wellington")
            updateDateToToday()
            updateTemperature(it.temperature)
            updateCondition(it.weathercode)
            updateWindSpeed(it.windspeed)
            updateWindDirection(it.winddirection)
        })
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

    private fun updateCondition(condition: Int) {
        var resourceStr: String = "@string/c"+condition
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

    private fun getResourceString(resName: String): String? {
        return try {
            context?.resources?.let { context?.resources!!.getString(it.getIdentifier(resName, "String", context?.packageName)) }
        } catch (e: Resources.NotFoundException) {
            null
        }
    }
}