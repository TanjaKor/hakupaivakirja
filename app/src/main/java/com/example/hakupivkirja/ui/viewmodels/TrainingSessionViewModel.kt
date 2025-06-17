package com.example.hakupivkirja.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.hakupivkirja.model.PistoMode
import com.example.hakupivkirja.model.PistoStateEntity
import com.example.hakupivkirja.model.PistoUiState
import com.example.hakupivkirja.model.TrainingSession
import com.example.hakupivkirja.model.TrainingSessionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TrainingSessionViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(TrainingSessionUiState())
  val uiState: StateFlow<TrainingSessionUiState> = _uiState.asStateFlow()

  fun initializeEmptyTrainingSession() {
    _uiState.update { currentState ->
      currentState.copy(
        currentTrainingSession = TrainingSession(
          dateMillis = System.currentTimeMillis(),
          shortDescription = "",
          dogName = "",
          alarmType = null,
          notes = null,
          overallRating = null,
          difficultyRating = null
        ),
        currentTrackId = 1L, // Default track
        pistoStates = emptyMap(),
        totalPistoCount = 0,
        isLoading = false,
        error = null
      )
    }
  }

  fun updateTrainingSession(trainingSession: TrainingSession) {
    _uiState.update { currentState ->
      currentState.copy(currentTrainingSession = trainingSession)
    }
  }

  fun updateSelectedPistot(newPistot: Int) {
    _uiState.update { currentState ->
      currentState.copy(selectedPistot = newPistot)
    }
  }

  // Add this method to your TrainingSessionViewModel class
  fun updateMaxPistot(newMaxPistot: Int) {
    _uiState.update { currentState ->
      currentState.copy(
        maxPistot = newMaxPistot,
        selectedPistot = 3 // Reset to minimum when track changes
      )
    }
  }

  // Update pisto mode (DEFAULT, TYHJA, MM)
  fun updatePistoMode(pistoIndex: Int, mode: PistoMode) {
    _uiState.update { currentState ->
      val updatedPistos = currentState.pistoStates.toMutableMap()

      // Get the existing state for this pistoIndex, or create a new one if it doesn't exist.
      // The new PistoUiState will use its default values for properties other than pistoIndex and currentMode.
      // The selectedPistot from the PistoUiState's default will be used if creating a new one.
      val currentPisto = updatedPistos[pistoIndex]
        ?: PistoUiState(pistoIndex = pistoIndex, selectedPistot = currentState.selectedPistot)

      // Update the mode and put it back in the map
      updatedPistos[pistoIndex] = currentPisto.copy(currentMode = mode)

      currentState.copy(pistoStates = updatedPistos)
    }
  }

  // Update MM-specific details for a pisto
  fun updateMMDetails(pistoIndex: Int, haukut: String?, avut: String?, palkka: String?) {
    _uiState.update { currentState ->
      val currentPisto = currentState.pistoStates[pistoIndex] ?: return@update currentState
      val updatedPisto = currentPisto.copy(
        haukut = haukut,
        avut = avut,
        palkka = palkka
      )
      val updatedPistos = currentState.pistoStates.toMutableMap()
      updatedPistos[pistoIndex] = updatedPisto

      currentState.copy(pistoStates = updatedPistos)
    }
  }

