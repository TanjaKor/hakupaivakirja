package com.example.hakupivkirja.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.hakupivkirja.model.WeatherEntity

@Dao
interface WeatherDao {
  @Query("SELECT * FROM weather_conditions WHERE trainingSessionId = :sessionId")
  fun getWeatherBySessionId(sessionId: Long): WeatherEntity?

  @Upsert
  suspend fun upsertWeather(weather: WeatherEntity): Long

  @Update
  suspend fun updateWeather(weather: WeatherEntity)

  @Delete
  suspend fun deleteWeather(weather: WeatherEntity)
}