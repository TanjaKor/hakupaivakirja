package com.example.hakupivkirja.model.repository

import android.util.Log
import com.example.hakupivkirja.model.PistoStateEntity
import com.example.hakupivkirja.model.Terrain
import com.example.hakupivkirja.model.TrainingSession
import com.example.hakupivkirja.model.dao.TerrainDao
import com.example.hakupivkirja.model.dao.TrainingSessionDao
import com.example.hakupivkirja.model.dao.WeatherDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HakupivkirjaRepositoryImpl(
  private val trainingSessionDao: TrainingSessionDao,
  private val terrainDao: TerrainDao,
  private val weatherDao: WeatherDao
) : HakupivkirjaRepository {

  override suspend fun saveTrainingSession(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>
  ): TrainingSession {
    return trainingSessionDao.saveTrainingSession(trainingSession, pistoStates)
  }

  override suspend fun saveTrainingSessionWithTerrain(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>,
    terrain: Terrain?
  ): Pair<TrainingSession, Terrain?> {
    return withContext(Dispatchers.IO) {
      // Save session and get the ID
      val savedSession = trainingSessionDao.saveTrainingSession(trainingSession, pistoStates)
      val sessionId = savedSession.id

      if (sessionId == 0L) {
        Log.e("Repository", "Failed to save training session")
        return@withContext Pair(savedSession, null)
      }

      // Clear any existing terrain for this session first
      terrainDao.getTerrainBySessionId(sessionId)?.let { existingTerrain ->
        terrainDao.deleteTerrain(existingTerrain)
      }

      // Save new terrain if provided
      val savedTerrain = terrain?.let { newTerrain ->
        val terrainToSave = newTerrain.copy(
          id = 0L, // Force new terrain (since we deleted the old one)
          trainingSessionId = sessionId
        )
        val newTerrainId = terrainDao.upsertTerrain(terrainToSave)
        terrainToSave.copy(id = newTerrainId)
      }

      Pair(savedSession, savedTerrain)
    }
  }
}