package com.example.hakupivkirja.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pisto_states")
data class PistoStateEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val trackId: Long, // Foreign key to TrainingSession
  val pistoIndex: Int, // Index of the pisto in the track
  val type: String, // To store the type: "Default", "Tyhja", "MM"
  // Fields for Bark alarm type (nullable)
  val barkAmount: Int? = null,
  // Fields for Roll alarm type (nullable)
  val decoyPraisesDirectly: Boolean? = null,
  val isRollSolid: Boolean? = null,
  val rollPositionWithDecoy: String? = null, // Where the roll is if not solid
  val isClosed: Boolean? = false // Is the hide plain or closed
)