package com.learningloop.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.learningloop.app.data.entity.LearningRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningRecordDao {

    @Insert
    suspend fun insert(record: LearningRecord): Long

    @Query("SELECT * FROM learning_record WHERE user_id = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(userId: Long = 1L, limit: Int = 200): Flow<List<LearningRecord>>

    @Query("SELECT * FROM learning_record WHERE user_id = :userId AND timestamp BETWEEN :from AND :to ORDER BY timestamp ASC")
    suspend fun listBetween(userId: Long, from: Long, to: Long): List<LearningRecord>

    /** 统计区间内的正确率 */
    @Query("SELECT AVG(CASE WHEN is_correct THEN 1.0 ELSE 0.0 END) FROM learning_record WHERE user_id = :userId AND timestamp BETWEEN :from AND :to")
    suspend fun accuracyBetween(userId: Long, from: Long, to: Long): Double?

    /** 连续答对题目数（最近一次答题之前） */
    @Query("SELECT COUNT(*) FROM (SELECT * FROM learning_record WHERE user_id = :userId ORDER BY timestamp DESC LIMIT :limit) WHERE is_correct = 1")
    suspend fun recentCorrectStreakCap(userId: Long = 1L, limit: Int = 10): Int

    @Query("SELECT * FROM learning_record")
    suspend fun dumpAll(): List<LearningRecord>

    /** 最近 N 条该认知层级的耗时，用于计算个性化答题基线 */
    @Query(
        "SELECT duration_ms FROM learning_record WHERE user_id = :userId " +
            "AND cognitive_level = :level ORDER BY timestamp DESC LIMIT :limit"
    )
    suspend fun recentDurations(userId: Long, level: String, limit: Int = 20): List<Long>

    /** 冷却期内做过的题：组卷时避开，解决「多轮重复」 */
    @Query(
        "SELECT DISTINCT question_id FROM learning_record " +
            "WHERE user_id = :userId AND timestamp >= :since"
    )
    suspend fun questionIdsSince(userId: Long, since: Long): List<Long>

    /**
     * 按题目聚合作答证据，供「题目质量」看板使用：
     * 正确率过高 = 太简单，过低 = 太难或题干有误，平均耗时过短 = 疑似刷题。
     */
    @Query(
        "SELECT question_id AS questionId, COUNT(*) AS attempts, " +
            "SUM(CASE WHEN is_correct = 1 THEN 1 ELSE 0 END) AS correct, " +
            "CAST(AVG(duration_ms) AS INTEGER) AS avgDurationMs, " +
            "SUM(CASE WHEN signal_quality = 'FAST_SUSPECT' THEN 1 ELSE 0 END) AS fastGuesses, " +
            "SUM(option_switches) AS optionSwitches, SUM(answer_changes) AS answerChanges " +
            "FROM learning_record WHERE user_id = :userId GROUP BY question_id"
    )
    suspend fun itemAggregates(userId: Long): List<ItemAggregate>

    @Query("DELETE FROM learning_record")
    suspend fun clearAll()
}

/** 题目级聚合结果（Room 直接映射，不需要单独建表） */
data class ItemAggregate(
    val questionId: Long,
    val attempts: Int,
    val correct: Int,
    val avgDurationMs: Long,
    val fastGuesses: Int,
    val optionSwitches: Int,
    val answerChanges: Int
)
