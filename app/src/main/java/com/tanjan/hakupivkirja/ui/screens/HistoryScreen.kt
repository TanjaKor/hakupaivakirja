package com.tanjan.hakupivkirja.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanjan.hakupivkirja.ui.theme.primaryDark
import com.tanjan.hakupivkirja.ui.theme.primaryLight
import com.tanjan.hakupivkirja.ui.theme.secondaryLight
import com.tanjan.hakupivkirja.ui.viewmodels.HistoryViewModel


// Keep your existing data classes
data class Section(
  val id: String,
  val title: String,
  val summary: String,
  val data: List<Pair<String, String>>? = null,
  val colors: Pair<Color, Color>,
  val bgColor: Color,
  val icon: ImageVector,
  val challengeContent: ChallengeData? = null,
  val temperatureContent: TemperatureData? = null,
  val trainingContent: TrainingData? = null
)

data class TrainingData(
  val summary: List<Pair<String, String>>,
  val difficulty: List<Pair<String, String>>
)

data class ChallengeData(
  val average: Pair<String, String>,
  val overall: List<Pair<String, String>>,
  val coverage: List<Pair<String, String>>,
  val elevation: List<Pair<String, String>>,
  val dryness: List<Pair<String, String>>
)

data class TemperatureData(
  val average: Pair<String, String>,
  val temperatures: List<Pair<String, String>>,
  val conditions: List<Pair<String, String>>
)

@Composable
fun HistoryScreen( historyViewModel: HistoryViewModel) {

  val uiState by historyViewModel.uiState.collectAsState()
  var expandedStates by remember { mutableStateOf(mapOf<String, Boolean>()) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      "Kausi ${uiState.year}",
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      color = primaryLight
    )

    // Show loading indicator
    if (uiState.isLoading) {
      Box(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    }

    // Show error if any
    uiState.error?.let { error ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
          containerColor = Color.Red.copy(alpha = 0.1f)
        )
      ) {
        Text(
          text = error,
          modifier = Modifier.padding(16.dp),
          color = Color.Red
        )
      }
    }

    // Show data when available
    uiState.yearlyData?.let { yearlyData ->
      // Build sections dynamically from real data
      val sectionsData = buildSectionsFromData(historyViewModel)

      sectionsData.forEach { section ->
        val isExpanded = expandedStates[section.title] ?: false
        SectionCard(
          section = section,
          isExpanded = isExpanded,
          onToggle = {
            expandedStates = expandedStates.toMutableMap().apply {
              this[section.title] = !isExpanded
            }
          }
        )
      }
    }
  }
}

// Function to build sections from real data
@Composable
fun buildSectionsFromData(viewModel: HistoryViewModel): List<Section> {
  val totalTrainings = viewModel.getTotalTrainings()
  val avgDifficulty = viewModel.getAverageDifficulty()
  val terrainAvg = viewModel.getTerrainOverallAverage()
  val avgTemp = viewModel.getAverageTemperature()

  return listOf(
    Section(
      id = "treenimilarat",
      title = "Yleistä",
      summary = "$totalTrainings kpl • vaikeutaso avg $avgDifficulty",
      trainingContent = TrainingData(
        summary = viewModel.getTrainingSummary(),
        difficulty = viewModel.getDifficultyDistribution()
      ),
      colors = primaryLight to primaryDark,
      bgColor = secondaryLight,
      icon = Icons.Default.TableChart
    ),
    Section(
      id = "haasteet",
      title = "Maasto",
      summary = "Keskiarvo: $terrainAvg",
      challengeContent = ChallengeData(
        average = "Keskiarvo" to terrainAvg,
        overall = viewModel.getDifficultyDistribution(), // Using general difficulty as "overall"
        coverage = viewModel.getForestThicknessDistribution(), // forestThickness = coverage
        elevation = viewModel.getAltitudeChangesDistribution(), // altitudeChanges = elevation
        dryness = viewModel.getMoistureLevelDistribution() // moistureLevel = dryness (inverted logic)
      ),
      colors = primaryLight to primaryDark,
      bgColor = secondaryLight,
      icon = Icons.Default.Terrain
    ),
    Section(
      id = "lampotila",
      title = "Sää",
      summary = "Keskiarvo: ${avgTemp}°C",
      temperatureContent = TemperatureData(
        average = "Keskilämpötila" to "${avgTemp}°C",
        temperatures = viewModel.getTemperatureRanges(),
        conditions = viewModel.getWeatherConditions()
      ),
      colors = primaryLight to primaryDark,
      bgColor = secondaryLight,
      icon = Icons.Default.Thermostat
    )
  )
}

