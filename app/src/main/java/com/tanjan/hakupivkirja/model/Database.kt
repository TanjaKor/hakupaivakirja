package com.tanjan.hakupivkirja.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tanjan.hakupivkirja.model.dao.TerrainDao
import com.tanjan.hakupivkirja.model.dao.TrainingSessionDao
import com.tanjan.hakupivkirja.model.dao.WeatherDao

@Database(
  entities = [PistoStateEntity::class, TrainingSession::class, Terrain::class, WeatherEntity::class],
  version = 9,
  exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun trainingSessionDao(): TrainingSessionDao
  abstract fun terrainDao(): TerrainDao
  abstract fun weatherDao(): WeatherDao

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