package com.learningloop.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 锚点 × 认知层级的掌握状态。
 *
 * 一个锚点不是一个概率，而是按认知层级拆开的多格状态：
 *  记忆 / 理解 / 应用，每格独立维护 BKT 掌握概率与置信度。
 *
 * status 只暴露三档，不向家长展示小数概率：
 *  UNTOUCHED 未触发 / IN_PROGRESS 攻坚中 / SOLID 已稳固
 */
@Entity(
    tableName = "anchor_state",
    indices = [
        Index(value = ["user_id", "anchor_code", "cognitive_level"], unique = true, name = "idx_as_uq"),
        Index(value = ["user_id"], name = "idx_as_user")
    ]
)
data class AnchorState(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "user_id")
    val userId: Long = 1L,

    @ColumnInfo(name = "anchor_code")
    val anchorCode: String,

    /** MEMORY / UNDERSTANDING / APPLICATION */
    @ColumnInfo(name = "cognitive_level")
    val cognitiveLevel: String,

    /** BKT 掌握概率 0.0 - 1.0 */
    val probability: Float = 0.3f,

    /** 置信度：HIGH / LOW */
    val confidence: String = CONFIDENCE_LOW,

    /** 三档状态：UNTOUCHED / IN_PROGRESS / SOLID */
    val status: String = STATUS_UNTOUCHED,

    /** 该格累计作答次数 */
    val attempts: Int = 0,

    /** 该格累计答对次数 */
    val correct: Int = 0,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val CONFIDENCE_HIGH = "HIGH"
        const val CONFIDENCE_LOW = "LOW"

        const val STATUS_UNTOUCHED = "UNTOUCHED"
        const val STATUS_IN_PROGRESS = "IN_PROGRESS"
        const val STATUS_SOLID = "SOLID"

        const val SIGNAL_NORMAL = "NORMAL"
        const val SIGNAL_FAST_SUSPECT = "FAST_SUSPECT"
        const val SIGNAL_HESITANT = "HESITANT"
    }
}