// Keep all your existing composable functions unchanged
@Composable
fun SectionCard(section: Section, isExpanded: Boolean, onToggle: () -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onToggle),
    shape = RoundedCornerShape(12.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Brush.horizontalGradient(listOf(section.colors.first, section.colors.second)))
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(section.icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Column {
              Text(section.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
              Text(section.summary, fontSize = 13.sp, color = Color.White.copy(0.9f))
            }
          }
          Icon(
            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            null,
            tint = Color.White
          )
        }
      }
      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(section.bgColor)
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          when {
            section.trainingContent != null -> TrainingContent(data = section.trainingContent)
            section.challengeContent != null -> ChallengeContent(data = section.challengeContent)
            section.temperatureContent != null -> TemperatureContent(data = section.temperatureContent)
            section.data != null -> DataContent(data = section.data)
          }
        }
      }
    }
  }
}

@Composable
private fun TrainingContent(data: TrainingData) {
  fun calculateAverage(values: List<Pair<String, String>>): String {
    val totalItems = values.sumOf { it.second.toIntOrNull() ?: 0 }
    if (totalItems == 0) return "0.0"
    val weightedSum = values.sumOf { (it.first.toIntOrNull() ?: 0) * (it.second.toIntOrNull() ?: 0) }
    val average = weightedSum.toFloat() / totalItems.toFloat()
    return String.format("%.1f", average)
  }

  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    DataContent(data = data.summary)
    PairValueRow(
      title = "Yleinen vaikeustaso",
      average = calculateAverage(data.difficulty),
      values = data.difficulty
    )
  }
}

@Composable
private fun ChallengeContent(data: ChallengeData) {
  fun calculateAverage(values: List<Pair<String, String>>): String {
    val totalItems = values.sumOf { it.second.toIntOrNull() ?: 0 }
    if (totalItems == 0) return "0.0"
    val weightedSum = values.sumOf { (it.first.toIntOrNull() ?: 0) * (it.second.toIntOrNull() ?: 0) }
    val average = weightedSum.toFloat() / totalItems.toFloat()
    return String.format("%.1f", average)
  }

  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    PairValueRow(
      title = "Maaston yleinen haastavuus",
      average = calculateAverage(data.overall),
      values = data.overall
    )
    PairValueRow(
      title = "Maaston peittävyys",
      average = calculateAverage(data.coverage),
      values = data.coverage
    )
    PairValueRow(
      title = "Maaston korkeuserot",
      average = calculateAverage(data.elevation),
      values = data.elevation
    )
    PairValueRow(
      title = "Maaston kuivuus",
      average = calculateAverage(data.dryness),
      values = data.dryness
    )
  }
}

@Composable
private fun TemperatureContent(data: TemperatureData) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    DataContent(data = listOf(data.average))
    PairValueRow(title = "Lämpötilat", values = data.temperatures)
    PairValueRow(title = "Sääolosuhteet", values = data.conditions)
  }
}

@Composable
private fun PairValueRow(
  title: String,
  values: List<Pair<String, String>>,
  average: String? = null
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = Color.White),
    shape = RoundedCornerShape(8.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(title, fontSize = 18.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold)
      }
      if (average != null) {
        Text(
          text = "Keskiarvo $average",
          fontSize = 17.sp,
          color = Color(0xFF1E293B),
          modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        values.forEach { (key, value) ->
          Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
          ) {
            Column(
              modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
              horizontalAlignment = Alignment.Start,
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(text = key, fontSize = 12.sp, color = Color.Gray)
              Text(
                text = "$value kpl",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun DataContent(data: List<Pair<String, String>>) {
  data.chunked(3).forEach { rowData ->
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      rowData.forEach { (label, value) ->
        Card(
          colors = CardDefaults.cardColors(containerColor = Color.White),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 11.sp, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
          }
        }
      }
      if (rowData.size == 1) {
        Spacer(modifier = Modifier.weight(1f))
      }
    }
  }
}