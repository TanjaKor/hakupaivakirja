package com.example.hakupivkirja.model.repository

import android.content.Context
import com.example.hakupivkirja.model.AppDatabase

// model/repository/RepositoryProvider.kt
object RepositoryProvider {
  fun provideRepository(context: Context): HakupivkirjaRepository {
    val database = AppDatabase.getDatabase(context)
    return HakupivkirjaRepositoryImpl(
      trainingSessionDao = database.trainingSessionDao(),
    )
  }
}