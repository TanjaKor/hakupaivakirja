package com.example.hakupivkirja.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hakupivkirja.ui.components.UusiRata
import com.example.hakupivkirja.ui.components.Valintarivi
import com.example.hakupivkirja.ui.viewmodels.TrainingSessionViewModel

// NEXTSTEP loput ui:sta (sisääntulon lisäys, piston moden vaihtaminen (x, jolla menee takaisin default tilaan),
//Ilmaisun vaihtaminen Koiran kuvakkeeseen.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    trainingSessionViewModel: TrainingSessionViewModel
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // This will now scroll everything
    ) {
        Valintarivi(
//            selectedPistot = uiState.selectedPistot,
//            pistotMax = uiState.maxPistot,
//            onSelectedPistotChange = { count -> trainingSessionViewModel.updateSelectedPistot(count) },
//            onMaxPistotChange = { count -> trainingSessionViewModel.updateMaxPistot(count) },
            trainingSessionViewModel = trainingSessionViewModel)
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
