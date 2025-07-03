package com.example.hakupivkirja.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.hakupivkirja.model.PistoStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PistoDao {
  @Insert(onConflict= OnConflictStrategy.IGNORE) //updated only from the "kirjaa" button
  suspend fun insert(pisto: PistoStateEntity)

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertAllPistoStates(pistoStates: List<PistoStateEntity>)

  @Update
  suspend fun update(pisto: PistoStateEntity)

  @Delete
  suspend fun delete(pisto: PistoStateEntity)

  @Query("SELECT * FROM pisto_states WHERE trackId = :trackId")
  fun getPistoStatesByTrackId(trackId: Long): Flow<List<PistoStateEntity>>
}