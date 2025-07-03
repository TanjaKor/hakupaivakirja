package com.example.hakupivkirja.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hakupivkirja.model.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
  @Query("SELECT * FROM tracks WHERE trainingSessionId = :trainingSessionId")
  fun getTracksBySessionId(trainingSessionId: Long): Flow<List<TrackEntity>>

  @Insert
  suspend fun insertTrack(track: TrackEntity): Long

  @Update
  suspend fun updateTrack(track: TrackEntity)

  @Delete
  suspend fun deleteTrack(track: TrackEntity)
}