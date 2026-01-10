package com.tanjan.hakupivkirja.model.repository

import android.util.Log
import com.tanjan.hakupivkirja.model.PistoStateEntity
import com.tanjan.hakupivkirja.model.Terrain
import com.tanjan.hakupivkirja.model.TrainingSession
import com.tanjan.hakupivkirja.model.TrainingSessionWithPistoStates
import com.tanjan.hakupivkirja.model.WeatherEntity
import com.tanjan.hakupivkirja.model.dao.TerrainDao
import com.tanjan.hakupivkirja.model.dao.TrainingSessionDao
import com.tanjan.hakupivkirja.model.dao.WeatherDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class HakupivkirjaRepositoryImpl(
  private val trainingSessionDao: TrainingSessionDao,
  private val terrainDao: TerrainDao,
  private val weatherDao: WeatherDao
) : HakupivkirjaRepository {

  private val TAG = "HakupivakirjaRepo"

  // ========== FETCH OPERATIONS ==========

  override fun getAllTrainingSessions(): Flow<List<TrainingSession>> {
    return trainingSessionDao.getAllTrainingSessions()
  }

  override suspend fun getTrainingSessionById(sessionId: Long): TrainingSession? {
    return withContext(Dispatchers.IO) {
      trainingSessionDao.getTrainingSessionById(sessionId)
    }
  }

    override suspend fun getCompleteTrainingSession(sessionId: Long): CompleteTrainingSessionData? {
    return withContext(Dispatchers.IO) {
      val sessionWithPistos = trainingSessionDao.getTrainingSessionWithPistoStates(sessionId)

      if (sessionWithPistos == null) {
        return@withContext null
      }

      val terrain = terrainDao.getTerrainBySessionId(sessionId)
      val weather = weatherDao.getWeatherBySessionId(sessionId)

      CompleteTrainingSessionData(
        session = sessionWithPistos.trainingSession,
        pistoStates = sessionWithPistos.pistoStates,
        terrain = terrain,
        weather = weather
      )
    }
  }

  override fun getAllTrainingSessionsWithPistoStates(): Flow<List<TrainingSessionWithPistoStates>> {
    return trainingSessionDao.getAllTrainingSessionsWithPistoStates()
  }

  override suspend fun getTerrainBySessionId(sessionId: Long): Terrain? {
    return withContext(Dispatchers.IO) {
      terrainDao.getTerrainBySessionId(sessionId)
    }
  }

  override suspend fun getWeatherBySessionId(sessionId: Long): WeatherEntity? {
    return withContext(Dispatchers.IO) {
      weatherDao.getWeatherBySessionId(sessionId)
    }
  }

  // ========== YEARLY STATISTICS ==========

  override fun getTrainingSessionsByYear(year: Int): Flow<List<TrainingSession>> {
    val (startMillis, endMillis) = getYearRange(year)
    return trainingSessionDao.getTrainingSessionsByYear(startMillis, endMillis)
  }

  override suspend fun getYearlyTrainingData(year: Int): YearlyTrainingData {
    Log.d(TAG, "getYearlyTrainingData: Aloitetaan datan haku vuodelle $year")
    return withContext(Dispatchers.IO) {
      val (startMillis, endMillis) = getYearRange(year)

      // Get sessions first
      val sessions = trainingSessionDao.getTrainingSessionsByYear(startMillis, endMillis).first()
      val totalTrainings = sessions.size

      if (totalTrainings == 0) {
        return@withContext YearlyTrainingData(
          year = year,
          totalTrainings = 0,
          sessions = emptyList(),
          terrainData = emptyList(),
          weatherData = emptyList(),
          pistoData = emptyList(),
          statistics = YearlyStatistics(
            averageDifficulty = null,
            averageRating = null,
            averageTemperature = null,
            terrainDistribution = TerrainDistribution(0.0, 0.0, 0.0),
            weatherConditions = emptyMap(),
            monthlyBreakdown = emptyMap(),
            trackLengthDistribution = emptyMap(),
            averageTrackLength = null,
            pistoAmountDistribution = emptyMap(),
            tyhjaTrainingCount = 0
          )
        )
      }

      // Get session IDs
      val sessionIds = sessions.map { it.id }

      // Get terrain, weather and pisto data
      val terrainData = terrainDao.getTerrainForSessions(sessionIds)
      val weatherData = weatherDao.getWeatherForSessions(sessionIds)
      val pistoData = trainingSessionDao.getPistoStatesForSessions(sessionIds)

      // Calculate statistics
      val statistics = calculateYearlyStatistics(sessions, terrainData, weatherData, pistoData)

      YearlyTrainingData(
        year = year,
        totalTrainings = totalTrainings,
        sessions = sessions,
        terrainData = terrainData,
        weatherData = weatherData,
        pistoData = pistoData,
        statistics = statistics
      )
    }
  }

  private fun getYearRange(year: Int): Pair<Long, Long> {
    val calendar = java.util.Calendar.getInstance()
    calendar.set(year, 0, 1, 0, 0, 0) // January 1st
    calendar.set(java.util.Calendar.MILLISECOND, 0)
    val startMillis = calendar.timeInMillis

    calendar.set(year, 11, 31, 23, 59, 59) // December 31st
    calendar.set(java.util.Calendar.MILLISECOND, 999)
    val endMillis = calendar.timeInMillis

    return Pair(startMillis, endMillis)
  }

  private fun calculateYearlyStatistics(
    sessions: List<TrainingSession>,
    terrainData: List<Terrain>,
    weatherData: List<WeatherEntity>,
    pistoData: List<PistoStateEntity>
  ): YearlyStatistics {
    // Calculate average difficulty
    val difficulties = sessions.mapNotNull { it.difficultyRating }
    val averageDifficulty = if (difficulties.isNotEmpty()) {
      difficulties.average()
    } else null

    // Calculate average rating
    val ratings = sessions.mapNotNull { it.overallRating }
    val averageRating = if (ratings.isNotEmpty()) {
      ratings.average()
    } else null

    // Calculate average temperature
    val temperatures = weatherData.mapNotNull { it.temperatureCelsius }
    val averageTemperature = if (temperatures.isNotEmpty()) {
      temperatures.average()
    } else null

    // Terrain distribution
    val terrainDistribution = if (terrainData.isNotEmpty()) {
      val validThicknessValues = terrainData.map { it.forestThickness }.filterNotNull()
      val validMoistureValues = terrainData.map { it.moistureLevel }.filterNotNull()
      val validAltitudeValues = terrainData.map { it.altitudeChanges }.filterNotNull()
      TerrainDistribution(
        averageForestThickness = if (validThicknessValues.isNotEmpty()) validThicknessValues.average() else 0.0,
        averageMoistureLevel = if (validMoistureValues.isNotEmpty()) validMoistureValues.average() else 0.0,
        averageAltitudeChanges = if (validAltitudeValues.isNotEmpty()) validAltitudeValues.average() else 0.0
      )
    } else {
      TerrainDistribution(0.0, 0.0, 0.0)
    }

    // Weather conditions count
    val weatherConditions = weatherData
      .mapNotNull { it.weatherDescription }
      .groupingBy { it }
      .eachCount()

    // Monthly breakdown
    val calendar = java.util.Calendar.getInstance()
    val monthlyBreakdown = sessions
      .groupBy { session ->
        calendar.timeInMillis = session.dateMillis
        calendar.get(java.util.Calendar.MONTH) + 1 // 1-based month
      }
      .mapValues { it.value.size }

    // --- TRACK LENGTH STATISTICS ---
    val trackLengths = sessions.map { it.trackLength.filter { char -> char.isDigit() }.toDoubleOrNull() ?: 0.0 }
    val averageTrackLength = if (trackLengths.isNotEmpty()) trackLengths.average() else null
    val trackLengthDistribution = sessions
      .groupingBy { it.trackLength }
      .eachCount()

    // --- PISTO AMOUNT STATISTICS ---
    val pistosBySession = pistoData.groupBy { it.trainingSessionId }
    val pistoAmounts = sessions.map { session -> 
      pistosBySession[session.id]?.size ?: 0 
    }
    
    val pistoAmountDistribution = mapOf(
      "1-4" to pistoAmounts.count { it in 1..4 },
      "5-7" to pistoAmounts.count { it in 5..7 },
      "8+" to pistoAmounts.count { it >= 8 }
    )

    // --- TYHJÄ STATISTICS ---
    val tyhjaTrainingCount = sessions.count { session ->
      val sessionPistos = pistosBySession[session.id] ?: emptyList()
      sessionPistos.any { it.type == "Tyhja" }
    }

    return YearlyStatistics(
      averageDifficulty = averageDifficulty,
      averageRating = averageRating,
      averageTemperature = averageTemperature,
      terrainDistribution = terrainDistribution,
      weatherConditions = weatherConditions,
      monthlyBreakdown = monthlyBreakdown,
      trackLengthDistribution = trackLengthDistribution,
      averageTrackLength = averageTrackLength,
      pistoAmountDistribution = pistoAmountDistribution,
      tyhjaTrainingCount = tyhjaTrainingCount
    )
  }


  override suspend fun saveTrainingSession(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>
  ): TrainingSession {
    return trainingSessionDao.saveTrainingSession(trainingSession, pistoStates)
  }

  override suspend fun saveTrainingSessionWithTerrain(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>,
    terrain: Terrain?,
    weather: WeatherEntity?
  ): Triple<TrainingSession, Terrain?, WeatherEntity?> {
    return withContext(Dispatchers.IO) {
      // Save session and get the ID
      val savedSession = trainingSessionDao.saveTrainingSession(trainingSession, pistoStates)
      val sessionId = savedSession.id

      if (sessionId == 0L) {
        Log.e("Repository", "Failed to save training session")
        return@withContext Triple(savedSession, null, null)
      }

      // Clear any existing terrain for this session first
      terrainDao.getTerrainBySessionId(sessionId)?.let { existingTerrain ->
        terrainDao.deleteTerrain(existingTerrain)
      }

      // Clear any existing weather for this session first
      weatherDao.getWeatherBySessionId(sessionId)?.let { existingWeather ->
        weatherDao.deleteWeather(existingWeather)
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

      // Save new weather if provided
      val savedWeather = weather?.let { newWeather ->
        val weatherToSave = newWeather.copy(
          id = 0L, // Force new weather (since we deleted the old one)
          trainingSessionId = sessionId
        )
        val newWeatherId = weatherDao.upsertWeather(weatherToSave)
        weatherToSave.copy(id = newWeatherId)
      }


      Triple(savedSession, savedTerrain, savedWeather)
    }
  }
}