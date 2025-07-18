package com.example.hakupivkirja.model

import java.time.LocalDate

data class TrainingSessionUiState(
  val currentTrainingSession: TrainingSession? = TrainingSession(),
  val currentTrackId: Long? = null,
  val pistoStates: Map<Int, PistoUiState> = emptyMap(),
  val totalPistoCount: Int = 0,
  val isLoading: Boolean = false,
  val error: String? = null,
  val isSaving: Boolean = false,
  val selectedPistot: Int = 3,
  val maxPistot: Int = 3,
  val alarmType: String? = null,
  val dogName: String? = "",
  val saveSuccessMessage: Boolean = false,
  val selectedDate: LocalDate? = null,
  val overallRating: Int? = null,
  val difficultyRating: Int? = null,
  val terrain: Terrain? = null
  )
