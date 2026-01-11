package com.tanjan.hakupivkirja.model.repository

import android.content.Context
import com.tanjan.hakupivkirja.model.AppDatabase

object RepositoryProvider {
  fun provideRepository(context: Context): HakupivkirjaRepository {
    return try {
      val database = AppDatabase.getDatabase(context.applicationContext)
      val trainingSessionDao = database.trainingSessionDao()
      val terrainDao = database.terrainDao()
      val weatherDao = database.weatherDao()
      val userDao = database.userDao()

      HakupivkirjaRepositoryImpl(
        trainingSessionDao = trainingSessionDao,
        terrainDao = terrainDao,
        weatherDao = weatherDao,
        userDao = userDao
      )
    } catch (e: Exception) {
      throw RuntimeException("Failed to initialize repository. See Logcat for details.", e)
    }
  }
}
