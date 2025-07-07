package com.example.hakupivkirja.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.hakupivkirja.ui.viewmodels.TrainingSessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(trainingSessionViewModel: TrainingSessionViewModel) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var showDialog by remember { mutableStateOf(false) }
    val uiState by trainingSessionViewModel.uiState.collectAsState()

    if (showDialog) {
        IlmaisunValinta(
            uiState = uiState,
            onAlarmTypeChange = {alarmType ->
        trainingSessionViewModel.updateAlarmType(alarmType,)},
            onDismissRequest = { showDialog = false },
        )
    }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        title = {
            Text(
                "Hakupäiväkirja",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { /* Handle menu */ }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        actions = {
            TextButton(onClick = { showDialog = true }) {
                uiState.currentTrainingSession?.let {
                    Text(
                        text = it.dogName,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 18.sp)
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}
