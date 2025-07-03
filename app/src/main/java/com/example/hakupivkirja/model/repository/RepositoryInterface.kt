package com.example.hakupivkirja.model.repository

import com.example.hakupivkirja.model.PistoStateEntity
import com.example.hakupivkirja.model.Terrain
import com.example.hakupivkirja.model.TrackEntity
import com.example.hakupivkirja.model.TrainingSession
import com.example.hakupivkirja.model.Weather
import kotlinx.coroutines.flow.Flow

interface HakupivkirjaRepository {
  // TrainingSession operations
  fun getAllTrainingSessions(): Flow<List<TrainingSession>>
  fun getTrainingSessionById(id: Long): Flow<TrainingSession?>
  suspend fun insertTrainingSession(session: TrainingSession): Long
  suspend fun updateTrainingSession(session: TrainingSession)
  suspend fun deleteTrainingSession(session: TrainingSession)

  // Track operations
  fun getTracksBySessionId(sessionId: Long): Flow<List<TrackEntity>>
  suspend fun updateTrack(track: TrackEntity)
  suspend fun deleteTrack(track: TrackEntity)

  // PistoState operations
  fun getPistoStatesByTrackId(trackId: Long): Flow<List<PistoStateEntity>>
  suspend fun insertAllPistoStates(pistoStates: List<PistoStateEntity>)

  // Combined operations
  suspend fun saveTrackWithPistoStates(track: TrackEntity, pistoStates: List<PistoStateEntity>)

  // Weather and Terrain
  fun getWeatherBySessionId(sessionId: Long): Flow<Weather?>
  fun getTerrainBySessionId(sessionId: Long): Flow<Terrain?>
  suspend fun insertWeather(weather: Weather)
  suspend fun insertTerrain(terrain: Terrain)
}