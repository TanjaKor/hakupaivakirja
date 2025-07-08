package com.example.hakupivkirja.model.dao

import androidx.room.Dao
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.hakupivkirja.model.PistoStateEntity
import com.example.hakupivkirja.model.TrainingSession

@Dao
interface TrainingSessionDao {

  // Main function - handles both save and update
  @Transaction
  suspend fun saveTrainingSession(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>
  ) : TrainingSession {
    val sessionId = if (trainingSession.id == 0L) {
      // New session - insert and get the new ID
      upsertTrainingSession(trainingSession)
    } else {
      // Existing session - update and use existing ID
      upsertTrainingSession(trainingSession)
      trainingSession.id // Use the existing ID since upsert returns -1 for updates
    }

    val updatedPistoStates = pistoStates.map {
      it.copy(trainingSessionId = sessionId)
    }
    upsertPistoStates(updatedPistoStates)

    // Return the session with the correct ID
    return if (trainingSession.id == 0L) {
      trainingSession.copy(id = sessionId)
    } else {
      trainingSession
    }
  }
  // Internal upsert operations
  @Upsert
  suspend fun upsertTrainingSession(trainingSession: TrainingSession): Long

  @Upsert
  suspend fun upsertPistoStates(pistoStates: List<PistoStateEntity>)
}
