package com.learningloop.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.learningloop.app.data.entity.WrongAnswer
import kotlinx.coroutines.flow.Flow

@Dao
interface WrongAnswerDao {

    @Query("SELECT * FROM wrong_answer WHERE user_id = :userId AND master_level < 80 ORDER BY wrong_count DESC, last_wrong_at DESC")
    fun observeActiveWrong(userId: Long = 1L): Flow<List<WrongAnswer>>

    @Query("SELECT * FROM wrong_answer WHERE user_id = :userId AND master_level < 80")
    suspend fun listActiveWrong(userId: Long = 1L): List<WrongAnswer>

    @Query("SELECT * FROM wrong_answer WHERE user_id = :userId AND question_id = :questionId LIMIT 1")
    suspend fun getByUserAndQuestion(userId: Long, questionId: Long): WrongAnswer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(wa: WrongAnswer): Long

    @Update
    suspend fun update(wa: WrongAnswer)

    @Query("DELETE FROM wrong_answer WHERE user_id = :userId AND master_level >= 80")
    suspend fun removeMastered(userId: Long = 1L)

    @Query("SELECT COUNT(*) FROM wrong_answer WHERE user_id = :userId AND master_level < 80")
    suspend fun countActive(userId: Long = 1L): Int

    @Query("SELECT * FROM wrong_answer")
    suspend fun dumpAll(): List<WrongAnswer>

    @Query("DELETE FROM wrong_answer")
    suspend fun clearAll()
}
