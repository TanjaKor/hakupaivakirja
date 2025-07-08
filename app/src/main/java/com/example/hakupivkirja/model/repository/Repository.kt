package com.example.hakupivkirja.model.repository

import com.example.hakupivkirja.model.PistoStateEntity
import com.example.hakupivkirja.model.TrainingSession
import com.example.hakupivkirja.model.dao.TrainingSessionDao

class HakupivkirjaRepositoryImpl(
  private val trainingSessionDao: TrainingSessionDao,
) : HakupivkirjaRepository {

  override suspend fun saveTrainingSession(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>
  ): TrainingSession {
    return trainingSessionDao.saveTrainingSession(trainingSession, pistoStates)
  }
}