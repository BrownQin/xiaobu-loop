package com.learningloop.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.learningloop.app.data.entity.PointLog
import kotlinx.coroutines.flow.Flow

@Dao
interface PointLogDao {

    @Insert
    suspend fun insert(log: PointLog): Long

    @Query("SELECT * FROM point_log WHERE user_id = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(userId: Long = 1L, limit: Int = 50): Flow<List<PointLog>>

    @Query("SELECT * FROM point_log WHERE user_id = :userId AND timestamp BETWEEN :from AND :to ORDER BY timestamp ASC")
    suspend fun listBetween(userId: Long, from: Long, to: Long): List<PointLog>

    @Query("SELECT COALESCE(SUM(points), 0) FROM point_log WHERE user_id = :userId")
    suspend fun totalPointsEver(userId: Long = 1L): Long

    @Query("SELECT * FROM point_log")
    suspend fun dumpAll(): List<PointLog>

    @Query("DELETE FROM point_log")
    suspend fun clearAll()
}
