package com.example.hakupivkirja.network

import kotlinx.serialization.Serializable

@Serializable
data class WeatherDetails (
val main: Main,
val weather: List<Weather>,
val wind: Wind,
)
@Serializable
data class Main(
  val temp: Double
)
@Serializable
data class Weather(
  val id: Int,
  val main: String,
  val description: String,
  val icon: String
)
@Serializable
data class Wind(
  val speed: Double,
  val deg: Int
)