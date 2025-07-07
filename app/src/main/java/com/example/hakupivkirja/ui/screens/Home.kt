package com.example.hakupivkirja.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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

// NEXTSTEP: keksiikö Kirjausnappulalle FABin tms paremman ratkaisun?
//Tarkista raportista onko tosiaan valmis kotisivu treenin suunnittelun osalta??

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
            trainingSessionViewModel = trainingSessionViewModel)
        HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(bottom = 8.dp))
        UusiRata(
            uiState = uiState,
            onPistoModeChange = { pistoIndex, mode ->
                trainingSessionViewModel.updatePistoMode(pistoIndex, mode)
            },
            onHaukutChange = { pistoIndex, haukut ->
                trainingSessionViewModel.updateHaukut(pistoIndex, haukut)
            },
            onAvutChange = { pistoIndex, avut ->
                trainingSessionViewModel.updateAvut(pistoIndex, avut)
            },
            onPalkkaChange = { pistoIndex, palkka ->
                trainingSessionViewModel.updatePalkka(pistoIndex, palkka)
            },
            onComeToMiddleChange = { pistoIndex, comeToMiddle ->
                trainingSessionViewModel.updateComeToMiddle(pistoIndex, comeToMiddle)
            },
            onIsClosedChange = { pistoIndex, isClosed ->
                trainingSessionViewModel.updateIsClosed(pistoIndex, isClosed)
            },
            onSuoraPalkkaChange = { pistoIndex, suoraPalkka ->
                trainingSessionViewModel.updateSuoraPalkka(pistoIndex, suoraPalkka)
            },
            onKiintoRullaChange = { pistoIndex, kiintoRulla ->
                trainingSessionViewModel.updateKiintoRulla(pistoIndex, kiintoRulla)
            },
            onIrtorullanSijaintiChange = { pistoIndex, irtorullanSijainti ->
                trainingSessionViewModel.updateIrtorullanSijainti(pistoIndex, irtorullanSijainti)
            }
        )
    }
} // State managed at the parent level
