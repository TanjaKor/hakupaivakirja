package com.tanjan.hakupivkirja.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.tanjan.hakupivkirja.model.Terrain

@Dao
interface TerrainDao {
  @Query("SELECT * FROM terrain_details WHERE trainingSessionId = :sessionId LIMIT 1")
  suspend fun getTerrainBySessionId(sessionId: Long): Terrain?

  @Upsert
  suspend fun upsertTerrain(terrain: Terrain): Long

  @Update
  suspend fun updateTerrain(terrain: Terrain)

  @Delete
  suspend fun deleteTerrain(terrain: Terrain)

  // Get all terrain data for sessions in a specific year
  @Query("SELECT * FROM terrain_details WHERE trainingSessionId IN (:sessionIds)")
  suspend fun getTerrainForSessions(sessionIds: List<Long>): List<Terrain>
}