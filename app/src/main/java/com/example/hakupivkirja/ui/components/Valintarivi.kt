package com.example.hakupivkirja.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hakupivkirja.ui.viewmodels.TrainingSessionViewModel


@Composable
fun Valintarivi(
//    pistotMax: Int,
//    selectedPistot: Int,
//    onMaxPistotChange: (Int) -> Unit,
//    onSelectedPistotChange: (Int) -> Unit,
    trainingSessionViewModel: TrainingSessionViewModel,
    modifier: Modifier = Modifier
) {
    // Collect the uiState from the ViewModel
    val uiState by trainingSessionViewModel.uiState.collectAsState()
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var text by remember { mutableStateOf("Treenisuunnitelma lyhyesti") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            DatePickerFieldToModal()
            uiState.currentTrainingSession?.let {
                RadanPituusDropdown(currentTrackLength= it.trackLength, onSelectionChange = { trackLength, correspondingMaxPistot ->
                    trainingSessionViewModel.updateTrackLengthAndMaxPistot(trackLength, correspondingMaxPistot)
                })
            }
            PistojenMaaraDropdown(
                maxPistot = uiState.maxPistot,
                onSelectedPistotChange = { count ->
                    trainingSessionViewModel.updateSelectedPistot(count)
                }
            )
            Button(
                onClick = { trainingSessionViewModel.saveTrainingSession() },
            ) {
                Text(text = "Kirjaa")
            }

        }
        OutlinedTextField(
            value = uiState.currentTrainingSession?.shortDescription ?: "",
            onValueChange =  { newDescription ->
                // text = newDescription // (2) DON'T update local 'text' state
                // INSTEAD, update ViewModel state
                trainingSessionViewModel.updatePlanDescription(newDescription) // (3) CALL ViewModel function
            },
            label = { Text("Suunnitelma") },
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        )
    }
}