package com.example.hakupivkirja.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "training_sessions")
data class TrainingSession(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val dateMillis: Long?, // Store date as Long (nullable to match your composable)
  val shortDescription: String,
  val dogName: String,
  val alarmType: String? = null, // e.g., "Bark", "Roll"
  val notes: String? = null, // Notes after the training
  val overallRating: Int? = null, // 5-point star classification for overall training
  val difficultyRating: Int? = null // 5-point star classification for difficulty for the dog
)
