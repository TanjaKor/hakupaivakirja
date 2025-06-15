package com.example.hakupivkirja.model

data class TrainingSessionUiState(
  val currentTrainingSession: TrainingSession? = null,
  val currentTrackId: Long? = null,
  val pistoStates: Map<Int, PistoUiState> = emptyMap(),
  val totalPistoCount: Int = 0,
  val isLoading: Boolean = false,
  val error: String? = null,
  val isSaving: Boolean = false,
  val selectedPistot: Int = 3,
  val maxPistot: Int = 3
)
