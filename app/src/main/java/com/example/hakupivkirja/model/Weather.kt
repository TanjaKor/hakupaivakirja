package com.example.hakupivkirja.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_conditions")
data class Weather(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val trainingSessionId: Long?, // Foreign key to TrainingSession
  val weatherDescription: String?, // e.g., "Sunny", "Rainy"
  val temperatureCelsius: Double?,
  val windSpeed: Double?, // e.g., in m/s or km/h
  val windDirection: String? // e.g., "North", "Southwest"
)