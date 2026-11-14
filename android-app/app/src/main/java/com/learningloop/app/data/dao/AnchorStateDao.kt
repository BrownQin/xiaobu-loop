package com.learningloop.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.learningloop.app.data.entity.AnchorState
import kotlinx.coroutines.flow.Flow

@Dao
interface AnchorStateDao {

    @Query("SELECT * FROM anchor_state WHERE user_id = :userId")
    fun observeByUser(userId: Long): Flow<List<AnchorState>>

    @Query("SELECT * FROM anchor_state WHERE user_id = :userId")
    suspend fun listByUser(userId: Long): List<AnchorState>

    @Query(
        "SELECT * FROM anchor_state WHERE user_id = :userId " +
            "AND anchor_code = :code AND cognitive_level = :level LIMIT 1"
    )
    suspend fun find(userId: Long, code: String, level: String): AnchorState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: AnchorState): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(states: List<AnchorState>)

    @Query("DELETE FROM anchor_state WHERE user_id = :userId")
    suspend fun clearForUser(userId: Long)

    @Query("DELETE FROM anchor_state")
    suspend fun clearAll()
}
