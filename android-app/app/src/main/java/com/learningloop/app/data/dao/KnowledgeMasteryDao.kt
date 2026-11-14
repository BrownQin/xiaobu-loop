package com.learningloop.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.learningloop.app.data.entity.KnowledgeMastery
import kotlinx.coroutines.flow.Flow

@Dao
interface KnowledgeMasteryDao {

    @Query("SELECT * FROM knowledge_mastery WHERE user_id = :userId")
    fun observeAll(userId: Long = 1L): Flow<List<KnowledgeMastery>>

    @Query("SELECT * FROM knowledge_mastery WHERE user_id = :userId")
    suspend fun listAll(userId: Long = 1L): List<KnowledgeMastery>

    @Query("SELECT * FROM knowledge_mastery WHERE user_id = :userId AND knowledge_point = :kp LIMIT 1")
    suspend fun get(userId: Long, kp: String): KnowledgeMastery?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(km: KnowledgeMastery): Long

    @Update
    suspend fun update(km: KnowledgeMastery)

    /** 掌握度低于 60 的薄弱知识点 */
    @Query("SELECT * FROM knowledge_mastery WHERE user_id = :userId AND mastery < 60 ORDER BY mastery ASC")
    suspend fun listWeak(userId: Long = 1L): List<KnowledgeMastery>

    /** 随机抽取 n 个薄弱知识点 */
    @Query("SELECT * FROM knowledge_mastery WHERE user_id = :userId AND mastery < 60 ORDER BY RANDOM() LIMIT :limit")
    suspend fun sampleWeak(userId: Long = 1L, limit: Int): List<KnowledgeMastery>

    @Query("SELECT * FROM knowledge_mastery")
    suspend fun dumpAll(): List<KnowledgeMastery>

    @Query("DELETE FROM knowledge_mastery")
    suspend fun clearAll()
}
