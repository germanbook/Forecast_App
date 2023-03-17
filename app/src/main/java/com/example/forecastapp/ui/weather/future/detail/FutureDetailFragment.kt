package com.example.forecastapp.ui.weather.future.detail

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.forecastapp.R
import com.example.forecastapp.data.db.entity.sevendays.SevenDaysWeather
import com.example.forecastapp.data.repository.IMPERIAL_UNIT_MPH
import com.example.forecastapp.data.repository.METRIC_UNIT_KMH
import com.example.forecastapp.data.viewdata.sevendaysweather.SevenDaysWeatherListItemViewData
import com.example.forecastapp.databinding.FragmentFutureDetailBinding
import com.example.forecastapp.ui.base.ScopedFragment
import com.example.forecastapp.ui.weather.current.IMPERIAL_UNIT_FAHRENHEIT_SIGN
import com.example.forecastapp.ui.weather.current.METRIC_UNIT_CELSIUS_SIGN
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class FutureDetailFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()

    private val viewModelFactory : FutureDetailViewModelFactory by instance()

    private lateinit var viewModel: FutureDetailViewModel

    lateinit var binding: FragmentFutureDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFutureDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(FutureDetailViewModel::class.java)

        if (!requireArguments().isEmpty) {
            arguments?.apply {
                getInt("id").let { BuildUI(it) }
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
            getText(R.string.current_fragment_sunrise).toString() + " " +sevenDaysWeather.sunRise?.substring(sevenDaysWeather.sunRise.length - 5, sevenDaysWeather.sunRise.length) + " " + getText(R.string.current_fragment_am),
            getText(R.string.current_fragment_sunset).toString() + " " +sevenDaysWeather.sunRise?.substring(sevenDaysWeather.sunRise.length - 5, sevenDaysWeather.sunRise.length) + " " + getText(R.string.current_fragment_pm),
            getText(R.string.current_fragment_uv_index_max).toString() + " " + if (sevenDaysWeather.uvIndexMax == null) "--" else sevenDaysWeather.uvIndexMax.toString(),
            sevenDaysWeather.precipitationSum.toString(),
            getText(R.string.current_fragment_wind_speed).toString()+ " " +sevenDaysWeather.windSpeed.toString() + chooseLocalizedUnitAbbreviation(METRIC_UNIT_KMH, IMPERIAL_UNIT_MPH),
            getText(R.string.current_fragment_wind_direction) as String + " " + sevenDaysWeather.windDirection.toString() + "°",
            viewModel.getConditionIcon(sevenDaysWeather.weatherCode!!),
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