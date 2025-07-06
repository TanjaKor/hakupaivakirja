package com.example.hakupivkirja.model.repository

import android.content.Context
import android.util.Log
import com.example.hakupivkirja.model.AppDatabase

// model/repository/RepositoryProvider.kt
object RepositoryProvider {
  private const val TAG = "RepositoryProvider"

  fun provideRepository(context: Context): HakupivkirjaRepository {
    val database = AppDatabase.getDatabase(context)
    Log.d(TAG, "provideRepository called")
    try {
      Log.d(TAG, "Attempting to get AppDatabase instance...")
      // Use applicationContext to be safe
      val database = AppDatabase.getDatabase(context.applicationContext)
      Log.d(TAG, "AppDatabase instance obtained: $database")

      Log.d(TAG, "Attempting to get trainingSessionDao...")
      val dao = database.trainingSessionDao()
      Log.d(TAG, "trainingSessionDao obtained: $dao")

      val repository = HakupivkirjaRepositoryImpl(trainingSessionDao = dao)
      Log.d(TAG, "HakupivkirjaRepositoryImpl instance created: $repository")
      return repository
    } catch (e: Exception) {
      Log.e(TAG, "!!!! ERROR initializing repository !!!!", e)
      // Forcing a crash during debug can be helpful to not miss the error
      throw RuntimeException("Failed to initialize repository. See Logcat for details.", e)
    }
  }
}