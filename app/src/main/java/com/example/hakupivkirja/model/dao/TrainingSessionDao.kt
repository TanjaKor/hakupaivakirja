package com.example.hakupivkirja.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Update
import com.example.hakupivkirja.model.PistoStateEntity
import com.example.hakupivkirja.model.TrainingSession

@Dao
interface TrainingSessionDao {
  // For saving
  @Transaction
  suspend fun insertTrainingSession(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>
  ) {
    val sessionId = insertTrainingSession(trainingSession)
    val updatedPistoStates = pistoStates.map {
      it.copy(trainingSessionId = sessionId)
    }
    insertPistoStates(updatedPistoStates)
  }
  @Insert
  suspend fun insertTrainingSession(trainingSession: TrainingSession): Long

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertPistoStates(pistoStates: List<PistoStateEntity>)

  // Update operations
  @Update
  suspend fun updatePistoState(pisto: PistoStateEntity)

  @Update
  suspend fun updateTrainingSession(trainingSession: TrainingSession)
}