package com.example.hakupivkirja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hakupivkirja.model.repository.HakupivkirjaRepository
import com.example.hakupivkirja.model.repository.RepositoryProvider
import com.example.hakupivkirja.ui.components.AppTopBar
import com.example.hakupivkirja.ui.screens.HomeScreen
import com.example.hakupivkirja.ui.theme.HakupäiväkirjaTheme
import com.example.hakupivkirja.ui.viewmodels.TrainingSessionViewModel
import com.example.hakupivkirja.utils.AppViewModelProvider

class MainActivity : ComponentActivity() {
    private lateinit var repository: HakupivkirjaRepository


    // Get the ViewModel instance using the factory from AppViewModelProvider
    private val trainingSessionViewModel: TrainingSessionViewModel by viewModels {
        AppViewModelProvider.factory(repository) // Use your provider here
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = RepositoryProvider.provideRepository(applicationContext)

        // --- End of repository initialization ---
        enableEdgeToEdge()
        setContent {
            // Hoist SnackbarHostState
            val snackbarHostState = remember { SnackbarHostState() }
            // Collect UiState here to observe changes for the Snackbar
            val uiState by trainingSessionViewModel.uiState.collectAsState()

            LaunchedEffect(uiState.saveSuccessMessage) {
                if (uiState.saveSuccessMessage) {
                    snackbarHostState.showSnackbar(
                        message = "Treeni tallennettu!", // Your desired message
                        duration = SnackbarDuration.Short
                    )
                    trainingSessionViewModel.saveMessageShown() // Reset the flag in ViewModel
                }
            }
            HakupäiväkirjaTheme {
                Scaffold(
                    topBar = { AppTopBar(trainingSessionViewModel = trainingSessionViewModel) },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    App(
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.surface),
                        viewModel = trainingSessionViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun App(modifier: Modifier,viewModel: TrainingSessionViewModel) {
    Column(modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HomeScreen(viewModel)
    }
}

