package com.tanjan.hakupivkirja.ui.components

import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
    val menuRoutes = setOf("home", "overall")
    val showMenuIcon = currentRoute in menuRoutes

    fun getTitle(): String {
        return when (currentRoute) {
            "home" -> "Hakupäiväkirja"
            "overall" -> {
                uiState.currentTrainingSession?.let { "${it.dogName} - Yhteenveto" }
                    ?: "Yhteenveto"
            }
            else -> "Hakupäiväkirja"
        }
    }

    if (showDialog) {
        IlmaisunValinta(
            uiState = uiState,
            onAlarmTypeChange = { alarmType ->
                trainingSessionViewModel.updateAlarmType(alarmType)
                makeText(context, "Ilmaisutapa vaihdettu: $alarmType", LENGTH_SHORT).show()
                showDialog = false
            },
            onDismissRequest = { showDialog = false },
        )
    }

    // Käytetään Surfacea tuomaan elevation-efekti (varjo)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp,
        color = Color.Transparent // Gradientti piirretään Modifier.backgroundilla
    ) {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            modifier = Modifier.background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.90f)
                    ),
                )
            ),
            title = {
                Text(
                    getTitle(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                if (showMenuIcon) {
                    IconButton(onClick = onMenuIconClick) {
                        Icon(Icons.Filled.Menu, contentDescription = "Avaa valikko")
                    }
                } else {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Palaa takaisin")
                    }
                }
            },
            actions = {
                if (currentRoute == "home") {
                    TextButton(onClick = { showDialog = true }) {
                        uiState.currentTrainingSession?.let {
                            Text(
                                text = "Ilmaisu",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            },
            scrollBehavior = scrollBehavior
        )
    }
}
