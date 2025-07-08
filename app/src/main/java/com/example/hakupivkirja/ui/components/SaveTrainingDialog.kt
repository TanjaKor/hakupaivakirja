package com.example.hakupivkirja.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
          text = "Haluatko tallentaa suunnitelman vai tehtynä treeninä?",
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
          TrainingDetails(uiState = uiState, trainingSessionViewModel = trainingViewModel)
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.CenterHorizontally))
      }

    }
  }
}

@Composable
fun TrainingDetails(
  uiState: TrainingSessionUiState,
  trainingSessionViewModel: TrainingSessionViewModel) {



  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = "Sää: tähän APISTA sää")
    OutlinedTextField(
      value = uiState.currentTrainingSession?.notes ?: "",
      onValueChange = { newNotes ->
        // text = newDescription // (2) DON'T update local 'text' state
        // INSTEAD, update ViewModel state
        trainingSessionViewModel.updateNotes(newNotes) //
      },
      keyboardOptions = KeyboardOptions( // Keep only this one
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
      ),
      label = { Text("Vapaa sana treenistä") },
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
    )
    Row {
      RatingCard("Peittävyys", listOf(1,2,3), Modifier.weight(0.9f))
      RatingCard("Kosteus", listOf(1,2,3),Modifier.weight(0.8f))
      RatingCard("Korkeuserot", listOf(1,2,3),Modifier.weight(1f))
    }
    Row {
      RatingCard("Arvosana", listOf(1,2,3,4,5), Modifier.weight(1f))
      RatingCard("Vaikeusaste", listOf(1,2,3,4,5),Modifier.weight(1f))
    }
    Button(onClick = {trainingSessionViewModel.saveCompletedTraining()})
    {
      Text(text = "Valmis")
    }
  }
  }


@Composable
fun RatingCard(title: String, options: List<Int>, modifier: Modifier) {
  Card(modifier = modifier
    .padding(4.dp)
    .border(
      1.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)
    )
  ) {
    Text(text = title, textAlign = TextAlign.Center, modifier = Modifier.padding(2.dp))
    RadioButtons(options, modifier = Modifier)
  }
}

@Composable
fun RadioButtons(options: List<Int>, modifier: Modifier = Modifier) {
  val radioOptions = options
  val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
  // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
  Column(modifier.selectableGroup()) {
    radioOptions.forEach { text ->
      Row(
        Modifier
          .height(56.dp)
          .selectable(
            selected = (text == selectedOption),
            onClick = { onOptionSelected(text) },
            role = Role.RadioButton
          )
          .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        RadioButton(
          selected = (text == selectedOption),
          onClick = null // null recommended for accessibility with screen readers
        )
        Text(
          text = text.toString(),
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(start = 16.dp)

        )
      }
    }
  }
}