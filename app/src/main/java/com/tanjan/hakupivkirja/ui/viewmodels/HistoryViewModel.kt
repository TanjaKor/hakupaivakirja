package com.tanjan.hakupivkirja.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanjan.hakupivkirja.model.repository.HakupivkirjaRepository
import com.tanjan.hakupivkirja.model.repository.YearlyTrainingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar


// UI State matching your HistoryScreen structure
data class HistoryUiState(
  val year: Int = Calendar.getInstance().get(Calendar.YEAR),
  val yearlyData: YearlyTrainingData? = null,
  val isLoading: Boolean = false,
  val error: String? = null
)

class HistoryViewModel(
  private val repository: HakupivkirjaRepository
) : ViewModel() {

  private val TAG = "HistoryViewModel"
  private val _uiState = MutableStateFlow(HistoryUiState())
  val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

  init {
    loadCurrentYearData()
  }

  fun loadCurrentYearData() {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    loadYearData(currentYear)
  }

  private fun loadYearData(year: Int) {
    viewModelScope.launch { // 3. Lisää lokitietoja metodin alkuun
      Log.d(TAG, "Aloitetaan vuoden $year tietojen lataus...")

      _uiState.value = _uiState.value.copy(isLoading = true, error = null)

      try {
        val yearlyData = repository.getYearlyTrainingData(year)
        Log.d(TAG, "Tiedot haettu onnistuneesti vuodelle $year. Data: $yearlyData")
        _uiState.value = uiState.value.copy(
          year = year,
          yearlyData = yearlyData,
          isLoading = false,
          error = null
        )
      } catch (e: Exception) {
        Log.e(TAG, "Tietojen lataus epäonnistui", e)
        _uiState.value = _uiState.value.copy(
          isLoading = false,
          error = "Failed to load data: ${e.message}"
        )
      }
    }
  }

  // ========== TRAINING SUMMARY FUNCTIONS ==========

  fun getTrainingSummary(): List<Pair<String, String>> {
    val data = _uiState.value.yearlyData ?: return emptyList()
    val currentDate = Calendar.getInstance()
    val currentMonth = currentDate.get(Calendar.MONTH) + 1


    val avgPerMonth = if (data.totalTrainings > 0 && currentMonth > 0) {
      data.totalTrainings / currentMonth
    } else 0

    val thisMonth = if (data.totalTrainings > 0 ) {
      // Filter the sessions already in the state to get the count for the current month
      val sessionsThisYear = _uiState.value.yearlyData?.sessions ?: emptyList()
      sessionsThisYear.count { session ->
        val sessionCalendar = Calendar.getInstance().apply {
          timeInMillis = session.dateMillis
        }
        sessionCalendar.get(Calendar.MONTH) == currentMonth - 1 &&
            sessionCalendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)
      }
    } else 0


    return listOf(
      "Yhteensä" to "${data.totalTrainings} kpl",
      "Keskiarvo/kk" to "$avgPerMonth kpl",
      "Tässä kuussa" to "$thisMonth kpl"
    )
  }

  fun getDifficultyDistribution(): List<Pair<String, String>> {
    val sessions = _uiState.value.yearlyData?.sessions ?: return emptyList()

    // Count how many trainings have each difficulty rating (1-5)
    // This uses TrainingSession.difficultyRating field
    return (1..5).map { rating ->
      val count = sessions.count { it.difficultyRating == rating }
      rating.toString() to count.toString()
    }
  }

  fun getAverageDifficulty(): String {
    return _uiState.value.yearlyData?.statistics?.averageDifficulty?.let {
      String.format("%.1f", it)
    } ?: "0.0"
  }

  fun getTotalTrainings(): Int {
    return _uiState.value.yearlyData?.totalTrainings ?: 0
  }

  // ========== TERRAIN DISTRIBUTION FUNCTIONS ==========

  /**
   * Forest Thickness (Maaston peittävyys)
   * Returns distribution: [("1", "3"), ("2", "5"), ("3", "2"), ("4", "1"), ("5", "0")]
   * Meaning: 3 trainings had forestThickness=1, 5 had forestThickness=2, etc.
   */
  fun getForestThicknessDistribution(): List<Pair<String, String>> {
    val terrainData = _uiState.value.yearlyData?.terrainData ?: return emptyList()

    return (1..5).map { level ->
      val count = terrainData.count { it.forestThickness == level }
      level.toString() to count.toString()
    }
  }

  /**
   * Altitude Changes (Maaston korkeuserot)
   * Returns distribution of altitudeChanges values (1-5)
   */
  fun getAltitudeChangesDistribution(): List<Pair<String, String>> {
    val terrainData = _uiState.value.yearlyData?.terrainData ?: return emptyList()

    return (1..5).map { level ->
      val count = terrainData.count { it.altitudeChanges == level }
      level.toString() to count.toString()
    }
  }

  /**
   * Moisture Level (Maaston kuivuus - but inverted!)
   * Lower moisture = drier, so we might want to invert this for "kuivuus"
   * Or just show moisture as-is
   */
  fun getMoistureLevelDistribution(): List<Pair<String, String>> {
    val terrainData = _uiState.value.yearlyData?.terrainData ?: return emptyList()

    return (1..5).map { level ->
      val count = terrainData.count { it.moistureLevel == level }
      level.toString() to count.toString()
    }
  }

  /**
   * Overall terrain difficulty average
   * This could be calculated as the average of all three terrain metrics
   */
  fun getTerrainOverallAverage(): String {
    val terrainData = _uiState.value.yearlyData?.terrainData ?: return "0.0"

    if (terrainData.isEmpty()) return "0.0"

    // Calculate average across all three metrics for each terrain entry
    val overallScores = terrainData.mapNotNull { terrain ->
      val values = listOfNotNull(
        terrain.forestThickness,
        terrain.altitudeChanges,
        terrain.moistureLevel
      )
      if (values.isNotEmpty()) values.average() else null
    }

    return if (overallScores.isNotEmpty()) {
      String.format("%.1f", overallScores.average())
    } else "0.0"
  }

  // ========== WEATHER FUNCTIONS ==========

  fun getAverageTemperature(): String {
    return _uiState.value.yearlyData?.statistics?.averageTemperature?.let {
      String.format("%.0f", it) // Round to whole number
    } ?: "0"
  }

  /**
   * Temperature ranges (Lämpötilat)
   * Groups weather data into temperature buckets
   */
  fun getTemperatureRanges(): List<Pair<String, String>> {
    val weatherData = _uiState.value.yearlyData?.weatherData ?: return emptyList()

    val ranges = listOf(
      "0-10°C" to weatherData.count {
        val temp = it.temperatureCelsius ?: return@count false
        temp in 0.0..10.0
      },
      "10-20°C" to weatherData.count {
        val temp = it.temperatureCelsius ?: return@count false
        temp in 10.0..20.0
      },
      "20-30°C" to weatherData.count {
        val temp = it.temperatureCelsius ?: return@count false
        temp in 20.0..30.0
      }
    )

    return ranges.map { (label, count) -> label to count.toString() }
  }

  /**
   * Weather conditions (Sääolosuhteet)
   * Returns the actual weather descriptions from database
   */
  fun getWeatherConditions(): List<Pair<String, String>> {
    val stats = _uiState.value.yearlyData?.statistics ?: return emptyList()

    // Convert the weather conditions map to list of pairs
    // Map contains: "clear sky" -> 5, "few clouds" -> 3, etc.
    return stats.weatherConditions
      .map { (condition, count) ->
        // Capitalize first letter of each word for display
        val displayCondition = condition.split(" ")
          .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        displayCondition to count.toString()
      }
      .sortedByDescending { it.second.toInt() } // Sort by count, most common first
      .take(5) // Only show top 5 conditions
  }
}