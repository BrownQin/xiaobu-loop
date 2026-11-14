package com.learningloop.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 错题表：记录每个孩子每道错题的状态。
 *
 * masterLevel：掌握度 0-100。每次重做答对 +20，答错 -10；
 * 当 masterLevel >= 80 时视为已掌握，可移出错题本。
 */
@Entity(
    tableName = "wrong_answer",
    indices = [
        Index(value = ["user_id"], name = "idx_wa_user"),
        Index(value = ["question_id"], name = "idx_wa_question"),
        Index(value = ["user_id", "question_id"], unique = true, name = "idx_wa_uq")
    ]
)
data class WrongAnswer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "question_id")
    val questionId: Long,

    @ColumnInfo(name = "user_id")
    val userId: Long = 1L, // MVP 单用户默认 1

    @ColumnInfo(name = "user_answer")
    val userAnswer: String,

    @ColumnInfo(name = "wrong_count")
    val wrongCount: Int = 1,

    @ColumnInfo(name = "last_wrong_at")
    val lastWrongAt: Long = System.currentTimeMillis(),

    /** 掌握度 0-100 */
    @ColumnInfo(name = "master_level")
    val masterLevel: Int = 0
)
