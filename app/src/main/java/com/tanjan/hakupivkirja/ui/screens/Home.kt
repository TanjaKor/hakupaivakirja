package com.tanjan.hakupivkirja.ui.screens

// Apukomponentti Box importtia varten
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tanjan.hakupivkirja.ui.components.UusiRataItems
import com.tanjan.hakupivkirja.ui.components.Valintarivi
import com.tanjan.hakupivkirja.ui.viewmodels.TrainingSessionViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    trainingSessionViewModel: TrainingSessionViewModel,
) {
    val uiState by trainingSessionViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (uiState.currentTrainingSession == null) {
            trainingSessionViewModel.initializeEmptyTrainingSession()
        }
    }

    // Käytetään haaleaa taustaväriä, joka korostaa valkoisia kortteja
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        item {
            // Lisätään hieman ilmaa yläreunaan, mutta pidetään tiiviinä
            Box((Modifier.padding(bottom=6.dp))) {
                Valintarivi(trainingSessionViewModel = trainingSessionViewModel)
            }
        }

        // UusiRataItems hoitaa omat välinsä ja korttityylinsä
        UusiRataItems(
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
            },
            onControlChange = { pistoIndex, control ->
                trainingSessionViewModel.updateControl(pistoIndex, control)
            }
        )
    }
}


