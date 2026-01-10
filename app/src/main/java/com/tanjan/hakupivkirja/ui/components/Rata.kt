package com.tanjan.hakupivkirja.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.West
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.tanjan.hakupivkirja.model.PistoMode
import com.tanjan.hakupivkirja.model.PistoUiState
import com.tanjan.hakupivkirja.model.TrainingSessionUiState
import kotlin.math.ceil

/**
 * Extension function for LazyColumn to render the training track rows.
 * This approach works better with Android's "Capture more" scrolling screenshot feature.
 */
fun LazyListScope.UusiRataItems(
    uiState: TrainingSessionUiState,
    onPistoModeChange: (Int, PistoMode) -> Unit,
    onHaukutChange: (Int, String) -> Unit,
    onAvutChange: (Int, String) -> Unit,
    onPalkkaChange: (Int, String) -> Unit,
    onComeToMiddleChange: (Int, Boolean) -> Unit,
    onIsClosedChange: (Int, Boolean) -> Unit,
    onSuoraPalkkaChange: (Int, Boolean) -> Unit,
    onKiintoRullaChange: (Int, Boolean) -> Unit,
    onIrtorullanSijaintiChange: (Int, String) -> Unit,
    onControlChange: (Int, Boolean) -> Unit
) {
    val selectedPistot = uiState.selectedPistot
    val rowCount = ceil(selectedPistot / 2.0).toInt()

    // Loop through the rows. We use indices to ensure each row is a separate item.
    items(rowCount) { rowIndex ->
        
        // This helper function now returns the correct solid color based on the mode.
        @Composable
        fun getBackgroundBrush(mode: PistoMode): Brush {
            val color = when (mode) {
                PistoMode.MM -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.onTertiaryContainer
            }
            return Brush.horizontalGradient(colors = listOf(color, color))
        }

        // Invert the index for calculation to ensure bottom-up numbering
        val invertedRowIndex = (rowCount - 1) - rowIndex

        val leftPistoNumber: Int
        val rightPistoNumber: Int

        if (uiState.startFromLeft) {
            leftPistoNumber = invertedRowIndex * 2 + 1
            rightPistoNumber = invertedRowIndex * 2 + 2
        } else {
            leftPistoNumber = invertedRowIndex * 2 + 2
            rightPistoNumber = invertedRowIndex * 2 + 1
        }

        val leftPistoIndex = leftPistoNumber - 1
        val rightPistoIndex = rightPistoNumber - 1

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .padding(vertical = 4.dp)
        ) {
            if (leftPistoIndex in 0 until selectedPistot) {
                val leftPistoState = uiState.pistoStates[leftPistoIndex] ?: PistoUiState(
                    pistoIndex = leftPistoIndex,
                    selectedPistot = selectedPistot
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .padding(horizontal = 5.dp)
                        .background(getBackgroundBrush(leftPistoState.currentMode)),
                    horizontalAlignment = Alignment.End,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(leftPistoNumber.toString(), color = MaterialTheme.colorScheme.onSecondaryContainer)
                        if (leftPistoState.currentMode == PistoMode.MM || leftPistoState.currentMode == PistoMode.TYHJA) {
                            Icon(
                                Icons.Sharp.West,
                                contentDescription = "takasin",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier
                                    .clickable { onPistoModeChange(leftPistoIndex, PistoMode.DEFAULT) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = if (leftPistoState.currentMode == PistoMode.TYHJA)
                            Arrangement.Center
                        else Arrangement.Start
                    ) {
                        when (leftPistoState.currentMode) {
                            PistoMode.MM -> {
                                Pisto(
                                    pistoUiState = leftPistoState,
                                    sessionAlarmType = uiState.currentTrainingSession?.alarmType,
                                    onHaukutChange = { haukut -> onHaukutChange(leftPistoIndex, haukut) },
                                    onAvutChange = { avut -> onAvutChange(leftPistoIndex, avut) },
                                    onPalkkaChange = { palkka -> onPalkkaChange(leftPistoIndex, palkka) },
                                    onComeToMiddleChange = { comeToMiddle -> onComeToMiddleChange(leftPistoIndex, comeToMiddle)},
                                    onIsClosedChange = { isClosed -> onIsClosedChange(leftPistoIndex, isClosed)},
                                    onSuoraPalkkaChange = { suoraPalkka -> onSuoraPalkkaChange(leftPistoIndex, suoraPalkka)},
                                    onKiintoRullaChange = { kiintoRulla -> onKiintoRullaChange(leftPistoIndex, kiintoRulla)},
                                    onIrtorullanSijaintiChange = { irtorullanSijainti -> onIrtorullanSijaintiChange(leftPistoIndex, irtorullanSijainti)},
                                    onControlChange = { control -> onControlChange(leftPistoIndex, control)}
                                )
                            }
                            PistoMode.TYHJA -> {
                                Row(modifier = Modifier
                                    .background(MaterialTheme.colorScheme.onTertiaryContainer)
                                    .fillMaxWidth()
                                    .fillMaxHeight(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "Tyhjä", modifier = Modifier.padding(start = 10.dp), color = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                            }
                            PistoMode.DEFAULT -> {
                                Row(modifier = Modifier
                                    .background(MaterialTheme.colorScheme.onTertiaryContainer)
                                    .fillMaxWidth()
                                    .fillMaxHeight(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                    TextButton(onClick = {
                                        onPistoModeChange(leftPistoIndex, PistoMode.TYHJA)
                                    }) {
                                        Text(text = "Tyhjä")
                                    }
                                    TextButton(onClick = {
                                        onPistoModeChange(leftPistoIndex, PistoMode.MM)
                                    }) {
                                        Text(text = "MM")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f).height(0.dp))
            }

            if (rightPistoIndex in 0 until selectedPistot) {
                val rightPistoState = uiState.pistoStates[rightPistoIndex] ?: PistoUiState(
                    pistoIndex = rightPistoIndex,
                    selectedPistot = selectedPistot
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .padding(horizontal = 5.dp)
                        .background(getBackgroundBrush(rightPistoState.currentMode)),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(rightPistoNumber.toString(), color = MaterialTheme.colorScheme.onSecondaryContainer)
                        if (rightPistoState.currentMode == PistoMode.MM || rightPistoState.currentMode == PistoMode.TYHJA) {
                            Icon(
                                Icons.Sharp.West,
                                contentDescription = "takasin",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .clickable { onPistoModeChange(rightPistoIndex, PistoMode.DEFAULT) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = if (rightPistoState.currentMode == PistoMode.TYHJA)
                            Arrangement.Center
                        else Arrangement.End
                    ) {
                        when (rightPistoState.currentMode) {
                            PistoMode.MM -> {
                                Pisto(
                                    pistoUiState = rightPistoState,
                                    sessionAlarmType = uiState.currentTrainingSession?.alarmType,
                                    onHaukutChange = { haukut -> onHaukutChange(rightPistoIndex, haukut) },
                                    onAvutChange = { avut -> onAvutChange(rightPistoIndex, avut) },
                                    onPalkkaChange = { palkka -> onPalkkaChange(rightPistoIndex, palkka) },
                                    onComeToMiddleChange = { comeToMiddle -> onComeToMiddleChange(rightPistoIndex, comeToMiddle)},
                                    onIsClosedChange = { isClosed -> onIsClosedChange(rightPistoIndex, isClosed)},
                                    onSuoraPalkkaChange = { suoraPalkka -> onSuoraPalkkaChange(rightPistoIndex, suoraPalkka)},
                                    onKiintoRullaChange = { kiintoRulla -> onKiintoRullaChange(rightPistoIndex, kiintoRulla)},
                                    onIrtorullanSijaintiChange = { irtorullanSijainti -> onIrtorullanSijaintiChange(rightPistoIndex, irtorullanSijainti)},
                                    onControlChange = { control -> onControlChange(rightPistoIndex, control)}
                                )
                            }
                            PistoMode.TYHJA -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Tyhjä", modifier = Modifier.padding(start = 10.dp), color = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                            }
                            PistoMode.DEFAULT -> {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                    TextButton(onClick = {
                                        onPistoModeChange(rightPistoIndex, PistoMode.TYHJA)
                                    }) {
                                        Text(text = "Tyhjä")
                                    }
                                    TextButton(onClick = {
                                        onPistoModeChange(rightPistoIndex, PistoMode.MM)
                                    }) {
                                        Text(text = "MM")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f).height(0.dp))
            }
        }
    }
}

/**
 * Original UusiRata component, now implemented using a Column for backwards compatibility 
 * or internal layout.
 */
@Composable
fun UusiRata(
    uiState: TrainingSessionUiState,
    onPistoModeChange: (Int, PistoMode) -> Unit,
    onHaukutChange: (Int, String) -> Unit,
    onAvutChange: (Int, String) -> Unit,
    onPalkkaChange: (Int, String) -> Unit,
    onComeToMiddleChange: (Int, Boolean) -> Unit,
    onIsClosedChange: (Int, Boolean) -> Unit,
    onSuoraPalkkaChange: (Int, Boolean) -> Unit,
    onKiintoRullaChange: (Int, Boolean) -> Unit,
    onIrtorullanSijaintiChange: (Int, String) -> Unit,
    onControlChange: (Int, Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // We reuse the logic by calling a manual loop here if needed, 
        // but for HomeScreen we'll use the UusiRataItems version.
        val selectedPistot = uiState.selectedPistot
        val rowCount = ceil(selectedPistot / 2.0).toInt()

        for (rowIndex in 0 until rowCount) {
            // Helper function logic duplicated here for the legacy component
            @Composable
            fun getBackgroundBrush(mode: PistoMode): Brush {
                val color = when (mode) {
                    PistoMode.MM -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.onTertiaryContainer
                }
                return Brush.horizontalGradient(colors = listOf(color, color))
            }

            val invertedRowIndex = (rowCount - 1) - rowIndex
            val leftPistoNumber: Int
            val rightPistoNumber: Int

            if (uiState.startFromLeft) {
                leftPistoNumber = invertedRowIndex * 2 + 1
                rightPistoNumber = invertedRowIndex * 2 + 2
            } else {
                leftPistoNumber = invertedRowIndex * 2 + 2
                rightPistoNumber = invertedRowIndex * 2 + 1
            }

            val leftPistoIndex = leftPistoNumber - 1
            val rightPistoIndex = rightPistoNumber - 1

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().height(220.dp)
            ) {
                if (leftPistoIndex in 0 until selectedPistot) {
                    val leftPistoState = uiState.pistoStates[leftPistoIndex] ?: PistoUiState(pistoIndex = leftPistoIndex, selectedPistot = selectedPistot)
                    Column(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).padding(horizontal = 5.dp).background(getBackgroundBrush(leftPistoState.currentMode)),
                        horizontalAlignment = Alignment.End,
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp).padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(leftPistoNumber.toString(), color = MaterialTheme.colorScheme.onSecondaryContainer)
                            if (leftPistoState.currentMode == PistoMode.MM || leftPistoState.currentMode == PistoMode.TYHJA) {
                                Icon(Icons.Sharp.West, contentDescription = "takasin", tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.clickable { onPistoModeChange(leftPistoIndex, PistoMode.DEFAULT) })
                            }
                        }
                        Row(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 10.dp).padding(bottom = 8.dp), horizontalArrangement = if (leftPistoState.currentMode == PistoMode.TYHJA) Arrangement.Center else Arrangement.Start) {
                            when (leftPistoState.currentMode) {
                                PistoMode.MM -> Pisto(pistoUiState = leftPistoState, sessionAlarmType = uiState.currentTrainingSession?.alarmType, onHaukutChange = { haukut -> onHaukutChange(leftPistoIndex, haukut) }, onAvutChange = { avut -> onAvutChange(leftPistoIndex, avut) }, onPalkkaChange = { palkka -> onPalkkaChange(leftPistoIndex, palkka) }, onComeToMiddleChange = { comeToMiddle -> onComeToMiddleChange(leftPistoIndex, comeToMiddle)}, onIsClosedChange = { isClosed -> onIsClosedChange(leftPistoIndex, isClosed)}, onSuoraPalkkaChange = { suoraPalkka -> onSuoraPalkkaChange(leftPistoIndex, suoraPalkka)}, onKiintoRullaChange = { kiintoRulla -> onKiintoRullaChange(leftPistoIndex, kiintoRulla)}, onIrtorullanSijaintiChange = { irtorullanSijainti -> onIrtorullanSijaintiChange(leftPistoIndex, irtorullanSijainti)}, onControlChange = { control -> onControlChange(leftPistoIndex, control)})
                                PistoMode.TYHJA -> Row(modifier = Modifier.background(MaterialTheme.colorScheme.onTertiaryContainer).fillMaxWidth().fillMaxHeight(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) { Text(text = "Tyhjä", modifier = Modifier.padding(start = 10.dp), color = MaterialTheme.colorScheme.onSecondaryContainer) }
                                PistoMode.DEFAULT -> Row(modifier = Modifier.background(MaterialTheme.colorScheme.onTertiaryContainer).fillMaxWidth().fillMaxHeight(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) { TextButton(onClick = { onPistoModeChange(leftPistoIndex, PistoMode.TYHJA) }) { Text(text = "Tyhjä") }; TextButton(onClick = { onPistoModeChange(leftPistoIndex, PistoMode.MM) }) { Text(text = "MM") } }
                            }
                        }
                    }
                } else { Box(modifier = Modifier.weight(1f).height(0.dp)) }
                
                if (rightPistoIndex in 0 until selectedPistot) {
                    val rightPistoState = uiState.pistoStates[rightPistoIndex] ?: PistoUiState(pistoIndex = rightPistoIndex, selectedPistot = selectedPistot)
                    Column(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).padding(horizontal = 5.dp).background(getBackgroundBrush(rightPistoState.currentMode)), horizontalAlignment = Alignment.End) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp).padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(rightPistoNumber.toString(), color = MaterialTheme.colorScheme.onSecondaryContainer)
                            if (rightPistoState.currentMode == PistoMode.MM || rightPistoState.currentMode == PistoMode.TYHJA) {
                                Icon(Icons.Sharp.West, contentDescription = "takasin", tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.padding(horizontal = 8.dp).clickable { onPistoModeChange(rightPistoIndex, PistoMode.DEFAULT) })
                            }
                        }
                        Row(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 10.dp).padding(bottom = 8.dp), horizontalArrangement = if (rightPistoState.currentMode == PistoMode.TYHJA) Arrangement.Center else Arrangement.End) {
                            when (rightPistoState.currentMode) {
                                PistoMode.MM -> Pisto(pistoUiState = rightPistoState, sessionAlarmType = uiState.currentTrainingSession?.alarmType, onHaukutChange = { haukut -> onHaukutChange(rightPistoIndex, haukut) }, onAvutChange = { avut -> onAvutChange(rightPistoIndex, avut) }, onPalkkaChange = { palkka -> onPalkkaChange(rightPistoIndex, palkka) }, onComeToMiddleChange = { comeToMiddle -> onComeToMiddleChange(rightPistoIndex, comeToMiddle)}, onIsClosedChange = { isClosed -> onIsClosedChange(rightPistoIndex, isClosed)}, onSuoraPalkkaChange = { suoraPalkka -> onSuoraPalkkaChange(rightPistoIndex, suoraPalkka)}, onKiintoRullaChange = { kiintoRulla -> onKiintoRullaChange(rightPistoIndex, kiintoRulla)}, onIrtorullanSijaintiChange = { irtorullanSijainti -> onIrtorullanSijaintiChange(rightPistoIndex, irtorullanSijainti)}, onControlChange = { control -> onControlChange(rightPistoIndex, control)})
                                PistoMode.TYHJA -> Row(modifier = Modifier.fillMaxWidth().fillMaxHeight(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) { Text(text = "Tyhjä", modifier = Modifier.padding(start = 10.dp), color = MaterialTheme.colorScheme.onSecondaryContainer) }
                                PistoMode.DEFAULT -> Row(modifier = Modifier.fillMaxWidth().fillMaxHeight(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) { TextButton(onClick = { onPistoModeChange(rightPistoIndex, PistoMode.TYHJA) }) { Text(text = "Tyhjä") }; TextButton(onClick = { onPistoModeChange(rightPistoIndex, PistoMode.MM) }) { Text(text = "MM") } }
                            }
                        }
                    }
                } else { Box(modifier = Modifier.weight(1f).height(0.dp)) }
            }
        }
    }
}
