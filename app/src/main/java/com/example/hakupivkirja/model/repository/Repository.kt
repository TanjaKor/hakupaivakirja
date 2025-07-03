package com.example.hakupivkirja.model.repository

import com.example.hakupivkirja.model.PistoStateEntity
import com.example.hakupivkirja.model.Terrain
import com.example.hakupivkirja.model.TrackEntity
import com.example.hakupivkirja.model.TrainingSession
import com.example.hakupivkirja.model.Weather
import com.example.hakupivkirja.model.dao.PistoDao
import com.example.hakupivkirja.model.dao.TerrainDao
import com.example.hakupivkirja.model.dao.TrackDao
import com.example.hakupivkirja.model.dao.TrainingSessionDao
import com.example.hakupivkirja.model.dao.WeatherDao
import kotlinx.coroutines.flow.Flow

class HakupivkirjaRepositoryImpl(
  private val trackDao: TrackDao,
  private val pistoDao: PistoDao,
  private val trainingSessionDao: TrainingSessionDao,
  private val weatherDao: WeatherDao,
  private val terrainDao: TerrainDao
) : HakupivkirjaRepository {

  // TrainingSession operations
  override fun getAllTrainingSessions(): Flow<List<TrainingSession>> =
    trainingSessionDao.getAllTrainingSessions()

  override fun getTrainingSessionById(id: Long): Flow<TrainingSession?> =
    trainingSessionDao.getTrainingSessionById(id)

  override suspend fun insertTrainingSession(session: TrainingSession): Long =
    trainingSessionDao.insertTrainingSession(session)

  override suspend fun updateTrainingSession(session: TrainingSession) =
    trainingSessionDao.updateTrainingSession(session)

  override suspend fun deleteTrainingSession(session: TrainingSession) =
    trainingSessionDao.deleteTrainingSession(session)

  // Track operations
  override fun getTracksBySessionId(sessionId: Long): Flow<List<TrackEntity>> =
    trackDao.getTracksBySessionId(sessionId)

  override suspend fun updateTrack(track: TrackEntity) =
    trackDao.updateTrack(track)

  override suspend fun deleteTrack(track: TrackEntity) =
    trackDao.deleteTrack(track)

  // PistoState operations
  override fun getPistoStatesByTrackId(trackId: Long): Flow<List<PistoStateEntity>> =
    pistoDao.getPistoStatesByTrackId(trackId)

  // Remove the individual insertPistoState method since you only work with lists
  override suspend fun insertAllPistoStates(pistoStates: List<PistoStateEntity>) =
    pistoDao.insertAllPistoStates(pistoStates)

  // Combined operations - this is your main use case
  override suspend fun saveTrackWithPistoStates(track: TrackEntity, pistoStates: List<PistoStateEntity>) {
    val trackId = trackDao.insertTrack(track)
    val pistoStatesWithTrackId = pistoStates.map { it.copy(trackId = trackId) }
    pistoDao.insertAllPistoStates(pistoStatesWithTrackId)
  }

  // Weather and Terrain
  override fun getWeatherBySessionId(sessionId: Long): Flow<Weather?> =
    weatherDao.getWeatherBySessionId(sessionId)

  override fun getTerrainBySessionId(sessionId: Long): Flow<Terrain?> =
    terrainDao.getTerrainBySessionId(sessionId)

  override suspend fun insertWeather(weather: Weather) =
    weatherDao.insertWeather(weather)

  override suspend fun insertTerrain(terrain: Terrain) =
    terrainDao.insertTerrain(terrain)
}