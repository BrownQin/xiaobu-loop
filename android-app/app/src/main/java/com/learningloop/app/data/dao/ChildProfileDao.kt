package com.learningloop.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.learningloop.app.data.entity.ChildProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildProfileDao {

    @Query("SELECT * FROM child_profile ORDER BY id ASC")
    fun observeAll(): Flow<List<ChildProfile>>

    @Query("SELECT * FROM child_profile WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<ChildProfile?>

    @Query("SELECT * FROM child_profile WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ChildProfile?

    @Query("SELECT * FROM child_profile ORDER BY id ASC")
    suspend fun listAll(): List<ChildProfile>

    @Query("SELECT COUNT(*) FROM child_profile")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: ChildProfile): Long

    @Update
    suspend fun update(profile: ChildProfile)

    @Delete
    suspend fun delete(profile: ChildProfile)

    @Query("UPDATE child_profile SET total_points = total_points + :delta WHERE id = :id")
    suspend fun addPoints(id: Long, delta: Long)

    @Query("UPDATE child_profile SET total_points = :points WHERE id = :id")
    suspend fun setPoints(id: Long, points: Long)

    @Query("DELETE FROM child_profile")
    suspend fun clearAll()
}
