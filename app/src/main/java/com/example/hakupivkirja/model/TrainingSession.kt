package com.example.hakupivkirja.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation


@Entity(tableName = "training_sessions")
data class TrainingSession(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val dateMillis: Long = System.currentTimeMillis(), // Store date as Long (nullable to match your composable)
  var shortDescription: String? = "", // short description of the track when planning
  var dogName: String = "",
  var alarmType: String? = null, // e.g., "Bark", "Roll"
  var notes: String? = null, // Notes after the training
  var overallRating: Int? = null, // 5-point star classification for overall training
  var difficultyRating: Int? = null, // 5-point star classification for difficulty for the dog
  var trackLength: String = "100m" // e.g., in meters
)

// Data class for fetching with related pistostates
data class TrainingSessionWithPistoStates(
  @Embedded val trainingSession: TrainingSession,
  @Relation(
    parentColumn = "id",
    entityColumn = "trainingSessionId"
  )
  val pistoStates: List<PistoStateEntity>
)
