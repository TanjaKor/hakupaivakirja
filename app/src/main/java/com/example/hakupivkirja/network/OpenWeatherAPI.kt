package com.example.hakupivkirja.network


import com.example.hakupivkirja.model.api.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
  @GET("weather")
  suspend fun getCurrentWeather(
    @Query("lat") latitude: Double,
    @Query("lon") longitude: Double,
    @Query("appid") apiKey: String,
    @Query("units") units: String = "metric"
  ): WeatherResponse

  @GET("weather")
  suspend fun getWeatherByCity(
    @Query("q") cityName: String,
    @Query("appid") apiKey: String,
    @Query("units") units: String = "metric"
  ): WeatherResponse

}


