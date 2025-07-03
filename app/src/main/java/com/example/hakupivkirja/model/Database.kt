package com.example.hakupivkirja.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hakupivkirja.model.dao.PistoDao
import com.example.hakupivkirja.model.dao.TerrainDao
import com.example.hakupivkirja.model.dao.TrackDao
import com.example.hakupivkirja.model.dao.TrainingSessionDao
import com.example.hakupivkirja.model.dao.WeatherDao

@Database(
  entities = [TrackEntity::class, PistoStateEntity::class, TrainingSession::class, Weather::class, Terrain::class],
  version = 1,
  exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun trackDao(): TrackDao
  abstract fun pistoDao(): PistoDao
  abstract fun trainingSessionDao(): TrainingSessionDao
  abstract fun weatherDao(): WeatherDao
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
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}