package com.example.hakupivkirja.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
  tableName = "weather_conditions",
  foreignKeys = [
    ForeignKey(
      entity = TrainingSession::class,
      parentColumns = ["id"],
      childColumns = ["trainingSessionId"],
      onDelete = ForeignKey.CASCADE,
      onUpdate = ForeignKey.CASCADE
    )
  ]
)
data class WeatherEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  @ColumnInfo(name = "trainingSessionId")
  val trainingSessionId: Long?, // Foreign key to TrainingSession
  val weatherDescription: String?, // e.g., "Sunny", "Rainy"
  val temperatureCelsius: Double?,
  val windSpeed: Double?, // e.g., in m/s or km/h
  val windDirection: String? // e.g., "North", "Southwest"
)