//  // Set alarm type for a specific training
//  fun setAlarmType(trainingSessionIndex: Int, ilmaisuTyyli: String?) {
//    _uiState.update { currentState ->
//      val currentPisto = currentState.pistoStates[pistoIndex] ?: return@update currentState
//      val updatedPisto = currentPisto.copy(ilmaisu = ilmaisuTyyli)
//      val updatedPistos = currentState.pistoStates.toMutableMap()
//      updatedPistos[pistoIndex] = updatedPisto
//
//      currentState.copy(pistoStates = updatedPistos)
//    }
//  }

  // Set decoy praises directly for a specific pisto
  fun setDecoyPraisesDirectly(pistoIndex: Int, suoraPalkka: Boolean) {
    _uiState.update { currentState ->
      val currentPisto = currentState.pistoStates[pistoIndex] ?: return@update currentState
      val updatedPisto = currentPisto.copy(suoraPalkka = suoraPalkka)
      val updatedPistos = currentState.pistoStates.toMutableMap()
      updatedPistos[pistoIndex] = updatedPisto

      currentState.copy(pistoStates = updatedPistos)
    }
  }

  // Set roll type (solid or not) for a specific pisto
  fun setIsRollSolid(pistoIndex: Int, kiintea: Boolean) {
    _uiState.update { currentState ->
      val currentPisto = currentState.pistoStates[pistoIndex] ?: return@update currentState
      val updatedPisto = currentPisto.copy(kiintoRulla = kiintea)
      val updatedPistos = currentState.pistoStates.toMutableMap()
      updatedPistos[pistoIndex] = updatedPisto

      currentState.copy(pistoStates = updatedPistos)
    }
  }

  // Update roll position for a specific pisto
  fun updateRollPosition(pistoIndex: Int, sijainti: String?) {
    _uiState.update { currentState ->
      val currentPisto = currentState.pistoStates[pistoIndex] ?: return@update currentState
      val updatedPisto = currentPisto.copy(irtorullanSijainti = sijainti)
      val updatedPistos = currentState.pistoStates.toMutableMap()
      updatedPistos[pistoIndex] = updatedPisto

      currentState.copy(pistoStates = updatedPistos)
    }
  }

  // Set closed status for a specific pisto
  fun setIsClosed(pistoIndex: Int, isClosed: Boolean) {
    _uiState.update { currentState ->
      val currentPisto = currentState.pistoStates[pistoIndex] ?: return@update currentState
      val updatedPisto = currentPisto.copy(isClosed = isClosed)
      val updatedPistos = currentState.pistoStates.toMutableMap()
      updatedPistos[pistoIndex] = updatedPisto

      currentState.copy(pistoStates = updatedPistos)
    }
  }

  // Get a specific pisto's state
  fun getPistoState(pistoIndex: Int): PistoUiState? {
    return _uiState.value.pistoStates[pistoIndex]
  }

  // Convert UI state to database entities for saving
  fun convertToEntityStates(trackId: Long): List<PistoStateEntity> {
    return _uiState.value.pistoStates.map { (index, uiState) ->
      PistoStateEntity(
        trackId = trackId,
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
        isClosed = uiState.isClosed ?: false
      )
    }
  }

  // Load existing pisto states from database entities
  fun loadFromEntityStates(entities: List<PistoStateEntity>, totalPistoCount: Int) {
    _uiState.update { currentState ->
      val pistoStates = entities.associate { entity ->
        entity.pistoIndex to PistoUiState(
          pistoIndex = entity.pistoIndex,
          currentMode = when (entity.type) {
            "Default" -> PistoMode.DEFAULT
            "Tyhja" -> PistoMode.TYHJA
            "MM" -> PistoMode.MM
            else -> PistoMode.DEFAULT
          },
          haukut = entity.barkAmount?.toString() ?: "",
          suoraPalkka = entity.decoyPraisesDirectly ?: false,
          kiintoRulla = entity.isRollSolid,
          irtorullanSijainti = entity.rollPositionWithDecoy,
          isClosed = entity.isClosed,
          selectedPistot = totalPistoCount
        )
      }

      currentState.copy(
        pistoStates = pistoStates,
        totalPistoCount = totalPistoCount
      )
    }
  }

  // Clear all pisto states (reset to default)
  fun clearAllPistos() {
    _uiState.update { currentState ->
      val clearedPistos = currentState.pistoStates.mapValues { (index, _) ->
        PistoUiState(
          pistoIndex = index,
          currentMode = PistoMode.DEFAULT,
          selectedPistot = currentState.totalPistoCount
        )
      }

      currentState.copy(pistoStates = clearedPistos)
    }
  }

  // Set loading state
  fun setLoading(isLoading: Boolean) {
    _uiState.update { it.copy(isLoading = isLoading) }
  }

  // Set error state
  fun setError(error: String?) {
    _uiState.update { it.copy(error = error) }
  }

  // Set saving state
  fun setSaving(isSaving: Boolean) {
    _uiState.update { it.copy(isSaving = isSaving) }
  }
}
