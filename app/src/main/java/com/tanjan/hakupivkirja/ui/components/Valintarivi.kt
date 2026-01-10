package com.tanjan.hakupivkirja.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tanjan.hakupivkirja.ui.viewmodels.TrainingSessionViewModel


@Composable
fun Valintarivi(
    trainingSessionViewModel: TrainingSessionViewModel
) {
    // Collect the uiState from the ViewModel
    val uiState by trainingSessionViewModel.uiState.collectAsState()
    var showSaveTraining by remember { mutableStateOf(false) }

    if (showSaveTraining) {
        SaveTrainingSession(
            trainingViewModel = trainingSessionViewModel,
            onDismissRequest = { showSaveTraining = false },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            uiState.currentTrainingSession?.dateMillis?.let {
                DatePickerFieldToModal(
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    selectedDate = uiState.currentTrainingSession?.dateMillis!!,
                    onDateSelected = { selectedDate ->
                        selectedDate.let { dateMillis ->
                            trainingSessionViewModel.updateSelectedDate(dateMillis)
                        }
                    }
                )
            }
            uiState.currentTrainingSession?.let {
                RadanPituusDropdown(
                    currentTrackLength= it.trackLength,
                    onSelectionChange = { trackLength, correspondingMaxPistot ->
                    trainingSessionViewModel.updateTrackLengthAndMaxPistot(trackLength, correspondingMaxPistot)
                    }
                )
            }
            PistojenMaaraDropdown(
                maxPistot = uiState.maxPistot,
                onSelectedPistotChange = { count ->
                    trainingSessionViewModel.updateSelectedPistot(count)
                }
            )

            Column{
                IconButton(
                    onClick = { showSaveTraining = true },
                    modifier = Modifier
                        .padding(0.dp)
                        .offset(y = (10).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Kirjaa",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = {trainingSessionViewModel.initializeEmptyTrainingSession()},
                    modifier = Modifier.padding(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Lisää uusi",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

        }
        Row {
            OutlinedTextField(
                value = uiState.currentTrainingSession?.shortDescription ?: "",
                onValueChange = { newDescription ->
                    // text = newDescription // (2) DON'T update local 'text' state
                    // INSTEAD, update ViewModel state
                    trainingSessionViewModel.updatePlanDescription(newDescription) // (3) CALL ViewModel function
                },
                keyboardOptions = KeyboardOptions( // Keep only this one
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                label = { Text("Suunnitelma") },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp, end = 4.dp)
            ){
                Text("Käännä rata")
                Switch(
                    // Read the value from the uiState
                    checked = uiState.startFromLeft,
                    // Call the ViewModel function on change
                    onCheckedChange = { newValue ->
                        // Kutsutaan suoraan ViewModelin funktiota
                        trainingSessionViewModel.setStartFromLeft(newValue)
                    }
                )

            }
        }
    }
}