package com.example.hakupivkirja.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "terrain_details")
data class Terrain(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val trainingSessionId: Long?, // Foreign key to TrainingSession
  val forestThickness: Int?, // 5-point scale
  val altitudeChanges: Int?, // 5-point scale
  val moistureLevel: Int?, // 5-point scale
  val overallDifficultyLevel: Double? // Calculated difficulty
)