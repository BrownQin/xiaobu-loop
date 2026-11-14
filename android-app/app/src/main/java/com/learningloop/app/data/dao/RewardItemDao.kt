package com.learningloop.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.learningloop.app.data.entity.RewardItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardItemDao {

    @Query("SELECT * FROM reward_item WHERE enabled = 1 ORDER BY sort_order ASC, points ASC")
    fun observeEnabled(): Flow<List<RewardItem>>

    @Query("SELECT * FROM reward_item ORDER BY sort_order ASC, points ASC")
    fun observeAll(): Flow<List<RewardItem>>

    @Query("SELECT * FROM reward_item ORDER BY sort_order ASC, points ASC")
    suspend fun listAll(): List<RewardItem>

    @Query("SELECT COUNT(*) FROM reward_item")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RewardItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RewardItem>)

    @Update
    suspend fun update(item: RewardItem)

    @Delete
    suspend fun delete(item: RewardItem)

    @Query("DELETE FROM reward_item")
    suspend fun clearAll()
}
