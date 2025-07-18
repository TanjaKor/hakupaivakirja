package com.example.hakupivkirja.model.repository

import com.example.hakupivkirja.model.WeatherEntity
import com.example.hakupivkirja.model.api.WeatherResponse
import com.example.hakupivkirja.model.dao.WeatherDao
import com.example.hakupivkirja.network.WeatherApi

class WeatherRepository(
  private val weatherApi: WeatherApi,
  private val weatherDao: WeatherDao
) {
  private val apiKey = "YOUR_API_KEY_HERE" // Store in BuildConfig or secure storage

  private fun WeatherResponse.toWeatherEntity(): WeatherEntity {
    return WeatherEntity(
      temperatureCelsius = main.temp,
      weatherDescription = weather.firstOrNull()?.description ?: "Unknown",
      windDirection = wind.direction,
      windSpeed = wind.speed,
      trainingSessionId = null
    )
  }

  suspend fun getCurrentWeather(
    latitude: Double,
    longitude: Double,
    cacheTimeMinutes: Int = 30
  ): Result<WeatherEntity> {
    return try {
      // Fetch from API
      val response = weatherApi.getCurrentWeather(latitude, longitude, apiKey)
      val weatherEntity = response.toWeatherEntity()

      // Save to database
      val id = weatherDao.upsertWeather(weatherEntity)
      Result.success(weatherEntity.copy(id = id))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun saveWeatherForSession(
    sessionId: Long,
    latitude: Double,
    longitude: Double
  ): Result<WeatherEntity> {
    return try {
      val response = weatherApi.getCurrentWeather(latitude, longitude, apiKey)
      val weatherEntity = response.toWeatherEntity().copy(trainingSessionId = sessionId)

      val id = weatherDao.upsertWeather(weatherEntity)
      Result.success(weatherEntity.copy(id = id))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}

