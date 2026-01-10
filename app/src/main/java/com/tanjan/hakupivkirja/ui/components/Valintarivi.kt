package com.tanjan.hakupivkirja.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanjan.hakupivkirja.ui.viewmodels.TrainingSessionViewModel


@Composable
fun Valintarivi(
    trainingSessionViewModel: TrainingSessionViewModel
) {
    val uiState by trainingSessionViewModel.uiState.collectAsState()
    var showSaveTraining by remember { mutableStateOf(false) }

    if (showSaveTraining) {
        SaveTrainingSession(
            trainingViewModel = trainingSessionViewModel,
            onDismissRequest = { showSaveTraining = false },
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 2.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 2.dp, bottom = 4.dp),
            // Negatiivinen väli vetää rivejä ja otsikkoa lähemmäs toisiaan
            verticalArrangement = Arrangement.spacedBy((-12).dp)
        ) {
            Text(
                text = "Treenin tiedot",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 0.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                uiState.currentTrainingSession?.dateMillis?.let {
                    DatePickerFieldToModal(
                        modifier = Modifier.weight(1f).padding(start = 0.dp),
                        selectedDate = it,
                        onDateSelected = { dateMillis ->
                            trainingSessionViewModel.updateSelectedDate(dateMillis)
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

                Column(
                  modifier = Modifier.width(52.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { showSaveTraining = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Kirjaa",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = {trainingSessionViewModel.initializeEmptyTrainingSession()},
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Lisää uusi",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.currentTrainingSession?.shortDescription ?: "",
                    onValueChange = { newDescription ->
                        trainingSessionViewModel.updatePlanDescription(newDescription)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    label = { Text("Suunnitelman nimi", fontSize = 12.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text("Käännä", fontSize = 11.sp, color = Color.Gray)
                    Switch(
                        checked = uiState.startFromLeft,
                        onCheckedChange = { newValue ->
                            trainingSessionViewModel.setStartFromLeft(newValue)
                        },
                        modifier = Modifier.padding(top = 0.dp)
                    )
                }
            }
        }
    }
}
