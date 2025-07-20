package com.example.hakupivkirja.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

private val json = Json { ignoreUnknownKeys = true }

//luodaan Retrofit olio, scalarsin avulla muutetaan json stringiksi
private val retrofit = Retrofit.Builder()
  .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
  .baseUrl(BASE_URL)
  .build()

//määrittelee kuinka retrofit keskustelee webin kanssa
interface WeatherApiService {
  @GET("weather")
  suspend fun getWeather(
    @Query("q") cityName: String,
    @Query("appid") apiKey: String,
    @Query("units") units: String
  ): WeatherDetails
}

//retrofitin luonti vie paljon muistia, joten luodaan se vain kerran, siksi julkinen olio
//lazylla varmistetaan, että luodaan vasta kun tarvitaan eikä turhaan tukita appia
object WeatherApi {
  val retrofitService: WeatherApiService by lazy {
    retrofit.create(WeatherApiService::class.java)
  }
}