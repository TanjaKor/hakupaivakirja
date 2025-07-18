package com.example.hakupivkirja.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.hakupivkirja.model.TrainingSessionUiState
import com.example.hakupivkirja.ui.viewmodels.TrainingSessionViewModel

@Composable
fun SaveTrainingSession(
  onDismissRequest: () -> Unit,
  trainingViewModel: TrainingSessionViewModel
) {

  val uiState by trainingViewModel.uiState.collectAsState()
  var showTrainingDetails by remember { mutableStateOf(false) }
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(uiState.saveSuccessMessage) {
    if (uiState.saveSuccessMessage) {
      snackbarHostState.showSnackbar(
        message = "Tallennettu!",
        duration = SnackbarDuration.Short
      ).let {
        // After snackbar is shown (or dismissed), reset flag and dismiss dialog
        trainingViewModel.saveMessageShown()
        onDismissRequest() // Dismiss the dialog
      }
    }
  }

  // Effect to show error Snackbar

  Dialog(
    onDismissRequest = {onDismissRequest()}
  )  {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(0.dp),
      shape = RoundedCornerShape(16.dp)
    ) {
      Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()
        .wrapContentSize(Alignment.Center),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Haluatko tallentaa suunnitelman vai tehdyn treenin?",
          textAlign = TextAlign.Center,
        )
        if (!showTrainingDetails) {
          TextButton(onClick = {
            trainingViewModel.saveTrainingPlan()
          }) {
            Text(text = "Suunnitelma")
          }
          TextButton(onClick = {
            showTrainingDetails = true
          }) {
            Text(text = "Treeni on tehty")
          }
        }
        if (showTrainingDetails) {
          TrainingDetails(uiState = uiState, trainingSessionViewModel = trainingViewModel, onSaveCompleted = {showTrainingDetails = false})
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.CenterHorizontally))
      }

    }
  }
}

@Composable
fun TrainingDetails(
  uiState: TrainingSessionUiState,
  trainingSessionViewModel: TrainingSessionViewModel,
  onSaveCompleted: () -> Unit
) {
  //state for location
  var trainingLocation by remember { mutableStateOf("")}
  val focusManager = LocalFocusManager.current
  var weatherData by remember { mutableStateOf("") }
  var isLoadingWeather by remember { mutableStateOf(false) }
  // States for rating and notes
  var selectedRating by remember { mutableStateOf(uiState.currentTrainingSession?.overallRating ?: 1) } // Default to 1 or value from uiState
  var selectedDifficulty by remember { mutableStateOf(uiState.currentTrainingSession?.difficultyRating ?: 1) } // Default to 1 or value from uiState
  var notes by remember { mutableStateOf(uiState.currentTrainingSession?.notes ?: "") }
  // States for Terrain
  var selectedCoverage by remember { mutableStateOf(uiState.terrain?.forestThickness ?: 1) }
  var selectedMoisture by remember { mutableStateOf(uiState.terrain?.moistureLevel ?: 1) }
  var selectedAltitudeChanges by remember { mutableStateOf(uiState.terrain?.altitudeChanges ?: 1) }

  // Function to fetch weather data
  fun fetchWeatherData(location: String) {
    if (location.isNotBlank()) {
      isLoadingWeather = true
      // Call your weather API here
      trainingSessionViewModel.fetchWeatherData(location) { weather ->
        weatherData = weather
        isLoadingWeather = false
      }
    }
  }

  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    OutlinedTextField(
      value = trainingLocation,
      onValueChange = { trainingLocation = it },
      label = { Text("Treenipaikka")},
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .onFocusChanged { focusState ->
          // Fetch weather when field loses focus and has text
          if (!focusState.isFocused && trainingLocation.isNotBlank()) {
            fetchWeatherData(trainingLocation)
          }
        },
      keyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Done
      ),
      keyboardActions = KeyboardActions(
        onDone = {
          // Fetch weather when Done button is pressed
          if (trainingLocation.isNotBlank()) {
            fetchWeatherData(trainingLocation)
          }
          focusManager.clearFocus() // Hide keyboard
        }
      )
    )
    Text(text = "Sää: ")
    when {
      isLoadingWeather -> {
        CircularProgressIndicator(
          modifier = Modifier.size(16.dp),
          strokeWidth = 2.dp
        )
      }

      weatherData.isNotBlank() -> {
        Text(text = weatherData)
      }

      else -> {
        Text(text = "Syötä sijainti saadaksesi säätiedot")
      }
    }
    OutlinedTextField(
      value = notes,
      onValueChange = { newNotes ->
       notes = newNotes
      },
      keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
      ),
      label = { Text("Vapaa sana treenistä") },
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
    )
    Row {
      RatingCard(
        title = "Peittävyys",
        options = listOf(1,2,3),
        selectedOption = selectedCoverage,
        onOptionSelected = { selectedCoverage = it },
        modifier = Modifier.weight(0.9f)
      )
      RatingCard(
        title ="Kosteus",
        options =listOf(1,2,3),
        selectedOption = selectedMoisture,
        onOptionSelected = { selectedMoisture = it },
        modifier =Modifier.weight(0.8f)
      )
      RatingCard(
        title ="Korkeuserot",
        options = listOf(1,2,3),
        selectedOption = selectedAltitudeChanges,
        onOptionSelected = { selectedAltitudeChanges = it },
        modifier =Modifier.weight(1f)
      )
    }
    Row {
      RatingCard(
        title = "Arvosana",
        options = listOf(1, 2, 3, 4, 5),
        selectedOption = selectedRating,
        onOptionSelected = { selectedRating = it },
        modifier = Modifier.weight(1f)
      )
      RatingCard(
        title ="Vaikeusaste",
        options = listOf(1,2,3,4,5),
        selectedOption = selectedDifficulty,
        onOptionSelected = { selectedDifficulty = it },
        modifier = Modifier.weight(1f)
      )
    }
    Button(onClick = {
      trainingSessionViewModel.saveCompletedTraining(
        rating = selectedRating,
        difficulty = selectedDifficulty,
        notes = notes,
        forestThickness = selectedCoverage,
        moistureLevel = selectedMoisture,
        altitudeChanges = selectedAltitudeChanges
      )
      onSaveCompleted()
    })
    {
      Text(text = "Valmis")
    }
  }
  }


@Composable
fun RatingCard(
  title: String,
  options: List<Int>,
  selectedOption: Int,
  onOptionSelected: (Int) -> Unit,
  modifier: Modifier) {
  Card(modifier = modifier
    .padding(4.dp)
    .border(
      1.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)
    )
  ) {
    Text(text = title, textAlign = TextAlign.Center, modifier = Modifier.padding(2.dp))
    RadioButtons(
      options = options,
      selectedOption = selectedOption,
      onOptionSelected = onOptionSelected,
      modifier = Modifier)
  }
}

@Composable
fun RadioButtons(
  options: List<Int>,
  selectedOption: Int,
  onOptionSelected: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val radioOptions = options

  // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
  Column(modifier.selectableGroup()) {
    radioOptions.forEach { optionValue ->
      Row(
        Modifier
          .height(56.dp)
          .selectable(
            selected = (optionValue == selectedOption),
            onClick = { onOptionSelected(optionValue) },
            role = Role.RadioButton
          )
          .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        RadioButton(
          selected = (optionValue == selectedOption),
          onClick = null // null recommended for accessibility with screen readers
        )
        Text(
          text = optionValue.toString(),
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(start = 16.dp)
        )
      }
    }
  }
}