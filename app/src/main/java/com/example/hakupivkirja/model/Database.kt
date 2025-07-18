package com.example.hakupivkirja.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hakupivkirja.model.dao.TerrainDao
import com.example.hakupivkirja.model.dao.TrainingSessionDao

@Database(
  entities = [PistoStateEntity::class, TrainingSession::class, Terrain::class],
  version = 8,
  exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun trainingSessionDao(): TrainingSessionDao
  abstract fun terrainDao(): TerrainDao


  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context,
          AppDatabase::class.java,
          "hakupaivakirja_database"
        )
          .fallbackToDestructiveMigration(true)
          .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}