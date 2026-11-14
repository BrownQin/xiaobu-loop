package com.learningloop.app.llm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * LLM 客户端 —— 低成本策略 + 离线降级
 * ============================================================
 *
 * 【低成本策略】
 *  做题过程 **完全本地**，从不调用 LLM。
 *  只有以下 3 类低频任务才会触发网络请求：
 *   1) 家长点击"生成本周测评报告"；
 *   2) 家长查看薄弱点时要"生成干预方案"；
 *   3) 孩子答完题后可选"给我一句鼓励语"。
 *
 *  日均调用量 < 10 次，完全可以走免费 API key。
 *
 * 【离线降级】
 *  没有网络、API key 未配置、或超时（5s）时，LLMClient 会自动回退到
 *  [fallbackGentleFeedback] / [fallbackAssessmentReport] 的本地规则输出，
 *  保证功能永不中断。
 */
class LLMClient(
    /**
     * 在线接口：MVP 阶段可填真实的低成本 endpoint（如 Ollama 本地 / 豆包 / 通义千问）。
     * 为了让项目可编译，这里不硬编码 key，启动时注入或读 BuildConfig。
     */
    private val endpoint: String? = null,
    private val apiKey: String? = null
) {

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    /**
     * 通用发送。失败时返回 null，由调用方走降级。
     */
    suspend fun chat(prompt: String, system: String = "你是一个温和、专业的小学低年级学习助手。"): String? {
        if (endpoint == null || apiKey == null) return null

        val body = """
        {
          "model": "qwen-turbo",
          "messages": [
            {"role": "system", "content": ${system.jsonQuote()}},
            {"role": "user", "content": ${prompt.jsonQuote()}}
          ],
          "temperature": 0.7
        }
        """.trimIndent()

        val request = Request.Builder()
            .url(endpoint)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) return@use null
                    val raw = resp.body?.string() ?: return@use null
                    // 极简 JSON 解析，只取 content 字段；失败则整体返回 null 走降级
                    extractContent(raw)
                }
            } catch (_: IOException) { null }
        }
    }

    // ==================== 便捷入口 + 离线降级 ====================

    /** 测评诊断报告：失败返回本地规则文本 */
    suspend fun assessmentReport(childName: String, subject: String, total: Int, correct: Int, masteryMap: Map<String, Int>): String {
        val prompt = PromptTemplates.assessmentReport(childName, subject, total, correct, masteryMap)
        val online = chat(prompt)
        return online ?: fallbackAssessmentReport(childName, correct, total, masteryMap)
    }

    /** 干预方案：失败返回本地规则文本 */
    suspend fun interventionPlan(childName: String, weakPoints: List<String>): String {
        val prompt = PromptTemplates.interventionPlan(childName, weakPoints)
        val online = chat(prompt)
        return online ?: fallbackInterventionPlan(weakPoints)
    }

    /** 孩子温和反馈语：失败返回本地规则文本 */
    suspend fun kidFeedback(isCorrect: Boolean, streak: Int, kp: String): String {
        val prompt = PromptTemplates.kidFeedback(isCorrect, streak, kp)
        val online = chat(prompt)
        return online ?: fallbackGentleFeedback(isCorrect, streak, kp)
    }

    // ==================== 离线降级规则 ====================

    private fun fallbackGentleFeedback(isCorrect: Boolean, streak: Int, kp: String): String {
        val congratulates = listOf(
            "哇，答对啦！你真厉害 ✨",
            "太棒了！继续保持 🌟",
            "真聪明！这道题确实有点难哦 🌈"
        )
        val hints = listOf(
            "没关系，再读一遍题目试试~",
            "想一想，这个知识点我们之前练过哦 💪",
            "别着急，先说说你是怎么想的吧"
        )
        return if (isCorrect) {
            buildString {
                append(congratulates.random())
                if (streak >= 3) append(" 连续 $streak 题啦，超稳！")
            }
        } else hints.random()
    }

    private fun fallbackAssessmentReport(
        childName: String, correct: Int, total: Int, masteryMap: Map<String, Int>
    ): String {
        val acc = if (total > 0) correct * 100 / total else 0
        val weakest = masteryMap.minByOrNull { it.value }?.key ?: ""
        val strongest = masteryMap.maxByOrNull { it.value }?.key ?: ""
        return buildString {
            appendLine("【$childName 本周学习小结】")
            appendLine("本次测评正确率约 $acc%。")
            if (strongest.isNotBlank()) appendLine("💪 优势：$strongest 掌握较好。")
            if (weakest.isNotBlank()) appendLine("🎯 接下来重点练：$weakest。")
            appendLine("建议每天做 3-5 道错题重做，保持手感。")
        }
    }

    private fun fallbackInterventionPlan(weakPoints: List<String>): String {
        if (weakPoints.isEmpty()) return "没有明显薄弱点，保持当前节奏即可。"
        val first = weakPoints.first()
        return buildString {
            appendLine("一周小方案 · 针对【$first】")
            appendLine("周一~三：每天 3 道同知识点口算题")
            appendLine("周四~五：做 1 道应用题，说说思路")
            appendLine("周六：玩 10 分钟相关知识点小游戏")
        }
    }

    // ==================== 工具函数 ====================

    /** 极其简易的 JSON content 提取：兼容不同 provider 格式 */
    private fun extractContent(raw: String): String? {
        // 先尝试 OpenAI 风格：choices[0].message.content
        val idx = raw.indexOf("\"content\"")
        if (idx < 0) return null
        val colon = raw.indexOf(':', idx)
        val start = raw.indexOf('"', colon + 1) + 1
        val end = raw.indexOf('"', start)
        if (start <= 0 || end <= start) return null
        return raw.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"")
    }

    private fun String.jsonQuote(): String = "\"" + replace("\\", "\\\\").replace("\"", "\\\"") + "\""
}
