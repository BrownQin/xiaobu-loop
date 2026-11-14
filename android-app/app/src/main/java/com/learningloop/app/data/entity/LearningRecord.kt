package com.learningloop.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 学习记录：每次答题的流水，用于成长曲线分析和积分计算。
 */
@Entity(
    tableName = "learning_record",
    indices = [
        Index(value = ["user_id"], name = "idx_lr_user"),
        Index(value = ["question_id"], name = "idx_lr_question"),
        Index(value = ["timestamp"], name = "idx_lr_ts")
    ]
)
data class LearningRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long = 1L,

    @ColumnInfo(name = "question_id")
    val questionId: Long,

    @ColumnInfo(name = "is_correct")
    val isCorrect: Boolean,

    /** 答题耗时 (毫秒) */
    @ColumnInfo(name = "duration_ms")
    val durationMs: Long,

    /** 答题时间戳 */
    val timestamp: Long = System.currentTimeMillis(),

    /** 所属锚点，答题时冗余存储，便于按锚点回溯证据 */
    @ColumnInfo(name = "anchor_code")
    val anchorCode: String = "",

    /** 认知层级：MEMORY / UNDERSTANDING / APPLICATION */
    @ColumnInfo(name = "cognitive_level")
    val cognitiveLevel: String = Question.LEVEL_MEMORY,

    /** 信号质量：NORMAL / FAST_SUSPECT 秒答待验证 / HESITANT 犹豫 */
    @ColumnInfo(name = "signal_quality")
    val signalQuality: String = "NORMAL",

    /** 选择题里切换选项的次数：切换多说明不确定，是「猜对」的重要证据 */
    @ColumnInfo(name = "option_switches")
    val optionSwitches: Int = 0,

    /** 填空题改答次数：反复改说明掌握不稳 */
    @ColumnInfo(name = "answer_changes")
    val answerChanges: Int = 0
)
