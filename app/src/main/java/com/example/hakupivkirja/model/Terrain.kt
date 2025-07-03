package com.example.hakupivkirja.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
  tableName = "terrain_details",
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
data class Terrain(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val trainingSessionId: Long?, // Foreign key to TrainingSession
  val forestThickness: Int?, // 5-point scale
  val altitudeChanges: Int?, // 5-point scale
  val moistureLevel: Int?, // 5-point scale
  // this is calculated separately in the composable itself: val overallDifficultyLevel: Double? // Calculated difficulty
)