package com.example.forecastapp.ui.weather.future.detail

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.forecastapp.R
import com.example.forecastapp.data.db.entity.sevendays.SevenDaysWeather
import com.example.forecastapp.data.viewdata.sevendaysweather.SevenDaysWeatherListItemViewData
import com.example.forecastapp.databinding.FragmentFutureDetailBinding
import com.example.forecastapp.ui.base.ScopedFragment
import com.example.forecastapp.ui.weather.current.IMPERIAL_UNIT_FAHRENHEIT_SIGN
import com.example.forecastapp.ui.weather.current.METRIC_UNIT_CELSIUS_SIGN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import org.kodein.di.generic.instance

class FutureDetailFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()

    private val viewModelFactory : FutureDetailViewModelFactory by instance()

    private lateinit var viewModel: FutureDetailViewModel

    lateinit var binding: FragmentFutureDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFutureDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(FutureDetailViewModel::class.java)

        if (!requireArguments().isEmpty) {
            arguments?.apply {
                getInt("id")?.let { BuildUI(it) }
                arguments?.clear()
            }
        }else findNavController().navigate(R.id.futureListWeatherFragment)

    }

    fun BuildUI(id: Int) {
        val sevenDaysWeather = viewModel.getSevenDaysItemWeather(id)
        val itemViewData = sevenDaysWeatherListItemViewDataConverter(sevenDaysWeather)
        updateDate(itemViewData.time!!)
        binding.sevenDaysWeatherListItem = itemViewData

    }

    fun sevenDaysWeatherListItemViewDataConverter(sevenDaysWeather: SevenDaysWeather)
        : SevenDaysWeatherListItemViewData {
        return SevenDaysWeatherListItemViewData(
            sevenDaysWeather.time,
            getText(R.string.current_fragment_temperature_max).toString() + " " + sevenDaysWeather.temperature2mMax.toString()+chooseLocalizedUnitAbbreviation(
                METRIC_UNIT_CELSIUS_SIGN, IMPERIAL_UNIT_FAHRENHEIT_SIGN
            ),
            getText(R.string.current_fragment_temperature_min).toString() + " " + sevenDaysWeather.temperature2mMin.toString()+chooseLocalizedUnitAbbreviation(
                METRIC_UNIT_CELSIUS_SIGN, IMPERIAL_UNIT_FAHRENHEIT_SIGN
            ),
            getResourceString("@string/c"+sevenDaysWeather.weatherCode)!!,
            getText(R.string.current_fragment_sunrise).toString() + " " +sevenDaysWeather.sunRise + " " + getText(R.string.current_fragment_am),
            getText(R.string.current_fragment_sunset).toString() + " " +sevenDaysWeather.sunSet + " " + getText(R.string.current_fragment_pm),
            getText(R.string.current_fragment_uv_index_max).toString() + " " + if (sevenDaysWeather.uvIndexMax == null) "--" else sevenDaysWeather.uvIndexMax.toString(),
            sevenDaysWeather.precipitationSum.toString(),
            sevenDaysWeather.windSpeed.toString(),
            sevenDaysWeather.windDirection.toString(),
            getConditionIcon(sevenDaysWeather.weatherCode!!),
            sevenDaysWeather.id,
            onClicked = :: itemClicked,
        )
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

    private fun chooseLocalizedUnitAbbreviation(metric: String, imperial: String): String {
        return if (viewModel.isMetric) metric else imperial
    }

    fun itemClicked(id: Int) {
        // Dummy
    }

    private fun updateDate(date: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            title = date
            subtitle = null
        }
    }

}