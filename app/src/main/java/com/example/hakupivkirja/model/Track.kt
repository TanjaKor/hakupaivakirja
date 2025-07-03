package com.example.hakupivkirja.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
  tableName = "tracks",
  foreignKeys = [
    ForeignKey(
      entity = TrainingSession::class, // Parent entity
      parentColumns = ["id"],      // Primary key column in TrainingSession
      childColumns = ["trainingSessionId"],  // Foreign key column in TrackEntity
      onDelete = ForeignKey.CASCADE, // Optional: Deletes tracks if the parent trainingsession is deleted
      onUpdate = ForeignKey.CASCADE  // Optional: Updates trainingSessionId if the parent training session id is updated
    )
  ]
)
data class TrackEntity (
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val trainingSessionId: Long, // Foreign key to TrainingSession
  val trackLength: Int // e.g., in meters
)