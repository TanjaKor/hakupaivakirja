package com.tanjan.hakupivkirja.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.West
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanjan.hakupivkirja.model.PistoMode
import com.tanjan.hakupivkirja.model.PistoUiState
import com.tanjan.hakupivkirja.model.TrainingSessionUiState
import kotlin.math.ceil

/**
 * Extension function for LazyColumn to render the training track rows.
 * Updated with theme-aware Cards matching modern styling.
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

    items(rowCount) { rowIndex ->
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
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // LEFT PISTO
            if (leftPistoIndex in 0 until selectedPistot) {
                PistoCard(
                    pistoNumber = leftPistoNumber,
                    pistoIndex = leftPistoIndex,
                    uiState = uiState,
                    onPistoModeChange = onPistoModeChange,
                    onHaukutChange = { haukut -> onHaukutChange(leftPistoIndex, haukut) },
                    onAvutChange = { avut -> onAvutChange(leftPistoIndex, avut) },
                    onPalkkaChange = { palkka -> onPalkkaChange(leftPistoIndex, palkka) },
                    onComeToMiddleChange = { comeToMiddle -> onComeToMiddleChange(leftPistoIndex, comeToMiddle)},
                    onIsClosedChange = { isClosed -> onIsClosedChange(leftPistoIndex, isClosed)},
                    onSuoraPalkkaChange = { suoraPalkka -> onSuoraPalkkaChange(leftPistoIndex, suoraPalkka)},
                    onKiintoRullaChange = { kiintoRulla -> onKiintoRullaChange(leftPistoIndex, kiintoRulla)},
                    onIrtorullanSijaintiChange = { irtorullanSijainti -> onIrtorullanSijaintiChange(leftPistoIndex, irtorullanSijainti)},
                    onControlChange = { control -> onControlChange(leftPistoIndex, control)},
                    modifier = Modifier.weight(1f)
                )
            } else {
                Box(modifier = Modifier.weight(1f))
            }

            // RIGHT PISTO
            if (rightPistoIndex in 0 until selectedPistot) {
                PistoCard(
                    pistoNumber = rightPistoNumber,
                    pistoIndex = rightPistoIndex,
                    uiState = uiState,
                    onPistoModeChange = onPistoModeChange,
                    onHaukutChange = { haukut -> onHaukutChange(rightPistoIndex, haukut) },
                    onAvutChange = { avut -> onAvutChange(rightPistoIndex, avut) },
                    onPalkkaChange = { palkka -> onPalkkaChange(rightPistoIndex, palkka) },
                    onComeToMiddleChange = { comeToMiddle -> onComeToMiddleChange(rightPistoIndex, comeToMiddle)},
                    onIsClosedChange = { isClosed -> onIsClosedChange(rightPistoIndex, isClosed)},
                    onSuoraPalkkaChange = { suoraPalkka -> onSuoraPalkkaChange(rightPistoIndex, suoraPalkka)},
                    onKiintoRullaChange = { kiintoRulla -> onKiintoRullaChange(rightPistoIndex, kiintoRulla)},
                    onIrtorullanSijaintiChange = { irtorullanSijainti -> onIrtorullanSijaintiChange(rightPistoIndex, irtorullanSijainti)},
                    onControlChange = { control -> onControlChange(rightPistoIndex, control)},
                    modifier = Modifier.weight(1f)
                )
            } else {
                Box(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun PistoCard(
    pistoNumber: Int,
    pistoIndex: Int,
    uiState: TrainingSessionUiState,
    onPistoModeChange: (Int, PistoMode) -> Unit,
    onHaukutChange: (String) -> Unit,
    onAvutChange: (String) -> Unit,
    onPalkkaChange: (String) -> Unit,
    onComeToMiddleChange: (Boolean) -> Unit,
    onIsClosedChange: (Boolean) -> Unit,
    onSuoraPalkkaChange: (Boolean) -> Unit,
    onKiintoRullaChange: (Boolean) -> Unit,
    onIrtorullanSijaintiChange: (String) -> Unit,
    onControlChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val pistoState = uiState.pistoStates[pistoIndex] ?: PistoUiState(
        pistoIndex = pistoIndex,
        selectedPistot = uiState.selectedPistot
    )

    Card(
        modifier = modifier.height(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            // Header styling based on mode using theme colors
            val headerBrush = when (pistoState.currentMode) {
                PistoMode.MM -> Brush.horizontalGradient(listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primaryContainer
                ))
                PistoMode.DEFAULT -> Brush.horizontalGradient(listOf(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                ))
                PistoMode.TYHJA -> Brush.horizontalGradient(listOf(
                    MaterialTheme.colorScheme.surface,
                    MaterialTheme.colorScheme.surface
                ))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBrush)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pistoNumber.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = when (pistoState.currentMode) {
                        PistoMode.MM -> MaterialTheme.colorScheme.onPrimary
                        PistoMode.DEFAULT -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    }
                )
                if (pistoState.currentMode != PistoMode.DEFAULT) {
                    Icon(
                        Icons.Sharp.West,
                        contentDescription = "takaisin",
                        // KORJAUS: Nuoli on eri väriä kuin numero
                        tint = when (pistoState.currentMode) {
                            PistoMode.MM -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onPistoModeChange(pistoIndex, PistoMode.DEFAULT) }
                    )
                }
            }

            // Content area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ) {
                when (pistoState.currentMode) {
                    PistoMode.MM -> {
                        Pisto(
                            pistoUiState = pistoState,
                            sessionAlarmType = uiState.currentTrainingSession?.alarmType,
                            onHaukutChange = onHaukutChange,
                            onAvutChange = onAvutChange,
                            onPalkkaChange = onPalkkaChange,
                            onComeToMiddleChange = onComeToMiddleChange,
                            onIsClosedChange = onIsClosedChange,
                            onSuoraPalkkaChange = onSuoraPalkkaChange,
                            onKiintoRullaChange = onKiintoRullaChange,
                            onIrtorullanSijaintiChange = onIrtorullanSijaintiChange,
                            onControlChange = onControlChange
                        )
                    }
                    PistoMode.TYHJA -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Tyhjä", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f), fontWeight = FontWeight.Medium)
                        }
                    }
                    PistoMode.DEFAULT -> {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { onPistoModeChange(pistoIndex, PistoMode.TYHJA) }) {
                                Text("Tyhjä", color = MaterialTheme.colorScheme.primary)
                            }
                            TextButton(onClick = { onPistoModeChange(pistoIndex, PistoMode.MM) }) {
                                Text("MM", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
