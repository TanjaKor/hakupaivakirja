package com.example.hakupivkirja.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hakupivkirja.model.PistoMode
import com.example.hakupivkirja.model.PistoUiState
import com.example.hakupivkirja.model.TrainingSessionUiState
import com.example.hakupivkirja.ui.theme.HakupäiväkirjaTheme
import kotlin.math.ceil

@Composable
fun UusiRata(
    uiState: TrainingSessionUiState,
    onPistoModeChange: (Int, PistoMode) -> Unit,
    onMMDetailsChange: (Int, String?, String?, String?) -> Unit)
{
    val selectedPistot = uiState.selectedPistot
    println("Rata - pistojenMaara initial value: $selectedPistot")

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(20.dp)
    ) {
        //varmistetaan, että jakolaskun tulokseksi tulee kokonaisluku
        val rowCount = ceil(selectedPistot  / 2.0).toInt()
        //loopataan takaperin, että pistojen järjestys on oikea
        for (rowIndex in (rowCount - 1) downTo 0) {
            Row( //Rivi kahdelle vierekkäiselle pistolle
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().height(125.dp)
            ) {
                val leftPistoNumber = rowIndex * 2+2 //vas. pistonro + apu, ettei pistoja tule liikaa
                val leftPistoIndex = leftPistoNumber - 1 //vas puoleisen piston index

                val rightPistoNumber = leftPistoNumber -1 //oikean puoleinen pistonro
                val rightPistoIndex = rightPistoNumber - 1 //oikean puoleisen piston index

                if (leftPistoIndex in 0..<selectedPistot) { //renderöidään vain niin monta kuin pistoja
                    val leftPistoState = uiState.pistoStates[leftPistoIndex] ?: PistoUiState(
                        pistoIndex = leftPistoIndex,
                        selectedPistot = selectedPistot
                    )
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement =
                            if (leftPistoState.currentMode == PistoMode.TYHJA)
                                Arrangement.Center
                            else Arrangement.Start
                    ) { //vas piston sisältö
                        Text(leftPistoNumber.toString()) // pistonro
                        when (leftPistoState.currentMode) {
                            PistoMode.MM -> {
                                Pisto(
                                    pistoUiState = leftPistoState,
                                    onHaukutChange = { haukut ->
                                        onMMDetailsChange(leftPistoIndex, haukut, leftPistoState.avut, leftPistoState.palkka)
                                    },
                                    onAvutChange = { avut ->
                                        onMMDetailsChange(leftPistoIndex, leftPistoState.haukut, avut, leftPistoState.palkka)
                                    },
                                    onPalkkaChange = { palkka ->
                                        onMMDetailsChange(leftPistoIndex, leftPistoState.haukut, leftPistoState.avut, palkka)
                                    }
                                )
                            }

                            PistoMode.TYHJA -> {
                                Text(text = "Tyhjä", modifier = Modifier.padding(start = 10.dp))
                            }

                            PistoMode.DEFAULT -> {
                                Row {
                                    TextButton(onClick = {
                                        onPistoModeChange(leftPistoIndex, PistoMode.TYHJA)
                                        Log.d("UusiRata", "list of pistos ${uiState.pistoStates}")
                                        Log.d("UusiRata", "Changed pisto $leftPistoIndex to TYHJA")
                                    }) {
                                        Text(text = "Tyhjä")
                                    }
                                    TextButton(onClick = {
                                        onPistoModeChange(leftPistoIndex, PistoMode.MM)
                                        Log.d("UusiRata", "list of pistos ${uiState.pistoStates}")
                                        Log.d("UusiRata", "Changed pisto $leftPistoIndex to MM")
                                    }) {
                                        Text(text = "MM")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    //placeholder for empty space when only one pisto
                    Box(modifier = Modifier.weight(1f).height(0.dp))
                }
                if (rightPistoIndex in 0..<selectedPistot) {
                    // Get the PistoUiState for the right pisto, or create default if not exists
                    val rightPistoState = uiState.pistoStates[rightPistoIndex] ?: PistoUiState(
                        pistoIndex = rightPistoIndex,
                        selectedPistot = selectedPistot
                    )
                    Row(
                        modifier = Modifier.weight(1f),
                        //PistoState.Tyhjä renders at the center
                        horizontalArrangement = if (rightPistoState.currentMode == PistoMode.TYHJA)
                            Arrangement.Center
                        else Arrangement.End
                    ) { //oikeanpuoleisen piston sisältö
                        Text(text = rightPistoNumber.toString())
                        when (rightPistoState.currentMode) {
                           PistoMode.MM -> {
                                Pisto(
                                    pistoUiState = rightPistoState,
                                    onHaukutChange = { haukut ->
                                        onMMDetailsChange(rightPistoIndex, haukut, rightPistoState.avut, rightPistoState.palkka)
                                    },
                                    onAvutChange = { avut ->
                                        onMMDetailsChange(rightPistoIndex, rightPistoState.haukut, avut, rightPistoState.palkka)
                                    },
                                    onPalkkaChange = { palkka ->
                                        onMMDetailsChange(rightPistoIndex, rightPistoState.haukut, rightPistoState.avut, palkka)
                                    }
                                )
                           }

                            PistoMode.TYHJA -> {
                                Text(text = "Tyhjä", modifier = Modifier.padding(start = 10.dp))
                            }

                            PistoMode.DEFAULT -> {
                                Row {
                                    TextButton(onClick = {
                                        onPistoModeChange(rightPistoIndex, PistoMode.TYHJA)
                                        Log.d("UusiRata", "list of pistos ${uiState.pistoStates}")
                                        Log.d("UusiRata", "Changed pisto $rightPistoIndex to TYHJA")
                                    }) {
                                        Text(text = "Tyhjä")
                                    }
                                    TextButton(onClick = {
                                        onPistoModeChange(rightPistoIndex, PistoMode.MM)
                                        Log.d("UusiRata", "list of pistos ${uiState.pistoStates}")
                                        Log.d("UusiRata", "Changed pisto $rightPistoIndex to MM")
                                    }) {
                                        Text(text = "MM")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Alternative preview with fewer pistot for testing
@Preview(showBackground = true, name = "UusiRata - 2 Pistot")
@Composable
fun UusiRataPreview2Pistot() {
    HakupäiväkirjaTheme {
        val mockUiState = TrainingSessionUiState(
            selectedPistot = 3,
            maxPistot = 6,
            pistoStates = mapOf(
                0 to PistoUiState(
                    pistoIndex = 0,
                    currentMode = PistoMode.DEFAULT,
                    selectedPistot = 2
                ),
                1 to PistoUiState(
                    pistoIndex = 1,
                    currentMode = PistoMode.MM,
                    haukut = "5",
                    avut = "Pupu",
                    palkka = "Lelu",
                    selectedPistot = 3
                ),
                2 to PistoUiState(
                    pistoIndex = 1,
                    currentMode = PistoMode.MM,
                    haukut = "5",
                    avut = "Pupu",
                    palkka = "Lelu",
                    selectedPistot = 3
                )
            )
        )

        UusiRata(
            uiState = mockUiState,
            onPistoModeChange = { _, _ -> },
            onMMDetailsChange = { _, _, _, _ -> }
        )
    }
}
