package com.tanjan.hakupivkirja.ui.components


import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tanjan.hakupivkirja.ui.viewmodels.TrainingSessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    trainingSessionViewModel: TrainingSessionViewModel,
    navController: NavController,
    onMenuIconClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var showDialog by remember { mutableStateOf(false) }
    val uiState by trainingSessionViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isHomeScreen = currentRoute == "home"

    if (showDialog) {
        IlmaisunValinta(
            uiState = uiState,
            onAlarmTypeChange = { alarmType ->
                trainingSessionViewModel.updateAlarmType(alarmType)
                makeText(
                    context,
                    "Ilmaisutapa vaihdettu: $alarmType",
                    LENGTH_SHORT
                ).show()
                showDialog = false
            },
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
            if (currentRoute == "home" || currentRoute == "history") {
                IconButton(onClick = onMenuIconClick) { // MUUTOS
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Avaa valikko"
                    )
                }
            } else {
                // Muualla näytä takaisin-nuoli
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Palaa takaisin"
                    )
                }
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
