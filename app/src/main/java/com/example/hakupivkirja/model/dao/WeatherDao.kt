package com.example.hakupivkirja.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hakupivkirja.model.Weather
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
  @Query("SELECT * FROM weather_conditions WHERE trainingSessionId = :sessionId")
  fun getWeatherBySessionId(sessionId: Long): Flow<Weather?>

  @Insert
  suspend fun insertWeather(weather: Weather)

  @Update
  suspend fun updateWeather(weather: Weather)

  @Delete
  suspend fun deleteWeather(weather: Weather)
}