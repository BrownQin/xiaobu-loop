package com.learningloop.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 积分日志：记录每次积分变动来源。
 */
@Entity(
    tableName = "point_log",
    indices = [
        Index(value = ["user_id"], name = "idx_pl_user"),
        Index(value = ["timestamp"], name = "idx_pl_ts")
    ]
)
data class PointLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long = 1L,

    /** 积分数：答对为正，兑换为负 */
    val points: Long,

    /** 来源说明：如 "答对选择题", "连击5题", "进步奖励-拼音" */
    val reason: String,

    val timestamp: Long = System.currentTimeMillis()
)
