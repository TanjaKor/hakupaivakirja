package com.tanjan.hakupivkirja.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanjan.hakupivkirja.model.repository.HakupivkirjaRepository
import com.tanjan.hakupivkirja.model.repository.YearlyTrainingData
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt


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

  private var observationJob: Job? = null

  init {
    loadCurrentYearData()
  }

  fun loadCurrentYearData() {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    loadYearData(currentYear)
  }

  private fun loadYearData(year: Int) {
    observationJob?.cancel()
    
    observationJob = viewModelScope.launch {
      Log.d(TAG, "Aloitetaan vuoden $year tietojen seuranta...")

      _uiState.update { it.copy(isLoading = true, error = null) }

      try {
        repository.getTrainingSessionsByYear(year).collect {
          try {
            val yearlyData = repository.getYearlyTrainingData(year)
            _uiState.update { currentState ->
              currentState.copy(
                year = year,
                yearlyData = yearlyData,
                isLoading = false,
                error = null
              )
            }
          } catch (e: Exception) {
            Log.e(TAG, "Yhteenvedon laskenta epäonnistui", e)
            _uiState.update { it.copy(isLoading = false, error = "Laskenta epäonnistui: ${e.message}") }
          }
        }
      } catch (e: Exception) {
        Log.e(TAG, "Tietojen seuranta epäonnistui", e)
        _uiState.update { it.copy(isLoading = false, error = "Seuranta epäonnistui: ${e.message}") }
      }
    }
  }

  // ========== TRAINING SUMMARY FUNCTIONS ==========

  fun getTrainingSummary(): List<Pair<String, String>> {
    val data = _uiState.value.yearlyData ?: return emptyList()
    val currentDate = Calendar.getInstance()
    val currentYear = currentDate.get(Calendar.YEAR)
    val currentMonthNow = currentDate.get(Calendar.MONTH) + 1
    val viewingYear = _uiState.value.year

    val cal = Calendar.getInstance()
    val latestMonthInSessions = data.sessions.map {
      cal.timeInMillis = it.dateMillis
      cal.get(Calendar.MONTH) + 1
    }.maxOrNull() ?: 1

    val divisor = when {
        viewingYear < currentYear -> 12.0
        viewingYear == currentYear -> maxOf(currentMonthNow, latestMonthInSessions).toDouble()
        else -> latestMonthInSessions.toDouble()
    }

    val avgPerMonth = if (data.totalTrainings > 0) {
      String.format(Locale.getDefault(), "%.1f", data.totalTrainings.toDouble() / divisor)
    } else "0.0"

    val thisMonthCount = if (viewingYear == currentYear) {
      val sessions = data.sessions
      sessions.count { session ->
        val sessionCalendar = Calendar.getInstance().apply {
          timeInMillis = session.dateMillis
        }
        sessionCalendar.get(Calendar.MONTH) == currentMonthNow - 1 &&
            sessionCalendar.get(Calendar.YEAR) == currentYear
      }
    } else 0


    return listOf(
      "Yhteensä" to "${data.totalTrainings} kpl",
      "Keskiarvo/kk" to "$avgPerMonth kpl",
      "Tässä kuussa" to "$thisMonthCount kpl"
    )
  }

  fun getDifficultyDistribution(): List<Pair<String, String>> {
    val sessions = _uiState.value.yearlyData?.sessions ?: return emptyList()
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

  // ========== TRACK & PISTO STATISTICS ==========

  fun getTrackLengthDistribution(): List<Pair<String, String>> {
    val stats = _uiState.value.yearlyData?.statistics ?: return emptyList()
    return stats.trackLengthDistribution.map { (length, count) ->
      length to count.toString()
    }.sortedBy { it.first }
  }

  fun getAverageTrackLength(): String {
    return _uiState.value.yearlyData?.statistics?.averageTrackLength?.let {
      String.format("%.0f m", it)
    } ?: "0 m"
  }

  fun getPistoAmountDistribution(): List<Pair<String, String>> {
    val stats = _uiState.value.yearlyData?.statistics ?: return emptyList()
    return stats.pistoAmountDistribution.map { (range, count) ->
      range to count.toString()
    }
  }

  fun getTyhjaTrainingStats(): List<Pair<String, String>> {
    val data = _uiState.value.yearlyData ?: return emptyList()
    val count = data.statistics.tyhjaTrainingCount
    val percentage = if (data.totalTrainings > 0) {
      (count.toDouble() / data.totalTrainings * 100).toInt()
    } else 0
    
    return listOf(
      "Tyhjiä sisältävät" to "$count",
      "Osuus" to "$percentage %"
    )
  }

  // ========== TERRAIN DISTRIBUTION FUNCTIONS (1-3 scale) ==========

  /**
   * Laskee maaston yleisen haastavuuden jakauman (1-3)
   * perustuen peittävyyden, korkeuserojen ja kuivuuden keskiarvoon.
   */
  fun getTerrainOverallDistribution(): List<Pair<String, String>> {
    val terrainData = _uiState.value.yearlyData?.terrainData ?: return emptyList()
    
    // Lasketaan jokaiselle treenille keskiarvo (pyöristettynä lähimpään kokonaislukuun 1-3)
    val averages = terrainData.mapNotNull { terrain ->
      val values = listOfNotNull(
        terrain.forestThickness?.toDouble(),
        terrain.altitudeChanges?.toDouble(),
        terrain.moistureLevel?.toDouble()
      )
      if (values.isNotEmpty()) values.average().roundToInt() else null
    }

    return (1..3).map { level ->
      val count = averages.count { it == level }
      level.toString() to count.toString()
    }
  }

  fun getForestThicknessDistribution(): List<Pair<String, String>> {
    val terrainData = _uiState.value.yearlyData?.terrainData ?: return emptyList()
    return (1..3).map { level ->
      val count = terrainData.count { it.forestThickness == level }
      level.toString() to count.toString()
    }
  }

  fun getAltitudeChangesDistribution(): List<Pair<String, String>> {
    val terrainData = _uiState.value.yearlyData?.terrainData ?: return emptyList()
    return (1..3).map { level ->
      val count = terrainData.count { it.altitudeChanges == level }
      level.toString() to count.toString()
    }
  }

  fun getMoistureLevelDistribution(): List<Pair<String, String>> {
    val terrainData = _uiState.value.yearlyData?.terrainData ?: return emptyList()
    return (1..3).map { level ->
      val count = terrainData.count { it.moistureLevel == level }
      level.toString() to count.toString()
    }
  }

  fun getTerrainOverallAverage(): String {
    val terrainData = _uiState.value.yearlyData?.terrainData ?: return "0.0"
    if (terrainData.isEmpty()) return "0.0"

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
      String.format("%.0f", it)
    } ?: "0"
  }

  fun getTemperatureRanges(): List<Pair<String, String>> {
    val weatherData = _uiState.value.yearlyData?.weatherData ?: return emptyList()
    val ranges = listOf(
      "-10-0°C" to weatherData.count {
        val temp = it.temperatureCelsius ?: return@count false
        temp in -10.0..0.0
      },
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

  fun getWeatherConditions(): List<Pair<String, String>> {
    val stats = _uiState.value.yearlyData?.statistics ?: return emptyList()
    return stats.weatherConditions
      .map { (condition, count) ->
        val displayCondition = condition.split(" ")
          .joinToString(" ") { it.replaceFirstChar { it.uppercase() } }
        displayCondition to count.toString()
      }
      .sortedByDescending { it.second.toInt() }
      .take(5)
  }
}
