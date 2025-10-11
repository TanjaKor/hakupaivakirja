package com.tanjan.hakupivkirja.model.repository

import com.tanjan.hakupivkirja.network.WeatherApi
import com.tanjan.hakupivkirja.network.WeatherDetails

interface WeatherRepository {
  suspend fun getWeather(trainingLocation: String): WeatherDetails
}

class NetworkWeatherRepository : WeatherRepository {
  private val apiKey = _root_ide_package_.com.tanjan.hakupivkirja.BuildConfig.WEATHER_API_KEY
  override suspend fun getWeather(trainingLocation: String): WeatherDetails {
    return WeatherApi.retrofitService.getWeather(trainingLocation, apiKey,  "metric")
  }
}