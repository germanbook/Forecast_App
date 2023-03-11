package com.example.forecastapp.data.network

import com.example.forecastapp.data.network.response.CurrentWeatherResponse
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
    ): Deferred<CurrentWeatherResponse>

    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): WeatherApiService {
            val requestInterceptor = Interceptor { chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("daily", "temperature_2m_max")
                    .addQueryParameter("daily", "temperature_2m_min")
                    .addQueryParameter("daily", "sunrise")
                    .addQueryParameter("daily", "sunset")
                    .addQueryParameter("daily", "uv_index_max")
                    .addQueryParameter("daily", "precipitation_sum")
                    .addQueryParameter("current_weather", "true")
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