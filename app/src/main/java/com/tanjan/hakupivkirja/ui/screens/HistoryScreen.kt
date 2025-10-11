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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanjan.hakupivkirja.ui.screens.SectionData.sectionsData
import com.tanjan.hakupivkirja.ui.theme.primaryDark
import com.tanjan.hakupivkirja.ui.theme.primaryLight
import com.tanjan.hakupivkirja.ui.theme.secondaryLight

// --- Data-luokkien päivitys ---
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

// Uusi dataluokka treenimäärät-osioon
data class TrainingData(
  val summary: List<Pair<String, String>>,
  val difficulty: List<Pair<String, String>>
)

// Uusi dataluokka haastavuus-osioon
data class ChallengeData(
  val average: Pair<String, String>,
  val overall: List<Pair<String, String>>,
  val coverage: List<Pair<String, String>>,
  val elevation: List<Pair<String, String>>,
  val dryness: List<Pair<String, String>>
)

// Uusi dataluokka lämpötila-osioon
data class TemperatureData(
  val average: Pair<String, String>,
  val temperatures: List<Pair<String, String>>,
  val conditions: List<Pair<String, String>>
)

object SectionData {
  val sectionsData = listOf(
    Section(
      id = "treenimilarat",
      title = "Yleistä",
      summary = "13 kpl • Avg 4/kk • vaikeutaso avg 2.9",
      trainingContent = TrainingData(
        summary = listOf("Yhteensä" to "13 kpl", "Keskiarvo/kk" to "4 kpl", "Keskiarvo/vk" to "1 kpl"),
        difficulty = listOf(
          "1" to "2", "2" to "3",
          "3" to "3", "4" to "4",
          "5" to "1"
        )
      ),
      colors = primaryLight to primaryDark,
      bgColor = secondaryLight,
      icon = Icons.Default.TableChart
    ),
    Section(
      id = "haasteet",
      title = "Maasto",
      summary = "Keskiarvo: 4",
      challengeContent = ChallengeData(
        average = "Keskiarvo" to "4",
        overall = listOf("1" to "4", "2" to "2","3" to "2","4" to "2","5" to "2"),
        coverage = listOf("1" to "5", "2" to "1", "3" to "3"),
        elevation = listOf("1" to "1", "2" to "6", "3" to "2"),
        dryness = listOf("1" to "2", "2" to "3", "3" to "10")
      ),
      colors = primaryLight to primaryDark,
      bgColor = secondaryLight,
      icon = Icons.Default.Terrain
    ),
    Section(
      id = "lampotila",
      title = "Sää",
      summary = "Keskiarvo: 17°C",
      temperatureContent = TemperatureData(
        average = "Keskilämpötila" to "17°C",
        temperatures = listOf("0-10°C" to "2", "10-20°C" to "3", "20-30°C" to "4", ),
        conditions = listOf(
          "Aurinkoinen" to "3",
          "Pilvinen" to "3",
          "Sadetta" to "3"
        )
      ),
      colors = primaryLight to primaryDark,
      bgColor = secondaryLight,
      icon = Icons.Default.Thermostat
    )
  )
}

@Composable
fun HistoryScreen() {
  var expandedStates by remember { mutableStateOf(mapOf<String, Boolean>()) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text("Kausi 2025", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = primaryLight)
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
          // --- Sisällön dynaaminen valinta ---
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
  // Funktio, joka laskee painotetun keskiarvon listasta
  fun calculateAverage(values: List<Pair<String, String>>): String {
    val totalItems = values.sumOf { it.second.replace(" kpl", "").toIntOrNull() ?: 0 }
    if (totalItems == 0) return "0.0"
    val weightedSum = values.sumOf { (it.first.toIntOrNull() ?: 0) * (it.second.replace(" kpl", "").toIntOrNull() ?: 0) }
    val average = weightedSum.toFloat() / totalItems.toFloat()
    return String.format("%.1f", average)
  }

  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    // Yhteenveto ylhäällä
    DataContent(data = data.summary)

    // Yleinen vaikeustaso alakategoriana
    PairValueRow(
      title = "Yleinen vaikeustaso",
      average = calculateAverage(data.difficulty),
      values = data.difficulty
    )
  }
}

@Composable
private fun ChallengeContent(data: ChallengeData) {
  // Funktio, joka laskee painotetun keskiarvon listasta
  fun calculateAverage(values: List<Pair<String, String>>): String {
    val totalItems = values.sumOf { it.second.toIntOrNull() ?: 0 }
    if (totalItems == 0) return "0.0"
    val weightedSum = values.sumOf { (it.first.toIntOrNull() ?: 0) * (it.second.toIntOrNull() ?: 0) }
    val average = weightedSum.toFloat() / totalItems.toFloat()
    // Pyöristetään yhteen desimaaliin
    return String.format("%.1f", average)
  }

  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

    // Jokainen alakategoria omassa kortissaan, jossa laskettu keskiarvo on korostettuna
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
    // Ylin rivi yleiselle keskiarvolle
    DataContent(data = listOf(data.average))

    // Alakategoriat omissa korteissaan
    PairValueRow(title = "Lämpötilat", values = data.temperatures)
    PairValueRow(title = "Sääolosuhteet", values = data.conditions)
  }
}

// --- Uudelleenkäytettävät apufunktiot ---
@Composable
private fun PairValueRow(
  title: String,
  values: List<Pair<String, String>>,
  average: String? = null // Valinnainen keskiarvo-parametri
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
          modifier = Modifier.padding(top=8.dp,bottom=8.dp)
        )
      }

      // Rivi, joka näyttää varsinaiset arvot
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
              Text(
                text = key,
                fontSize = 12.sp,
                color = Color.Gray
              )
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
  // Jaa data kahden kortin ryhmiin ja luo rivit dynaamisesti
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
      // Jos rivillä on vain yksi alkio, lisää tyhjä tila, jotta layout pysyy ehjänä
      if (rowData.size == 1) {
        Spacer(modifier = Modifier.weight(1f))
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
  HistoryScreen()
}