package com.learningloop.app.engine

import android.content.Context
import com.learningloop.app.BuildConfig
import com.learningloop.app.data.AppDatabase
import com.learningloop.app.data.TelemetryStore
import com.learningloop.app.data.entity.TelemetryEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * 匿名使用统计的最小实现。
 *
 * - 只上报事件级字段：event / install_id / version / channel / stage / ts；
 * - Webhook 地址来自构建参数，未配置时完全不发送；
 * - 家长关闭开关后不再入队、不再发送；
 * - 发送失败静默重试，最多 3 次，本地最多保留 30 天。
 */
class TelemetryReporter(
    private val db: AppDatabase,
    private val context: Context
) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .build()

    /** 入队一条事件；开关关闭时直接丢弃 */
    suspend fun record(event: String, stage: String) = withContext(Dispatchers.IO) {
        if (!TelemetryStore.enabled(context)) return@withContext
        db.telemetryEventDao().insert(
            TelemetryEvent(
                event = event,
                channel = BuildConfig.CHANNEL,
                version = BuildConfig.VERSION_NAME,
                stage = stage
            )
        )
    }

    /** 串行发送待发队列，一次最多 50 条 */
    suspend fun flush() = withContext(Dispatchers.IO) {
        if (!TelemetryStore.enabled(context)) return@withContext
        val endpoint = BuildConfig.TELEMETRY_WEBHOOK
        if (endpoint.isBlank()) return@withContext

        val dao = db.telemetryEventDao()
        val installId = TelemetryStore.installId(context)
        dao.deleteFinished()
        dao.deleteOlderThan(System.currentTimeMillis() - RETAIN_MS)

        repeat(MAX_PER_FLUSH) {
            val event = dao.pending(1).firstOrNull() ?: return@withContext
            if (post(endpoint, event, installId)) {
                dao.markSent(event.id, System.currentTimeMillis())
            } else {
                dao.bumpAttempt(event.id)
            }
        }
    }

    private fun post(endpoint: String, event: TelemetryEvent, installId: String): Boolean =
        runCatching {
            val payload = buildJsonObject {
                put("event", event.event)
                put("install_id", installId)
                put("version", event.version)
                put("channel", event.channel)
                put("stage", event.stage)
                put("ts", event.createdAt)
            }.toString()
            val request = Request.Builder()
                .url(endpoint)
                .post(payload.toRequestBody(JSON))
                .build()
            client.newCall(request).execute().use { it.isSuccessful }
        }.getOrDefault(false)

    companion object {
        private const val MAX_PER_FLUSH = 50
        private const val RETAIN_MS = 30L * 24 * 60 * 60 * 1000
        private val JSON = "application/json; charset=utf-8".toMediaType()
    }
}
