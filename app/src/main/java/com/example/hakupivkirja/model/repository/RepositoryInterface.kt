package com.example.hakupivkirja.model.repository

import com.example.hakupivkirja.model.PistoStateEntity
import com.example.hakupivkirja.model.TrainingSession

interface HakupivkirjaRepository {
  // TrainingSession operations
  suspend fun updateTrainingSession(session: TrainingSession)

//    // PistoState operations
//  suspend fun insertAllPistoStates(pistoStates: List<PistoStateEntity>)

  // Combined operations
  suspend fun saveTrainingSession(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>
  )
}