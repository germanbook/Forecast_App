package com.example.forecastapp.ui.weather.future.list

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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.forecastapp.R
import com.example.forecastapp.data.db.entity.current.DownloadedCurrentWeatherLocation
import com.example.forecastapp.data.db.entity.sevendays.SevenDaysWeather
import com.example.forecastapp.data.repository.DATE_FORMAT_DISPLAY
import com.example.forecastapp.data.repository.DATE_FORMAT_IN_SYSTEM
import com.example.forecastapp.data.repository.IMPERIAL_UNIT_MPH
import com.example.forecastapp.data.repository.METRIC_UNIT_KMH
import com.example.forecastapp.databinding.FragmentFutureListWeatherBinding
import com.example.forecastapp.databinding.ItemSevendaysListBinding
import com.example.forecastapp.ui.base.ScopedFragment
import com.example.forecastapp.data.viewdata.sevendaysweather.SevenDaysWeatherListItemViewData
import com.example.forecastapp.ui.weather.current.IMPERIAL_UNIT_FAHRENHEIT_SIGN
import com.example.forecastapp.ui.weather.current.METRIC_UNIT_CELSIUS_SIGN
import com.example.forecastapp.ui.weather.future.list.adapter.FutureListRecyclerViewAdapter
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class FutureListWeatherFragment : ScopedFragment(), KodeinAware {


    override val kodein by closestKodein()
    private val viewModelFactory: FutureListWeatherViewModelFactory by instance()
    private lateinit var binding: FragmentFutureListWeatherBinding
    private lateinit var futureWeatherItemUiBinding: ItemSevendaysListBinding
    private lateinit var viewModel: FutureListWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFutureListWeatherBinding.inflate(layoutInflater, container, false)
        futureWeatherItemUiBinding = ItemSevendaysListBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[FutureListWeatherViewModel::class.java]
        bindUI()
    }

    private fun bindUI() = launch(Dispatchers.Main) {

        if (viewModel.isOnline()) {
            if (isUseDeviceLocation()) {
                updateSevenDaysWeatherUI(getWeatherLocation().await()!!.latitude, getWeatherLocation().await()!!.longitude)
            } else {
                if (viewModel.getCustomLocationCoordinates() == null)
                    //NO INTERNET CONNECTION
                else
                    updateSevenDaysWeatherUI(viewModel.getCustomLocationCoordinates()!![0], viewModel.getCustomLocationCoordinates()!![1])

            }
        }
        else {
            if (viewModel.isSevenDaysWeatherDataDownloaded()) {
                var location: LiveData<out DownloadedCurrentWeatherLocation> =
                    viewModel.getDownloadedCurrentWeatherLocation()

                location.observe(viewLifecycleOwner, Observer {
                    if (it == null) return@Observer
                    updateSevenDaysWeatherUI(it.latitude, it.longitude)
                })
            }
            //NO INTERNET CONNECTION
        }

    }

    private fun updateSevenDaysWeatherUI(latitude: Double, longitude: Double) = launch(Dispatchers.Main) {

        val sevenDaysWeatherList = viewModel.updateSevenDaysWeather(latitude, longitude)

        var weatherLocation: LiveData<out DownloadedCurrentWeatherLocation> = viewModel.getDownloadedCurrentWeatherLocation()

        if (view != null) {
            weatherLocation.observe(viewLifecycleOwner, Observer {
                if (it == null) return@Observer
                updateLocation(it.addressString)
            })

            sevenDaysWeatherList.observe(viewLifecycleOwner, Observer {
                if (it == null) return@Observer
                binding.groupLoading.visibility = View.GONE
                updateDateToNextWeek()
                initRecyclerView(it)
            })
        }
    }

    private fun updateLocation(location: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
    }

    private fun updateDateToNextWeek() {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Next Week"
    }


    private fun getWeatherLocation(): Deferred<Location?> {
        return viewModel.deviceLocation
    }

    private fun isUseDeviceLocation(): Boolean {
        return viewModel.isUseDeviceLocation
    }

    private fun initRecyclerView(sevenDaysWeatherList: List<SevenDaysWeather>) {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = FutureListRecyclerViewAdapter(sevenDaysWeatherListConverter(sevenDaysWeatherList))
        }
    }

    fun sevenDaysWeatherListConverter(sevenDaysWeatherList: List<SevenDaysWeather>): List<SevenDaysWeatherListItemViewData> {
        var list: ArrayList<SevenDaysWeatherListItemViewData> = ArrayList<SevenDaysWeatherListItemViewData>()

        for (i in sevenDaysWeatherList.indices) {
            list.add(
                SevenDaysWeatherListItemViewData(
                    LocalDate.parse(sevenDaysWeatherList[i].time, DateTimeFormatter.ofPattern(DATE_FORMAT_IN_SYSTEM)).format(DateTimeFormatter.ofPattern(DATE_FORMAT_DISPLAY)).toString(),
                    //sevenDaysWeatherList[i].time,
                    getText(R.string.current_fragment_temperature_max).toString() + " " + sevenDaysWeatherList[i].temperature2mMax.toString()+chooseLocalizedUnitAbbreviation(
                        METRIC_UNIT_CELSIUS_SIGN, IMPERIAL_UNIT_FAHRENHEIT_SIGN),
                    getText(R.string.current_fragment_temperature_min).toString() + " " + sevenDaysWeatherList[i].temperature2mMin.toString()+chooseLocalizedUnitAbbreviation(
                        METRIC_UNIT_CELSIUS_SIGN, IMPERIAL_UNIT_FAHRENHEIT_SIGN),
                    getResourceString("@string/c"+sevenDaysWeatherList[i].weatherCode)!!,
                    getText(R.string.current_fragment_sunrise).toString() + " " +sevenDaysWeatherList[i].sunRise + " " + getText(R.string.current_fragment_am),
                    getText(R.string.current_fragment_sunset).toString() + " " +sevenDaysWeatherList[i].sunSet + " " + getText(R.string.current_fragment_pm),
                    getText(R.string.current_fragment_uv_index_max).toString() + " " + sevenDaysWeatherList[i].uvIndexMax.toString(),
                    sevenDaysWeatherList[i].precipitationSum.toString(),
                    getText(R.string.current_fragment_wind_speed).toString() + " " + sevenDaysWeatherList[i].windSpeed + chooseLocalizedUnitAbbreviation(METRIC_UNIT_KMH, IMPERIAL_UNIT_MPH),
                    getText(R.string.current_fragment_wind_direction) as String + " " + sevenDaysWeatherList[i].windDirection + "°",
                    getConditionIcon(sevenDaysWeatherList[i].weatherCode!!),
                    sevenDaysWeatherList[i].id,
                    onClicked = :: itemClicked,
                )
            )
        }
        return list.toList()
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
        var bundle: Bundle = Bundle()
        bundle.putInt("id", id)
        findNavController().navigate(R.id.futureDetailFragment, bundle)
    }
}