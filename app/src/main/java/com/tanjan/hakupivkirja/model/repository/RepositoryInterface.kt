package com.tanjan.hakupivkirja.model.repository

import com.tanjan.hakupivkirja.model.PistoStateEntity
import com.tanjan.hakupivkirja.model.Terrain
import com.tanjan.hakupivkirja.model.TrainingSession
import com.tanjan.hakupivkirja.model.TrainingSessionWithPistoStates
import com.tanjan.hakupivkirja.model.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface HakupivkirjaRepository {

  fun getAllTrainingSessions(): Flow<List<TrainingSession>>

  // Get a single training session by ID
  suspend fun getTrainingSessionById(sessionId: Long): TrainingSession?

  // Get all training sessions with their pisto states
  fun getAllTrainingSessionsWithPistoStates(): Flow<List<TrainingSessionWithPistoStates>>

  // Get training session with all related data (pisto states, terrain, weather)
  suspend fun getCompleteTrainingSession(sessionId: Long): CompleteTrainingSessionData?

  // Get terrain for a session
  suspend fun getTerrainBySessionId(sessionId: Long): Terrain?

  // Get weather for a session
  suspend fun getWeatherBySessionId(sessionId: Long): WeatherEntity?

  // Get training sessions for a specific year
  fun getTrainingSessionsByYear(year: Int): Flow<List<TrainingSession>>

  // Get complete yearly training data with terrain and weather
  suspend fun getYearlyTrainingData(year: Int): YearlyTrainingData

//    // PistoState operations
//  suspend fun insertAllPistoStates(pistoStates: List<PistoStateEntity>)

  suspend fun saveTrainingSession(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>
  ): TrainingSession

  suspend fun saveTrainingSessionWithTerrain(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>,
    terrain: Terrain?,
    weather: WeatherEntity?
  ):  Triple<TrainingSession, Terrain?, WeatherEntity?>
}

// Data class to hold complete training session data
data class CompleteTrainingSessionData(
  val session: TrainingSession,
  val pistoStates: List<PistoStateEntity>,
  val terrain: Terrain?,
  val weather: WeatherEntity?
)

// Data class to hold yearly training statistics
data class YearlyTrainingData(
  val year: Int,
  val totalTrainings: Int,
  val sessions: List<TrainingSession>,
  val terrainData: List<Terrain>,
  val weatherData: List<WeatherEntity>,
  val pistoData: List<PistoStateEntity>,
  val statistics: YearlyStatistics
)

data class YearlyStatistics(
  val averageDifficulty: Double?,
  val averageRating: Double?,
  val averageTemperature: Double?,
  val terrainDistribution: TerrainDistribution,
  val weatherConditions: Map<String, Int>, // Weather description to count
  val monthlyBreakdown: Map<Int, Int>, // Month (1-12) to training count
  val trackLengthDistribution: Map<String, Int>,
  val averageTrackLength: Double?,
  val pistoAmountDistribution: Map<String, Int>, // Ranges: "1-4", "5-7", "8+"
  val tyhjaTrainingCount: Int // How many trainings had at least one "Tyhja" pisto
)

data class TerrainDistribution(
  val averageForestThickness: Double,
  val averageMoistureLevel: Double,
  val averageAltitudeChanges: Double
)