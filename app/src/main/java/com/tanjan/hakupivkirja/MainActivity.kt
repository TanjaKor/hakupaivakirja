package com.tanjan.hakupivkirja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tanjan.hakupivkirja.model.repository.HakupivkirjaRepository
import com.tanjan.hakupivkirja.model.repository.RepositoryProvider
import com.tanjan.hakupivkirja.ui.components.AppTopBar
import com.tanjan.hakupivkirja.ui.components.ShareTrainingDialog
import com.tanjan.hakupivkirja.ui.screens.HistoryScreen
import com.tanjan.hakupivkirja.ui.screens.HomeScreen
import com.tanjan.hakupivkirja.ui.screens.LoginScreen
import com.tanjan.hakupivkirja.ui.theme.HakupäiväkirjaTheme
import com.tanjan.hakupivkirja.ui.viewmodels.AuthViewModel
import com.tanjan.hakupivkirja.ui.viewmodels.TrainingSessionViewModel
import com.tanjan.hakupivkirja.utils.AppViewModelProvider
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  private lateinit var repository: HakupivkirjaRepository

  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    repository = RepositoryProvider.provideRepository(applicationContext)
    enableEdgeToEdge()
    setContent {
      AppNavigation(repository = repository)
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(repository: HakupivkirjaRepository) {
  val authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
  val authUiState by authViewModel.uiState.collectAsState()

  HakupäiväkirjaTheme {
    // Jos käyttäjä EI ole kirjautunut, näytetään LoginScreen
    if (authUiState.currentUser == null) {
      LoginScreen(authViewModel = authViewModel)
    } else {
      // Jos käyttäjä ON kirjautunut, näytetään sovelluksen sisältö
      MainAppContent(repository, authViewModel)
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(repository: HakupivkirjaRepository, authViewModel: AuthViewModel) {
  val navController = rememberNavController()

  val trainingSessionViewModel: TrainingSessionViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
    factory = AppViewModelProvider.factory(repository)
  )

  val historyViewModel: com.tanjan.hakupivkirja.ui.viewmodels.HistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
    factory = AppViewModelProvider.factory(repository)
  )

  val snackbarHostState = remember { SnackbarHostState() }
  val uiState by trainingSessionViewModel.uiState.collectAsState()

  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  val backStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = backStackEntry?.destination?.route

  var showShareDialog by remember { mutableStateOf(false) }
  var shareText by remember { mutableStateOf("") }

  val navigationItems = listOf(
    "home" to "Koti",
    "overall" to "Yhteenveto",
    "share" to "Jaa suunnitelma",
    "logout" to "Kirjaudu ulos"
  )

  LaunchedEffect(uiState.saveSuccessMessage) {
    if (uiState.saveSuccessMessage) {
      snackbarHostState.showSnackbar(
        message = "Treeni tallennettu!",
        duration = SnackbarDuration.Short
      )
      trainingSessionViewModel.saveMessageShown()
    }
  }

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      ModalDrawerSheet {
        Spacer(Modifier.height(12.dp))
        navigationItems.forEach { (route, label) ->
          NavigationDrawerItem(
            label = { Text(label) },
            selected = route == currentRoute,
            onClick = {
              scope.launch { 
                drawerState.close() 
                
                when (route) {
                  "share" -> {
                    shareText = trainingSessionViewModel.generateShareText()
                    if (shareText.isNotEmpty()) {
                      showShareDialog = true
                    }
                  }
                  "logout" -> {
                    authViewModel.signOut()
                  }
                  else -> {
                    navController.navigate(route) {
                      popUpTo(navController.graph.startDestinationId)
                      launchSingleTop = true
                    }
                  }
                }
              }
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
          )
        }
      }
    }
  ) {
    Scaffold(
      topBar = {
        AppTopBar(
          trainingSessionViewModel = trainingSessionViewModel,
          navController = navController,
          onMenuIconClick = {
            scope.launch { drawerState.open() }
          })
      },
      snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
      NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.surface)
      ) {
        composable("home") {
          HomeScreen(trainingSessionViewModel = trainingSessionViewModel)
        }
        composable("overall") {
          HistoryScreen(historyViewModel = historyViewModel)
        }
      }

      if (showShareDialog) {
          ShareTrainingDialog(
              shareText = shareText,
              onDismiss = { showShareDialog = false }
          )
      }
    }
  }
}
