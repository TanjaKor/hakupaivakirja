package com.example.hakupivkirja.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hakupivkirja.model.dao.TrainingSessionDao

@Database(
  entities = [PistoStateEntity::class, TrainingSession::class],
  version = 7,
  exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun trainingSessionDao(): TrainingSessionDao


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
          .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}