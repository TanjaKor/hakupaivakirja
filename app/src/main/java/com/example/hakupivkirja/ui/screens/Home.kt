package com.example.hakupivkirja.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hakupivkirja.ui.components.UusiRata
import com.example.hakupivkirja.ui.components.Valintarivi
import com.example.hakupivkirja.ui.viewmodels.TrainingSessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    trainingSessionViewModel: TrainingSessionViewModel = viewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    //tarkastellaan uiState:n muutoksia
    val uiState by trainingSessionViewModel.uiState.collectAsState()

    // Initialize empty training session on first launch
    LaunchedEffect(Unit) {
        if (uiState.currentTrainingSession == null) {
            trainingSessionViewModel.initializeEmptyTrainingSession()
        }
    }


    Column {
        Valintarivi(
            selectedPistot = uiState.selectedPistot,
            pistotMax = uiState.maxPistot,
            onSelectedPistotChange = { count -> trainingSessionViewModel.updateSelectedPistot(count) },
            onMaxPistotChange = { count -> trainingSessionViewModel.updateMaxPistot(count) })
        HorizontalDivider(thickness = 2.dp)
        UusiRata(
            uiState = uiState,
            onPistoModeChange = { pistoIndex, mode ->
                trainingSessionViewModel.updatePistoMode(pistoIndex, mode)
            },
            onMMDetailsChange = { pistoIndex, haukut, avut, palkka ->
                trainingSessionViewModel.updateMMDetails(pistoIndex, haukut, avut, palkka)
            }
        )
    }
} // State managed at the parent level
