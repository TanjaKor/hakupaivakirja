package com.example.hakupivkirja.model.repository

import com.example.hakupivkirja.model.PistoStateEntity
import com.example.hakupivkirja.model.Terrain
import com.example.hakupivkirja.model.TrainingSession

interface HakupivkirjaRepository {

//    // PistoState operations
//  suspend fun insertAllPistoStates(pistoStates: List<PistoStateEntity>)

  // Combined operations
  suspend fun saveTrainingSession(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>
  ): TrainingSession

  suspend fun saveTrainingSessionWithTerrain(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>,
    terrain: Terrain?
  ): Pair<TrainingSession, Terrain?>
}