package com.example.hakupivkirja.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hakupivkirja.model.TrainingSession
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingSessionDao {
  @Query("SELECT * FROM training_sessions")
  fun getAllTrainingSessions(): Flow<List<TrainingSession>>

  @Query("SELECT * FROM training_sessions WHERE id = :id")
  fun getTrainingSessionById(id: Long): Flow<TrainingSession?>

  @Insert
  suspend fun insertTrainingSession(session: TrainingSession): Long

  @Update
  suspend fun updateTrainingSession(session: TrainingSession)

  @Delete
  suspend fun deleteTrainingSession(session: TrainingSession)
}