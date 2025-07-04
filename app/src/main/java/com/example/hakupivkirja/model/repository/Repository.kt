package com.example.hakupivkirja.model.repository

import com.example.hakupivkirja.model.PistoStateEntity
import com.example.hakupivkirja.model.TrainingSession
import com.example.hakupivkirja.model.dao.TrainingSessionDao
import com.example.hakupivkirja.model.TrainingSessionWithPistoStates

import kotlinx.coroutines.flow.Flow

class HakupivkirjaRepositoryImpl(
  private val trainingSessionDao: TrainingSessionDao,
) : HakupivkirjaRepository {

  override suspend fun updateTrainingSession(session: TrainingSession) =
    trainingSessionDao.updateTrainingSession(session)

  override suspend fun saveTrainingSession(
    trainingSession: TrainingSession,
    pistoStates: List<PistoStateEntity>
  ) {
    trainingSessionDao.insertTrainingSession(trainingSession, pistoStates)
  }
}