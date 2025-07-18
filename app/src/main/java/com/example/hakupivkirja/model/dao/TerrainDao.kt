package com.example.hakupivkirja.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.hakupivkirja.model.Terrain

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
}