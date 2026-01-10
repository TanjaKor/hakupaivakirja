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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    var showResetWarning by remember { mutableStateOf(false) }

    if (showSaveTraining) {
        SaveTrainingSession(
            trainingViewModel = trainingSessionViewModel,
            onDismissRequest = { showSaveTraining = false },
        )
    }

    if (showResetWarning) {
        AlertDialog(
            onDismissRequest = { showResetWarning = false },
            title = { Text("Huomio") },
            text = { Text("Avaamalla uuden radan menetät tallentamattomat tietosi.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        trainingSessionViewModel.initializeEmptyTrainingSession()
                        showResetWarning = false
                    }
                ) {
                    Text("Jatka", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetWarning = false }) {
                    Text("Peruuta")
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 2.dp,
        // KORJAUS: Käytetään teeman väriä valkoisen sijaan
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 2.dp, bottom = 4.dp),
            verticalArrangement = Arrangement.spacedBy((1).dp)
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
                    selectedPistot = uiState.selectedPistot,
                    maxPistot = uiState.maxPistot,
                    onSelectedPistotChange = { count ->
                        trainingSessionViewModel.updateSelectedPistot(count)
                    }
                )

                Column(
                    modifier = Modifier.width(52.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy((-10).dp, Alignment.CenterVertically)
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
                        onClick = { showResetWarning = true },
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
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
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
                    label = { Text("Treenisuunnitelma lyhyesti", fontSize = 12.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                Column(
                    modifier = Modifier.width(52.dp).padding(top=2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy((-8).dp, Alignment.CenterVertically)
                ){
                    // KORJAUS: Käytetään teeman tekstiväriä
                    Text(
                        text = "Käännä", 
                        fontSize = 11.sp, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = uiState.startFromLeft,
                        onCheckedChange = { newValue ->
                            trainingSessionViewModel.setStartFromLeft(newValue)
                        },
                        modifier = Modifier.padding(0.dp)
                    )
                }
            }
        }
    }
}
