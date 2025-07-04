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
  val shortDescription: String = "", // short description of the track when planning
  val dogName: String = "",
  val alarmType: String? = null, // e.g., "Bark", "Roll"
  val notes: String? = null, // Notes after the training
  val overallRating: Int? = null, // 5-point star classification for overall training
  val difficultyRating: Int? = null, // 5-point star classification for difficulty for the dog
  val trackLength: String = "100m" // e.g., in meters
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
