package com.example.hakupivkirja.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hakupivkirja.model.Terrain
import kotlinx.coroutines.flow.Flow

@Dao
interface TerrainDao {
  @Query("SELECT * FROM terrain_details WHERE trainingSessionId = :sessionId")
  fun getTerrainBySessionId(sessionId: Long): Flow<Terrain?>

  @Insert
  suspend fun insertTerrain(terrain: Terrain)

  @Update
  suspend fun updateTerrain(terrain: Terrain)

  @Delete
  suspend fun deleteTerrain(terrain: Terrain)
}