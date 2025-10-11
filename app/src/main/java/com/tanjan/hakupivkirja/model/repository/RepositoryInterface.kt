package com.tanjan.hakupivkirja.model.repository

import com.tanjan.hakupivkirja.model.PistoStateEntity
import com.tanjan.hakupivkirja.model.Terrain
import com.tanjan.hakupivkirja.model.TrainingSession
import com.tanjan.hakupivkirja.model.WeatherEntity

interface HakupivkirjaRepository {

//    // PistoState operations
//  suspend fun insertAllPistoStates(pistoStates: List<PistoStateEntity>)

  suspend fun saveTrainingSession(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>
  ): TrainingSession

  suspend fun saveTrainingSessionWithTerrain(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>,
    terrain: Terrain?,
    weather: WeatherEntity?
  ):  Triple<TrainingSession, Terrain?, WeatherEntity?>
}