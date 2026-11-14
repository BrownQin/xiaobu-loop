package com.learningloop.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 匿名使用统计的本地待发送队列。
 *
 * 只存事件级信息（事件名、版本、渠道、学段、时间），
 * 不存孩子身份、题目内容或作答内容。
 * 发送成功后立即删除；未发送最多保留 30 天或重试 3 次。
 */
@Entity(
    tableName = "telemetry_event",
    indices = [Index(value = ["sent_at", "attempts"], name = "idx_te_pending")]
)
data class TelemetryEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** activate / day_open / quiz_done / diagnostic_done */
    val event: String,

    /** 分发渠道，如 dev / feishu */
    val channel: String,

    /** App 版本号，如 0.15.0 */
    val version: String,

    /** 孩子学段：KG / PRIMARY_LOW，仅用于难度匹配统计 */
    val stage: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    /** 0 表示尚未发送 */
    @ColumnInfo(name = "sent_at")
    val sentAt: Long = 0L,

    val attempts: Int = 0
)
