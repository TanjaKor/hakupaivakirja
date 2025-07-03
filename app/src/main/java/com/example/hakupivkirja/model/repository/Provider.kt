package com.example.hakupivkirja.model.repository

import android.content.Context
import com.example.hakupivkirja.model.AppDatabase

// model/repository/RepositoryProvider.kt
object RepositoryProvider {
  fun provideRepository(context: Context): HakupivkirjaRepository {
    val database = AppDatabase.getDatabase(context)
    return HakupivkirjaRepositoryImpl(
      trackDao = database.trackDao(),
      pistoDao = database.pistoDao(),
      trainingSessionDao = database.trainingSessionDao(),
      weatherDao = database.weatherDao(),
      terrainDao = database.terrainDao()
    )
  }
}