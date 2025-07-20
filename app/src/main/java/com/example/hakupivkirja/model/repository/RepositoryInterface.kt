package com.example.hakupivkirja.model.repository

import com.example.hakupivkirja.model.PistoStateEntity
import com.example.hakupivkirja.model.Terrain
import com.example.hakupivkirja.model.TrainingSession
import com.example.hakupivkirja.model.WeatherEntity

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