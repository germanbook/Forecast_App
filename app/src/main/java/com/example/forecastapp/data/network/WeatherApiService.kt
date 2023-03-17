package com.example.forecastapp.data.network

import com.example.forecastapp.data.network.response.CurrentWeatherResponse
import com.example.forecastapp.data.network.response.SevenDaysWeatherResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

const val API_URL = "https://api.open-meteo.com/v1/"
interface WeatherApiService {

    @GET("forecast")
    fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("temperature_unit") temperatureUnit: String,
        @Query("windspeed_unit") windSpeedUnit: String,
        @Query("precipitation_unit") precipitationUnit: String,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,sunrise,sunset,uv_index_max,precipitation_sum",
        @Query("current_weather") current_weather: Boolean = true,
    ): Deferred<CurrentWeatherResponse>

    @GET("forecast")
    fun getSevenDaysWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("temperature_unit") temperatureUnit: String,
        @Query("windspeed_unit") windSpeedUnit: String,
        @Query("precipitation_unit") precipitationUnit: String,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,weathercode,sunrise,sunset,uv_index_max,precipitation_sum,windspeed_10m_max,winddirection_10m_dominant"
    ): Deferred<SevenDaysWeatherResponse>

    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): WeatherApiService {
            val requestInterceptor = Interceptor { chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("timezone", "auto")
                    .build()

                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(connectivityInterceptor)
                .build()


            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(API_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService::class.java)
        }
    }
}