package com.example.hakupivkirja.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hakupivkirja.model.PistoMode
import com.example.hakupivkirja.model.PistoStateEntity
import com.example.hakupivkirja.model.PistoUiState
import com.example.hakupivkirja.model.TrainingSession
import com.example.hakupivkirja.model.TrainingSessionUiState
import com.example.hakupivkirja.model.repository.HakupivkirjaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrainingSessionViewModel(
  private val repository: HakupivkirjaRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(TrainingSessionUiState())
  val uiState: StateFlow<TrainingSessionUiState> = _uiState.asStateFlow()

  init {
    initializeEmptyTrainingSession()
  }

  // Save training session with pisto states to database
  fun saveTrainingSession() {
    viewModelScope.launch {
      try {
        setSaving(true)
        setError(null)

        val session = _uiState.value.currentTrainingSession
        val pistoStates = convertToEntityStates()

        if (session != null) {
          Log.d("ViewModelSave", "Attempting to save. currentTrainingSession.trackLength = '${session.trackLength}', Full session: $session")
        }


        if (session != null) {
          repository.saveTrainingSession(session, pistoStates)
          Log.d("ViewModelSave", "Repository call completed.")
        }

      } catch (e: Exception) {
        setError("Failed to save training session: ${e.message}")
      } finally {
        setSaving(false)
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
          dogName = "Testi",
          alarmType = "haukku",
          notes = null,
          overallRating = null,
          difficultyRating = null,
          trackLength = ""
        ),
        pistoStates = emptyMap(),
        selectedPistot = 3, // Default minimum
        maxPistot = 3,     // Default maximum
        totalPistoCount = 0,
        isLoading = false,
        isSaving = false,
        error = null
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

  fun updateKiintoRulla(pistoIndex: Int, kiintoRulla: Boolean) {
    updateMMDetails(pistoIndex, kiintoRulla = kiintoRulla)
  }

  fun updateSelectedDate(dateMillis: Long) {
    _uiState.update { currentState ->
      currentState.copy(
        currentTrainingSession = currentState.currentTrainingSession?.copy(
          dateMillis = dateMillis
        ) // If currentTrainingSession is null, initializeNewSessionForm should have handled it
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

  // MODIFIED: This function now updates trackLength within currentTrainingSession
  fun updateTrackLengthAndMaxPistot(trackLengthString: String, correspondingMaxPistot: Int) {
    _uiState.update { currentState ->
      val newSelectedPistot = if (currentState.selectedPistot > correspondingMaxPistot) {
        correspondingMaxPistot
      } else {
        currentState.selectedPistot.coerceAtLeast(0)
      }

      currentState.copy(
        currentTrainingSession = currentState.currentTrainingSession?.copy(
          trackLength = trackLengthString // Update STRING track length IN THE ENTITY
        ) ?: TrainingSession(trackLength = trackLengthString), // Create new if null (should be rare if init is correct)
        maxPistot = correspondingMaxPistot, // Update maxPistot in uiState
        selectedPistot = newSelectedPistot // Update selectedPistot in uiState
        // No separate uiState.trackLength to update anymore
      )
    }
  }

  fun updateAlarmType(alarmType: String?) {
    _uiState.update { currentState ->
      if (currentState.currentTrainingSession == null) {
      }
      currentState.copy(
        currentTrainingSession = currentState.currentTrainingSession?.copy(
          alarmType = alarmType
        )
      )
    }
  }

  // Update selected number of pistot
  fun updateSelectedPistot(newPistot: Int) {
    _uiState.update { currentState ->
      currentState.copy(
        selectedPistot = newPistot,
        totalPistoCount = newPistot
      )
    }
  }

  // Update pisto mode (DEFAULT, TYHJA, MM)
  fun updatePistoMode(pistoIndex: Int, mode: PistoMode) {    _uiState.update { currentState ->
      val updatedPistos = currentState.pistoStates.toMutableMap()
      val currentPisto = updatedPistos[pistoIndex]
        ?: PistoUiState(pistoIndex = pistoIndex, selectedPistot = currentState.selectedPistot)

      updatedPistos[pistoIndex] = currentPisto.copy(currentMode = mode)
      currentState.copy(pistoStates = updatedPistos)
    }
  }

  // Update MM-specific details for a pisto (all MM-related fields)
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
      )
    }
  }

  // Get a specific pisto's state
  fun getPistoState(pistoIndex: Int): PistoUiState? {
    return _uiState.value.pistoStates[pistoIndex]
  }

  // Clear all pisto states (reset to default)
  fun clearAllPistos() {
    _uiState.update { currentState ->
      currentState.copy(pistoStates = emptyMap())
    }
  }

  fun updateMaxPistot(newMaxPistot: Int) {
    _uiState.update { currentState ->
      currentState.copy(
        maxPistot = newMaxPistot,
        selectedPistot = 3 // Reset to minimum when track changes
      )
    }
  }

  // Helper function to update a specific pisto state
  private fun updatePistoState(pistoIndex: Int, update: (PistoUiState) -> PistoUiState) {
    _uiState.update { currentState ->
      val currentPisto = currentState.pistoStates[pistoIndex]
        ?: PistoUiState(pistoIndex = pistoIndex, selectedPistot = currentState.selectedPistot)

      val updatedPistos = currentState.pistoStates.toMutableMap()
      updatedPistos[pistoIndex] = update(currentPisto)

      currentState.copy(pistoStates = updatedPistos)
    }
  }

  // Convert UI state to database entities for saving
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
        isClosed = uiState.isClosed ?: false,
        comeToMiddle = uiState.comeToMiddle,
        trainingSessionId = 0L // Will be set by Room after TrainingSession is inserted
      )
    }
  }

  // Set saving state
  private fun setSaving(isSaving: Boolean) {
    _uiState.update { it.copy(isSaving = isSaving) }
  }

  // Set error state
  private fun setError(error: String?) {
    _uiState.update { it.copy(error = error) }
  }
}