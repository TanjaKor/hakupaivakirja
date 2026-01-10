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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.tanjan.hakupivkirja.ui.viewmodels.HistoryViewModel


// Data classes remain same
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
  val trainingContent: TrainingData? = null,
  val trackContent: TrackData? = null
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

data class TrackData(
  val averageLength: String,
  val lengthDistribution: List<Pair<String, String>>,
  val pistoDistribution: List<Pair<String, String>>,
  val tyhjaStats: List<Pair<String, String>>
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
      color = MaterialTheme.colorScheme.primary
    )

    if (uiState.isLoading) {
      Box(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    }

    uiState.error?.let { error ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.errorContainer
        )
      ) {
        Text(
          text = error,
          modifier = Modifier.padding(16.dp),
          color = MaterialTheme.colorScheme.onErrorContainer
        )
      }
    }

    uiState.yearlyData?.let { yearlyData ->
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

@Composable
fun buildSectionsFromData(viewModel: HistoryViewModel): List<Section> {
  val totalTrainings = viewModel.getTotalTrainings()
  val avgDifficulty = viewModel.getAverageDifficulty()
  val terrainAvg = viewModel.getTerrainOverallAverage()
  val avgTemp = viewModel.getAverageTemperature()
  val avgTrack = viewModel.getAverageTrackLength()

  return listOf(
    Section(
      id = "treenimilarat",
      title = "Yleistä",
      summary = "$totalTrainings kpl • vaikeutaso ka. $avgDifficulty",
      trainingContent = TrainingData(
        summary = viewModel.getTrainingSummary(),
        difficulty = viewModel.getDifficultyDistribution()
      ),
      colors = MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.primaryContainer,
      bgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
      icon = Icons.Default.TableChart
    ),
    Section(
      id = "radat",
      title = "Radat & Pistot",
      summary = "Ka. pituus: $avgTrack",
      trackContent = TrackData(
        averageLength = avgTrack,
        lengthDistribution = viewModel.getTrackLengthDistribution(),
        pistoDistribution = viewModel.getPistoAmountDistribution(),
        tyhjaStats = viewModel.getTyhjaTrainingStats()
      ),
      colors = MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.primaryContainer,
      bgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
      icon = Icons.Default.Analytics
    ),
    Section(
      id = "haasteet",
      title = "Maasto",
      summary = "Ka. $terrainAvg",
      challengeContent = ChallengeData(
        average = "Keskiarvo" to terrainAvg,
        overall = viewModel.getTerrainOverallDistribution(),
        coverage = viewModel.getForestThicknessDistribution(),
        elevation = viewModel.getAltitudeChangesDistribution(),
        dryness = viewModel.getMoistureLevelDistribution()
      ),
      colors = MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.primaryContainer,
      bgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
      icon = Icons.Default.Terrain
    ),
    Section(
      id = "lampotila",
      title = "Sää",
      summary = "Ka. ${avgTemp}°C",
      temperatureContent = TemperatureData(
        average = "Keskilämpötila" to "${avgTemp}°C",
        temperatures = viewModel.getTemperatureRanges(),
        conditions = viewModel.getWeatherConditions()
      ),
      colors = MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.primaryContainer,
      bgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
      icon = Icons.Default.Thermostat
    )
  )
}

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
                .background(MaterialTheme.colorScheme.onPrimary.copy(0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(section.icon, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
            }
            Column {
              Text(section.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimary)
              Text(section.summary, fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimary.copy(0.9f))
            }
          }
          Icon(
            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
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
            section.trackContent != null -> TrackContent(data = section.trackContent)
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
private fun TrackContent(data: TrackData) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    DataContent(data = listOf("Ka. pituus" to data.averageLength))
    PairValueRow(title = "Ratojen pituudet", values = data.lengthDistribution)
    PairValueRow(title = "Pistomäärät (kpl/treeni)", values = data.pistoDistribution)
    PairValueRow(title = "Tyhjät pistot", values = data.tyhjaStats)
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
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(8.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(title, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
      }
      if (average != null) {
        Text(
          text = "Ka. $average",
          fontSize = 17.sp,
          color = MaterialTheme.colorScheme.onSurface,
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
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
              Text(text = key, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(
                text = if (value.contains("kpl") || value.contains("%") || value.contains("m")) value else "$value kpl",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
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
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
          }
        }
      }
      if (rowData.size == 1) {
        Spacer(modifier = Modifier.weight(1f))
      }
    }
  }
}
