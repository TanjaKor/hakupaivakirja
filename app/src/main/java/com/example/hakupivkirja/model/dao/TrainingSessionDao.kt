package com.example.hakupivkirja.model.dao

import androidx.room.Dao
import androidx.room.Query
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
