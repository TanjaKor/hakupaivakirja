package com.example.hakupivkirja.model.api

data class WeatherResponse(
  val main: Main,
  val weather: List<Weather>,
  val wind: Wind,
  val name: String
)

data class Main(
  val temp: Double,
  val humidity: Int
)

data class Weather(
  val main: String,
  val description: String
)

data class Wind(
  val speed: Double,
  val direction: String
)
