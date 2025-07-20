package com.example.hakupivkirja.model.repository
import com.example.hakupivkirja.BuildConfig
import com.example.hakupivkirja.network.WeatherApi
import com.example.hakupivkirja.network.WeatherDetails



interface WeatherRepository {
  suspend fun getWeather(trainingLocation: String): WeatherDetails
}

class NetworkWeatherRepository : WeatherRepository {
  private val apiKey = BuildConfig.WEATHER_API_KEY
  override suspend fun getWeather(trainingLocation: String): WeatherDetails {
    return WeatherApi.retrofitService.getWeather(trainingLocation, apiKey ,  "metric")
  }
}