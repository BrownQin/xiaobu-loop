package com.learningloop.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.learningloop.app.data.entity.Question
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Query("SELECT * FROM question WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Question?

    /** 全量题库（含停用），供题库管理使用 */
    @Query("SELECT * FROM question ORDER BY subject, knowledge_point, difficulty, id")
    fun observeAll(): Flow<List<Question>>

    @Query("SELECT * FROM question WHERE subject = :subject")
    fun observeBySubject(subject: String): Flow<List<Question>>

    @Query("SELECT * FROM question WHERE subject = :subject")
    suspend fun listBySubject(subject: String): List<Question>

    @Query("SELECT * FROM question")
    suspend fun listAll(): List<Question>

    // ---------- 按学段取题：孩子切到幼儿园时只会拿到 KG 题库 ----------

    @Query("SELECT * FROM question WHERE stage = :stage")
    suspend fun listByStage(stage: String): List<Question>

    @Query("SELECT * FROM question WHERE subject = :subject AND stage = :stage")
    suspend fun listBySubjectAndStage(subject: String, stage: String): List<Question>

    @Query(
        "SELECT * FROM question WHERE subject = :subject AND knowledge_point = :kp " +
            "AND stage = :stage AND enabled = 1 ORDER BY RANDOM() LIMIT :limit"
    )
    suspend fun randomBySubjectKpStage(
        subject: String,
        kp: String,
        stage: String,
        limit: Int
    ): List<Question>

    @Query(
        "SELECT * FROM question WHERE knowledge_point = :kp AND stage = :stage " +
            "AND enabled = 1 ORDER BY RANDOM() LIMIT :limit"
    )
    suspend fun randomByKpStage(kp: String, stage: String, limit: Int): List<Question>

    @Query("SELECT * FROM question WHERE subject = :subject AND knowledge_point IN (:knowledgePoints) AND enabled = 1")
    suspend fun listBySubjectAndKnowledgePoints(
        subject: String,
        knowledgePoints: List<String>
    ): List<Question>

    @Query("SELECT * FROM question WHERE knowledge_point = :kp AND difficulty BETWEEN :minDiff AND :maxDiff AND enabled = 1")
    suspend fun listByKnowledgePointAndDifficulty(
        kp: String,
        minDiff: Int,
        maxDiff: Int
    ): List<Question>

    /** 从指定范围内随机抽题，limit 题 */
    @Query("SELECT * FROM question WHERE subject = :subject AND knowledge_point = :kp AND enabled = 1 ORDER BY RANDOM() LIMIT :limit")
    suspend fun randomBySubjectAndKp(subject: String, kp: String, limit: Int): List<Question>

    /** 跨学科按知识点随机抽题（subject 为 null 时使用） */
    @Query("SELECT * FROM question WHERE knowledge_point = :kp AND enabled = 1 ORDER BY RANDOM() LIMIT :limit")
    suspend fun randomByKp(kp: String, limit: Int): List<Question>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<Question>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: Question): Long

    @Update
    suspend fun update(question: Question)

    @Query("UPDATE question SET enabled = :enabled, updated_at = :now WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean, now: Long)

    @Query("DELETE FROM question WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM question")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM question WHERE enabled = 1")
    suspend fun countEnabled(): Int

    /** 按锚点 + 认知层级取启用题目 */
    @Query("SELECT * FROM question WHERE anchor_code = :code AND cognitive_level = :level AND enabled = 1")
    suspend fun listByAnchorLevel(code: String, level: String): List<Question>

    /** 按锚点 + 层级 + 学段取题：幼儿园与小学的锚点题互不串场 */
    @Query(
        "SELECT * FROM question WHERE anchor_code = :code AND cognitive_level = :level " +
            "AND stage = :stage AND enabled = 1"
    )
    suspend fun listByAnchorLevelStage(code: String, level: String, stage: String): List<Question>

    /** 某锚点下全部已标注题目，用于无题时降级 */
    @Query("SELECT * FROM question WHERE anchor_code = :code AND enabled = 1")
    suspend fun listByAnchor(code: String): List<Question>

    @Query("SELECT * FROM question WHERE anchor_code = :code AND stage = :stage AND enabled = 1")
    suspend fun listByAnchorStage(code: String, stage: String): List<Question>

    /** 尚未标注锚点的内置题，用于老用户数据回填 */
    @Query("SELECT * FROM question WHERE anchor_code = '' AND source = 'builtin'")
    suspend fun listUnannotatedBuiltin(): List<Question>

    @Query("DELETE FROM question")
    suspend fun clearAll()
}
