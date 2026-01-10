package com.tanjan.hakupivkirja.ui.viewmodels

//import com.tanjan.hakupivkirja.model.repository.WeatherRepository

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanjan.hakupivkirja.model.PistoMode
import com.tanjan.hakupivkirja.model.PistoStateEntity
import com.tanjan.hakupivkirja.model.PistoUiState
import com.tanjan.hakupivkirja.model.Terrain
import com.tanjan.hakupivkirja.model.TrainingSession
import com.tanjan.hakupivkirja.model.TrainingSessionUiState
import com.tanjan.hakupivkirja.model.WeatherEntity
import com.tanjan.hakupivkirja.model.repository.HakupivkirjaRepository
import com.tanjan.hakupivkirja.model.repository.NetworkWeatherRepository
import com.tanjan.hakupivkirja.network.WeatherDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrainingSessionViewModel(
  private val repository: HakupivkirjaRepository,
//  private val weatherRepository: WeatherRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(TrainingSessionUiState())
  val uiState: StateFlow<TrainingSessionUiState> = _uiState.asStateFlow()

  var weatherData by mutableStateOf<WeatherDetails?>(null)
    private set

  var isLoadingWeather by mutableStateOf(false)
    private set

  var errorMessage by mutableStateOf("")
    private set
//  var weatherUiState: WeatherUiState by mutableStateOf(WeatherUiState.Loading)
//    private set

//  private val _weatherState = MutableLiveData<WeatherEntity?>()
//  val weatherState: LiveData<WeatherEntity?> = _weatherState

  init {
    initializeEmptyTrainingSession()
  }

  // Save/update training session (works for both new and existing)
  // Your main saving logic (can remain private or internal)
  private fun saveTrainingSessionWithTerrainInternal(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>,
    terrain: Terrain? = null,
    weather: WeatherEntity? = null
  ) {
    viewModelScope.launch {
      setSaving(true)
      setError(null)
      try {
        Log.d("ViewModelSave", "Before repo call. Session ID: ${trainingSession.id}, Desc: ${trainingSession.shortDescription}, StartFromLeft: ${trainingSession.startFromLeft}") 
        // Call repository and get the saved session back (with its correct ID)
        val (savedSessionFromDb, savedTerrainFromDb) = repository.saveTrainingSessionWithTerrain(
        trainingSession,
        pistoStates,
        terrain,
          weather
        )

        _uiState.update { currentState ->
          currentState.copy(
            isSaving = false,
            currentTrainingSession = savedSessionFromDb,
            terrain = savedTerrainFromDb,
            error = null,
            saveSuccessMessage = true
          )
        }
      } catch (e: Exception) {
        Log.e("ViewModelSave", "Error saving session: ${e.message}", e)
        setError("Failed to save training session: ${e.message}")
        setSaving(false)
      } finally {
        if (_uiState.value.isSaving) {
          setSaving(false)
        }
      }
    }
  }
  
  // Function to be called by the UI after the message has been shown
  fun saveMessageShown() {
    _uiState.update { currentState ->
      currentState.copy(saveSuccessMessage = false)
    }
  }

  fun setStartFromLeft(startFromLeft: Boolean) {
    _uiState.update { currentState ->
      currentState.copy(
          startFromLeft = startFromLeft,
          currentTrainingSession = currentState.currentTrainingSession?.copy(
            startFromLeft = startFromLeft
          )
      )
    }
  }

  fun saveTrainingPlan() {
    val currentState = _uiState.value
    val currentSession = currentState.currentTrainingSession

    if (currentSession != null) {
      // Ensure the plan session has the current startFromLeft value
      val planSession = currentSession.copy(
        notes = null,
        overallRating = null,
        difficultyRating = null,
        startFromLeft = currentState.startFromLeft 
      )
      val pistoEntities = convertToEntityStates()

      saveTrainingSessionWithTerrainInternal(planSession, pistoEntities, null)
    } else {
      setError("Cannot save plan: No current session.")
    }
  }

  fun saveCompletedTraining(
    rating: Int,
    difficulty: Int,
    notes: String,
    forestThickness: Int,
    moistureLevel: Int,
    altitudeChanges: Int,
    weatherData: WeatherDetails?
  ) {
    val currentState = _uiState.value
    val currentSession = currentState.currentTrainingSession
    if (currentSession != null) {
      val completedSession = currentSession.copy(
        notes = notes,
        overallRating = rating,
        difficultyRating = difficulty,
        startFromLeft = currentState.startFromLeft // Ensure we use current UI state
      )

      val terrainDetails = Terrain(
        trainingSessionId = null,
        forestThickness = forestThickness,
        altitudeChanges = altitudeChanges,
        moistureLevel = moistureLevel
      )

      val weatherDetails = WeatherEntity(
        trainingSessionId = null,
        weatherDescription = weatherData?.weather?.get(0)?.description,
        temperatureCelsius = weatherData?.main?.temp,
        windSpeed = weatherData?.wind?.speed,
        windDirection = weatherData?.wind?.deg?.toString()
      )

      val pistoEntities = convertToEntityStates()
      saveTrainingSessionWithTerrainInternal(completedSession, pistoEntities, terrainDetails, weatherDetails)

    } else {
      setError("Cannot save training: No current session.")
    }
  }

  fun getWeather(trainingLocation: String
  ) {
    viewModelScope.launch {
      try {
        isLoadingWeather = true
        errorMessage = ""
        val weatherRepository = NetworkWeatherRepository()
        weatherData = weatherRepository.getWeather(trainingLocation)
        isLoadingWeather = false
      } catch (e: Exception) {
        Log.e("WeatherViewModel", "Error fetching weather: ${e.message}")
        errorMessage = "Error: ${e.message}"
        isLoadingWeather = false
        weatherData = null
      }
    }
  }

  // Initialize a new empty training session
  fun initializeEmptyTrainingSession() {
    _uiState.update {
      TrainingSessionUiState(
        currentTrainingSession = TrainingSession(
          dateMillis = System.currentTimeMillis(),
          shortDescription = "",
          dogName = "Himpu",
          alarmType = "haukku",
          notes = null,
          overallRating = null,
          difficultyRating = null,
          trackLength = "100m",
          startFromLeft = false
        ),
        pistoStates = emptyMap(),
        selectedPistot = 3,
        maxPistot = 3,
        totalPistoCount = 0,
        isLoading = false,
        isSaving = false,
        error = null,
        startFromLeft = false
      )
    }
  }

  fun updateHaukut(pistoIndex: Int, haukut: String) {
    updateMMDetails(pistoIndex, haukut = haukut)
  }

  fun updateAvut(pistoIndex: Int, avut: String) {
    updateMMDetails(pistoIndex, avut = avut)
  }

  fun updatePalkka(pistoIndex: Int, palkka: String) {
    updateMMDetails(pistoIndex, palkka = palkka)
  }

  fun updateComeToMiddle(pistoIndex: Int, comeToMiddle: Boolean) {
    updateMMDetails(pistoIndex, comeToMiddle = comeToMiddle)
  }

  fun updateIsClosed(pistoIndex: Int, isClosed: Boolean) {
    updateMMDetails(pistoIndex, isClosed = isClosed)
  }

  fun updateSuoraPalkka(pistoIndex: Int, suoraPalkka: Boolean) {
    updateMMDetails(pistoIndex, suoraPalkka = suoraPalkka)
  }

  fun updateIrtorullanSijainti(pistoIndex: Int, irtorullanSijainti: String) {
    updateMMDetails(pistoIndex, irtorullanSijainti = irtorullanSijainti)
  }
  fun updateControl(pistoIndex: Int, control: Boolean) {
    updateMMDetails(pistoIndex, control = control)
  }

  fun updateKiintoRulla(pistoIndex: Int, kiintoRulla: Boolean) {
    updateMMDetails(pistoIndex, kiintoRulla = kiintoRulla)
  }



  fun updateSelectedDate(dateMillis: Long) {
    _uiState.update { currentState ->
      currentState.copy(
        currentTrainingSession = currentState.currentTrainingSession?.copy(
          dateMillis = dateMillis
        )
      )
    }
  }

  fun updatePlanDescription(description: String) {
    _uiState.update { currentState ->
      currentState.copy(
        currentTrainingSession = currentState.currentTrainingSession?.copy(
          shortDescription = description
        )
      )
    }
  }

  fun updateTrackLengthAndMaxPistot(trackLengthString: String, correspondingMaxPistot: Int) {
    _uiState.update { currentState ->
      val newSelectedPistot = if (currentState.selectedPistot > correspondingMaxPistot) {
        correspondingMaxPistot
      } else {
        currentState.selectedPistot.coerceAtLeast(0)
      }

      currentState.copy(
        currentTrainingSession = currentState.currentTrainingSession?.copy(
          trackLength = trackLengthString
        ) ?: TrainingSession(trackLength = trackLengthString),
        maxPistot = correspondingMaxPistot,
        selectedPistot = newSelectedPistot
      )
    }
  }

  fun updateAlarmType(alarmType: String?) {
    _uiState.update { currentState ->
      currentState.copy(
        currentTrainingSession = currentState.currentTrainingSession?.copy(
          alarmType = alarmType
        )
      )
    }
  }

  fun updateSelectedPistot(newPistot: Int) {
    _uiState.update { currentState ->
      currentState.copy(
        selectedPistot = newPistot,
        totalPistoCount = newPistot
      )
    }
  }

  fun updatePistoMode(pistoIndex: Int, mode: PistoMode) {    
    _uiState.update { currentState ->
      val updatedPistos = currentState.pistoStates.toMutableMap()
      val currentPisto = updatedPistos[pistoIndex]
        ?: PistoUiState(pistoIndex = pistoIndex, selectedPistot = currentState.selectedPistot)

      updatedPistos[pistoIndex] = currentPisto.copy(currentMode = mode)
      currentState.copy(pistoStates = updatedPistos)
    }
  }

  fun updateMMDetails(
    pistoIndex: Int,
    haukut: String? = null,
    avut: String? = null,
    palkka: String? = null,
    comeToMiddle: Boolean? = null,
    isClosed: Boolean? = null,
    suoraPalkka: Boolean? = null,
    kiintoRulla: Boolean? = null,
    irtorullanSijainti: String? = null,
    control: Boolean? = null
  ) {
    updatePistoState(pistoIndex) { pisto ->
      pisto.copy(
        haukut = haukut?.trim() ?: pisto.haukut,
        avut = avut?.trim() ?: pisto.avut,
        palkka = palkka?.trim() ?: pisto.palkka,
        comeToMiddle = comeToMiddle ?: pisto.comeToMiddle,
        isClosed = isClosed ?: pisto.isClosed,
        suoraPalkka = suoraPalkka ?: pisto.suoraPalkka,
        kiintoRulla = kiintoRulla ?: pisto.kiintoRulla,
        irtorullanSijainti = irtorullanSijainti?.trim() ?: pisto.irtorullanSijainti,
        control = control ?: pisto.control
      )
    }
  }

  fun getPistoState(pistoIndex: Int): PistoUiState? {
    return _uiState.value.pistoStates[pistoIndex]
  }

  fun clearAllPistos() {
    _uiState.update { currentState ->
      currentState.copy(pistoStates = emptyMap())
    }
  }

  fun updateMaxPistot(newMaxPistot: Int) {
    _uiState.update { currentState ->
      currentState.copy(
        maxPistot = newMaxPistot,
        selectedPistot = 3
      )
    }
  }

  private fun updatePistoState(pistoIndex: Int, update: (PistoUiState) -> PistoUiState) {
    _uiState.update { currentState ->
      val currentPisto = currentState.pistoStates[pistoIndex]
        ?: PistoUiState(pistoIndex = pistoIndex, selectedPistot = currentState.selectedPistot)

      val updatedPistos = currentState.pistoStates.toMutableMap()
      updatedPistos[pistoIndex] = update(currentPisto)

      currentState.copy(pistoStates = updatedPistos)
    }
  }

  private fun convertToEntityStates(): List<PistoStateEntity> {
    return _uiState.value.pistoStates.map { (index, uiState) ->
      PistoStateEntity(
        pistoIndex = index,
        type = when (uiState.currentMode) {
          PistoMode.DEFAULT -> "Default"
          PistoMode.TYHJA -> "Tyhja"
          PistoMode.MM -> "MM"
        },
        help = uiState.avut ?: "",
        praise = uiState.palkka ?: "",
        barkAmount = uiState.haukut?.toIntOrNull(),
        decoyPraisesDirectly = uiState.suoraPalkka,
        isRollSolid = uiState.kiintoRulla,
        rollPositionWithDecoy = uiState.irtorullanSijainti,
        isClosed = uiState.isClosed,
        comeToMiddle = uiState.comeToMiddle,
        control = uiState.control,
        trainingSessionId = 0L
      )
    }
  }

  fun generateShareText(): String {
    val state = _uiState.value
    val session = state.currentTrainingSession ?: return ""
    
    val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(session.dateMillis))
    
    val sb = StringBuilder()
    sb.append("📋 Treenisuunnitelma: ${session.shortDescription ?: "Hakutreeni"}\n")
    sb.append("📅 Päivämäärä: $date\n")
    sb.append("🐕 Koira: ${session.dogName}\n")
    sb.append("📏 Radan pituus: ${session.trackLength}\n")
    sb.append("➡️ Radan aloitus: ${if (state.startFromLeft) "Vasen" else "Oikea"}\n")
    sb.append("🔢 Pistot: ${state.selectedPistot} kpl\n")
    sb.append("\n--- Pistot ---\n")

    val maxPisto = state.selectedPistot-1
    for (i in 0..maxPisto) {
      val pisto = state.pistoStates[i]
      sb.append("\n📍 Pisto ${i+1}: ")
      if (pisto == null) {
        sb.append("Pistoa ei löydy\n")
      } else {
        if (pisto.currentMode == PistoMode.TYHJA) {
          sb.append("Tyhjä\n")
          continue
        }
        if (pisto.currentMode == PistoMode.DEFAULT) {
          sb.append("Ei täytetty\n")
          continue
        }
        if (pisto.currentMode == PistoMode.MM) {
          if (!pisto.avut.isNullOrBlank()) sb.append("\n  - Avut: ${pisto.avut}\n")
          if (!pisto.haukut.isNullOrBlank()) sb.append("  - Haukut: ${pisto.haukut}\n")
          if (!pisto.irtorullanSijainti.isNullOrBlank()) sb.append(" - Irtorullan sijainti: ${pisto.irtorullanSijainti}\n")
          if (!pisto.palkka.isNullOrBlank()) sb.append("  - Palkka: ${pisto.palkka}\n")
          if (pisto.kiintoRulla == true) sb.append("  - Kiintorulla\n")
          if (pisto.suoraPalkka) sb.append("  - Suorapalkka\n")
          if (pisto.isClosed) sb.append("  - Umpipiilo\n")
          if (pisto.control) sb.append("  - Koehallinta\n")
          if (pisto.comeToMiddle) sb.append("  - Sisääntulo\n")
        }
      }
    }
    
    return sb.toString()
  }

  private fun setSaving(isSaving: Boolean) {
    _uiState.update { it.copy(isSaving = isSaving) }
  }

  private fun setError(error: String?) {
    _uiState.update { it.copy(error = error) }
  }
}
