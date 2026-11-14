package com.learningloop.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.learningloop.app.data.entity.TelemetryEvent

@Dao
interface TelemetryEventDao {

    @Insert
    suspend fun insert(event: TelemetryEvent): Long

    @Query(
        "SELECT * FROM telemetry_event WHERE sent_at = 0 AND attempts < 3 " +
            "ORDER BY id LIMIT :limit"
    )
    suspend fun pending(limit: Int): List<TelemetryEvent>

    @Query("UPDATE telemetry_event SET sent_at = :sentAt WHERE id = :id")
    suspend fun markSent(id: Long, sentAt: Long)

    @Query("UPDATE telemetry_event SET attempts = attempts + 1 WHERE id = :id")
    suspend fun bumpAttempt(id: Long)

    /** 清理已发送或重试耗尽的事件 */
    @Query("DELETE FROM telemetry_event WHERE sent_at > 0 OR attempts >= 3")
    suspend fun deleteFinished()

    /** 清理超过保留期的历史事件 */
    @Query("DELETE FROM telemetry_event WHERE created_at < :before")
    suspend fun deleteOlderThan(before: Long)

    /** 关闭统计时清空待发送队列 */
    @Query("DELETE FROM telemetry_event")
    suspend fun clear()

    @Query("SELECT COUNT(*) FROM telemetry_event")
    suspend fun count(): Int
}
