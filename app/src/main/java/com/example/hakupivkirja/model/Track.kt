package com.example.hakupivkirja.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_sessions")
data class Track (
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val trainingSessionId: Long, // Foreign key to TrainingSession
  val trackLength: Int // e.g., in meters
)