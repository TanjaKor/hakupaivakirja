package com.example.hakupivkirja.model.repository

import android.content.Context
import com.example.hakupivkirja.model.AppDatabase

object RepositoryProvider {
  fun provideRepository(context: Context): HakupivkirjaRepository {
    return try {
      val database = AppDatabase.getDatabase(context.applicationContext)
      val trainingSessionDao = database.trainingSessionDao()
      val terrainDao = database.terrainDao()
      HakupivkirjaRepositoryImpl(trainingSessionDao = trainingSessionDao, terrainDao = terrainDao)
    } catch (e: Exception) {
      throw RuntimeException("Failed to initialize repository. See Logcat for details.", e)
    }
  }
}