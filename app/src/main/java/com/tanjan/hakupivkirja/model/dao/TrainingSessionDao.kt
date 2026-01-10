package com.tanjan.hakupivkirja.model.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.tanjan.hakupivkirja.model.PistoStateEntity
import com.tanjan.hakupivkirja.model.TrainingSession
import com.tanjan.hakupivkirja.model.TrainingSessionWithPistoStates
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingSessionDao {
  // ========== FETCH OPERATIONS ==========

  // Get all training sessions ordered by date (newest first)
  @Query("SELECT * FROM training_sessions ORDER BY dateMillis DESC")
  fun getAllTrainingSessions(): Flow<List<TrainingSession>>

  // Get a single training session by ID
  @Query("SELECT * FROM training_sessions WHERE id = :sessionId LIMIT 1")
  suspend fun getTrainingSessionById(sessionId: Long): TrainingSession?

  // Get training session with all related pisto states
  @Transaction
  @Query("SELECT * FROM training_sessions WHERE id = :sessionId LIMIT 1")
  suspend fun getTrainingSessionWithPistoStates(sessionId: Long): TrainingSessionWithPistoStates?

  // Get all training sessions with their pisto states
  @Transaction
  @Query("SELECT * FROM training_sessions ORDER BY dateMillis DESC")
  fun getAllTrainingSessionsWithPistoStates(): Flow<List<TrainingSessionWithPistoStates>>

  // Get training sessions from a specific year
  @Query("SELECT * FROM training_sessions WHERE dateMillis >= :startMillis AND dateMillis <= :endMillis ORDER BY dateMillis DESC")
  fun getTrainingSessionsByYear(startMillis: Long, endMillis: Long): Flow<List<TrainingSession>>

  // Get count of training sessions in a year
  @Query("SELECT COUNT(*) FROM training_sessions WHERE dateMillis >= :startMillis AND dateMillis <= :endMillis")
  suspend fun getTrainingCountByYear(startMillis: Long, endMillis: Long): Int

  // Get pisto states for a specific session
  @Query("SELECT * FROM pisto_states WHERE trainingSessionId = :sessionId ORDER BY pistoIndex ASC")
  suspend fun getPistoStatesForSession(sessionId: Long): List<PistoStateEntity>

  // Get pisto states for multiple sessions
  @Query("SELECT * FROM pisto_states WHERE trainingSessionId IN (:sessionIds)")
  suspend fun getPistoStatesForSessions(sessionIds: List<Long>): List<PistoStateEntity>

  // Get all training session IDs from a specific year
  @Query("SELECT id FROM training_sessions WHERE dateMillis >= :startMillis AND dateMillis <= :endMillis")
  suspend fun getSessionIdsByYear(startMillis: Long, endMillis: Long): List<Long>


  // Main function - handles both save and update
  @Transaction
  suspend fun saveTrainingSession(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>
  ) : TrainingSession {

    val idFromUpsert = upsertTrainingSession(trainingSession)

    // Determine the definitive session ID to use for linking pisto states
    // and for the returned TrainingSession object.
    val definitiveSessionId = if (trainingSession.id == 0L && idFromUpsert > 0L) {
      idFromUpsert // It was a new session, use the ID returned by upsert
    } else {
      trainingSession.id // It was an existing session, use its original ID (which should match idFromUpsert)
    }

    // Create a 'savedTrainingSession' instance that has the definitive ID.
    val savedTrainingSession = trainingSession.copy(id = definitiveSessionId)

    // 2. Delete ALL existing pisto states for this training session ID.
    //    *** THIS WAS THE MISSING STEP IN YOUR PREVIOUS VERSION ***
    deletePistoStatesForSession(definitiveSessionId)

    // 3. Insert the new/current pisto states, linking them to the definitiveSessionId.
    if (pistoStates.isNotEmpty()) {
      val updatedPistoStates = pistoStates.map {
        it.copy(trainingSessionId = definitiveSessionId) // Ensure FK is set
      }
      upsertPistoStates(updatedPistoStates) // Assuming upsertPistoStates handles List
    }

    // Return the session with the correct ID
    return savedTrainingSession
  }
  // Internal upsert operations
  @Upsert
  suspend fun upsertTrainingSession(trainingSession: TrainingSession): Long

  @Upsert
  suspend fun upsertPistoStates(pistoStates: List<PistoStateEntity>)
  // ----> THIS IS THE NEW METHOD YOU NEED <----
  @Query("DELETE FROM pisto_states WHERE trainingSessionId = :sessionId")
  suspend fun deletePistoStatesForSession(sessionId: Long)


}
