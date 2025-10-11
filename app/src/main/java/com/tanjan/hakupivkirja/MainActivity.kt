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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tanjan.hakupivkirja.model.repository.HakupivkirjaRepository
import com.tanjan.hakupivkirja.model.repository.RepositoryProvider
import com.tanjan.hakupivkirja.ui.components.AppTopBar
import com.tanjan.hakupivkirja.ui.screens.HistoryScreen
import com.tanjan.hakupivkirja.ui.screens.HomeScreen
import com.tanjan.hakupivkirja.ui.theme.HakupäiväkirjaTheme
import com.tanjan.hakupivkirja.ui.viewmodels.TrainingSessionViewModel
import com.tanjan.hakupivkirja.utils.AppViewModelProvider
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  private lateinit var repository: HakupivkirjaRepository


  // Get the ViewModel instance using the factory from AppViewModelProvider
//    private val trainingSessionViewModel: TrainingSessionViewModel by viewModels {
//        AppViewModelProvider.factory(repository) // Use your provider here
//    }

  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    repository = RepositoryProvider.provideRepository(applicationContext)
    enableEdgeToEdge()
    setContent {
      // MUUTOS: Koko sovelluksen tila, mukaan lukien navigaatio,
      // on nyt AppNavigation-funktion sisällä.
      // Välitämme repositoryn, jotta ViewModel voidaan luoda oikein.
      AppNavigation(repository = repository)
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(repository: HakupivkirjaRepository) {
  // 1. Luo NavController ja ViewModel täällä.
  val navController = rememberNavController()

  // HUOM: Käytämme AppViewModelProvideriasi ViewModelin luomiseen.
  // Tämä on oikea tapa, kun tehdas (factory) on jo olemassa.
  val trainingSessionViewModel: TrainingSessionViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
    factory = AppViewModelProvider.factory(repository)
  )

  // 2. Kerää tilat (state) täällä, korkeimmalla tasolla.
  val snackbarHostState = remember { SnackbarHostState() }
  val uiState by trainingSessionViewModel.uiState.collectAsState()

  //DrawerState ja CoroutineScope valikon hallintaan
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  //Kuunnellaan nykyistä reittiä valikon itemin valinnan korostamista varten
  val backStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = backStackEntry?.destination?.route

  // Määritellään valikon kohteet listana, jotta niitä on helppo laajentaa
  val navigationItems = listOf(
    "home" to "Koti",
    "overall" to "Yhteenveto"
  )


  // 3. LaunchedEffect pysyy samana, mutta on nyt täällä.
  LaunchedEffect(uiState.saveSuccessMessage) {
    if (uiState.saveSuccessMessage) {
      snackbarHostState.showSnackbar(
        message = "Treeni tallennettu!",
        duration = SnackbarDuration.Short
      )
      trainingSessionViewModel.saveMessageShown()
    }
  }

  HakupäiväkirjaTheme {
    // Tämä varmistaa, että yläpalkki ja snackbar ovat näkyvissä kaikilla sivuilla.
    ModalNavigationDrawer(
      drawerState = drawerState,
      drawerContent = {
        // Tässä määritellään miltä avattu valikko näyttää
        ModalDrawerSheet {
          Spacer(Modifier.height(12.dp))
          navigationItems.forEach { (route, label) ->
            NavigationDrawerItem(
              label = { Text(label) },
              selected = route == currentRoute, // Korostaa aktiivisen näkymän
              onClick = {
                // Sulje valikko
                scope.launch { drawerState.close() }
                // Navigoi valittuun kohteeseen
                navController.navigate(route) {
                  // Varmistaa, ettei back stackiin kerry turhia kopioita
                  popUpTo(navController.graph.startDestinationId)
                  launchSingleTop = true
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
        // 5. NavHost määrittelee, mikä näkymä näytetään reitin perusteella.
        NavHost(
          navController = navController,
          startDestination = "home", // Määritä aloitusnäkymä
          modifier = Modifier
              .padding(innerPadding)
              .background(MaterialTheme.colorScheme.surface)
        ) {
          // Reitti kotinäkymään
          composable("home") {
            HomeScreen(
              trainingSessionViewModel = trainingSessionViewModel
            )
          }
          // Reitti uuteen historia-näkymään
          composable("overall") {
            HistoryScreen()
            // Jos HistoryScreen tarvitsisi oman ViewModelin, luotaisiin se täällä:
            // val historyViewModel: HistoryViewModel = viewModel(...)
            // HistoryScreen(viewModel = historyViewModel)
          }
        }
      }
    }
  }
}

