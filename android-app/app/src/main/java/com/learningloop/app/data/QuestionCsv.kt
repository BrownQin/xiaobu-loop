package com.learningloop.app.data

import com.learningloop.app.data.entity.Question
import org.json.JSONObject

/**
 * 题库 CSV 交换格式。
 *
 * 设计目标：让题库变成「显性资产」——家长或教研可以直接在飞书表格 / Excel 里编辑，
 * 导出 CSV 后在 App 内导入，不必改代码、不必发版。
 *
 * 表头顺序固定：
 *   id,subject,knowledgePoint,difficulty,type,content,options,answer,explanation,tags,enabled
 *
 * - id 留空 = 新增题目；id 有值 = 更新该题
 * - options 用 `A.选项一|B.选项二` 表示，导入时自动转为 JSON
 * - answer 选择题可填字母（A）或选项原文，导入时自动归一为字母
 * - enabled：1 启用 / 0 停用
 */
object QuestionCsv {

    private val HEADER = listOf(
        "id", "subject", "knowledgePoint", "difficulty", "type",
        "content", "options", "answer", "explanation", "tags",
        "anchorCode", "cognitiveLevel", "variantType", "enabled", "stage"
    )

    data class ImportResult(
        val added: Int,
        val updated: Int,
        val skipped: Int,
        val errors: List<String>
    ) {
        fun summary(): String = buildString {
            append("导入完成：新增 $added 题，更新 $updated 题")
            if (skipped > 0) append("，跳过 $skipped 题")
            if (errors.isNotEmpty()) {
                append("\n").append(errors.take(5).joinToString("\n"))
                if (errors.size > 5) append("\n… 另有 ${errors.size - 5} 条问题")
            }
        }
    }

    /** 导入结果：待落库题目 + 统计 */
    data class Parsed(val questions: List<Question>, val result: ImportResult)

    fun template(): String = buildString {
        appendLine(HEADER.joinToString(","))
        appendLine(",,,,choice,下面哪个字读作 mā？,A.妈|B.马|C.骂,A,妈妈的「妈」读第一声。,,chinese-pinyin-polyphone,MEMORY,TEXT,1,PRIMARY_LOW")
        appendLine(",,chinese-拼音-多音字,2,fill,「长」在「长大」中读什么？,,zhǎng,长有多音：cháng / zhǎng。,,chinese-pinyin-polyphone,UNDERSTANDING,TEXT,1,PRIMARY_LOW")
        appendLine(",,,,choice,数一数，这里有几个苹果？,A.2 个|B.3 个|C.4 个,B,一个一个数：1、2、3。,,kg-count-10,MEMORY,TEXT,1,KG")
    }

    fun export(questions: List<Question>): String = buildString {
        append(HEADER.joinToString(",")).append("\n")
        questions
            .sortedWith(compareBy({ it.subject }, { it.knowledgePoint }, { it.difficulty }, { it.id }))
            .forEach { q ->
                val cells = listOf(
                    if (q.id > 0) q.id.toString() else "",
                    q.subject,
                    q.knowledgePoint,
                    q.difficulty.toString(),
                    q.type,
                    q.content,
                    jsonToOptions(q.options),
                    q.answer,
                    q.explanation,
                    q.tags,
                    q.anchorCode,
                    q.cognitiveLevel,
                    q.variantType,
                    if (q.enabled) "1" else "0",
                    q.stage
                )
                append(cells.joinToString(",") { escapeCsv(it) }).append("\n")
            }
    }

    fun parse(text: String, now: Long = System.currentTimeMillis()): Parsed {
        val rows = parseCsv(text)
        if (rows.isEmpty()) {
            return Parsed(emptyList(), ImportResult(0, 0, 0, listOf("文件为空或无法解析")))
        }

        val header = rows.first().map { it.trim() }
        val index = header.withIndex().associate { it.value.trim().lowercase() to it.index }
        if (!index.containsKey("content")) {
            return Parsed(emptyList(), ImportResult(0, 0, 0, listOf("表头缺少 content 列，请先使用「导出模板」")))
        }

        val questions = mutableListOf<Question>()
        var added = 0
        var updated = 0
        var skipped = 0
        val errors = mutableListOf<String>()

        rows.drop(1).forEachIndexed { i, row ->
            val lineNo = i + 2
            fun col(name: String): String = index[name]?.let { row.getOrNull(it) }?.trim() ?: ""

            val content = col("content")
            if (content.isBlank()) {
                skipped++
                errors += "第 $lineNo 行：题目内容为空，已跳过"
                return@forEachIndexed
            }

            val subject = col("subject").ifBlank { Question.SUBJECT_CHINESE }
            if (subject != Question.SUBJECT_CHINESE && subject != Question.SUBJECT_MATH) {
                skipped++
                errors += "第 $lineNo 行：subject 只能是 chinese 或 math"
                return@forEachIndexed
            }

            val type = col("type").ifBlank { Question.TYPE_CHOICE }
            if (type !in listOf(Question.TYPE_CHOICE, Question.TYPE_FILL, Question.TYPE_WORD)) {
                skipped++
                errors += "第 $lineNo 行：type 只能是 choice / fill / word"
                return@forEachIndexed
            }

            val optionsJson = optionsToJson(col("options"))
            val answer = normalizeAnswer(col("answer"), optionsJson)
            if (answer.isBlank()) {
                skipped++
                errors += "第 $lineNo 行：答案为空，已跳过"
                return@forEachIndexed
            }

            val rawId = col("id").toLongOrNull() ?: 0L
            val difficulty = (col("difficulty").toIntOrNull() ?: 3).coerceIn(1, 5)
            val enabled = col("enabled") != "0"
            val level = col("cognitiveLevel").ifBlank { Question.LEVEL_MEMORY }
                .let { if (it in VALID_LEVELS) it else Question.LEVEL_MEMORY }
            val anchorCode = col("anchorCode")
            // 老 CSV 没有 stage 列，默认按小学低年级处理
            val stage = col("stage").ifBlank { Stage.PRIMARY_LOW }
                .let { if (it in Stage.all) it else Stage.PRIMARY_LOW }

            questions += Question(
                id = rawId,
                subject = subject,
                knowledgePoint = col("knowledgePoint").ifBlank { "$subject-未分类" },
                difficulty = difficulty,
                type = type,
                content = content,
                options = optionsJson,
                answer = answer,
                explanation = col("explanation"),
                tags = col("tags"),
                enabled = enabled,
                source = Question.SOURCE_IMPORTED,
                updatedAt = now,
                anchorCode = anchorCode,
                cognitiveLevel = level,
                variantType = col("variantType"),
                stage = stage
            )
            if (rawId > 0) updated++ else added++
        }

        return Parsed(questions, ImportResult(added, updated, skipped, errors))
    }

    // ---------- 内部工具 ----------

    private val VALID_LEVELS = setOf(
        Question.LEVEL_MEMORY,
        Question.LEVEL_UNDERSTANDING,
        Question.LEVEL_APPLICATION
    )

    private fun escapeCsv(v: String): String =
        if (v.contains(',') || v.contains('"') || v.contains('\n')) {
            "\"" + v.replace("\"", "\"\"") + "\""
        } else v

    /** RFC4180 简化解析：支持引号、转义双引号、字段内换行 */
    private fun parseCsv(text: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        var row = mutableListOf<String>()
        val field = StringBuilder()
        var inQuotes = false
        var i = 0
        val s = text.removePrefix("\uFEFF")

        while (i < s.length) {
            val c = s[i]
            if (inQuotes) {
                when {
                    c == '"' && i + 1 < s.length && s[i + 1] == '"' -> {
                        field.append('"'); i++
                    }
                    c == '"' -> inQuotes = false
                    else -> field.append(c)
                }
            } else {
                when (c) {
                    '"' -> inQuotes = true
                    ',' -> {
                        row.add(field.toString()); field.clear()
                    }
                    '\r' -> Unit
                    '\n' -> {
                        row.add(field.toString()); field.clear()
                        rows.add(row); row = mutableListOf()
                    }
                    else -> field.append(c)
                }
            }
            i++
        }
        if (field.isNotEmpty() || row.isNotEmpty()) {
            row.add(field.toString())
            rows.add(row)
        }
        return rows.filter { r -> r.any { it.isNotBlank() } }
    }

    fun optionsToJson(raw: String): String {
        if (raw.isBlank()) return ""
        val parts = raw.split("|").map { it.trim() }.filter { it.isNotEmpty() }
        if (parts.isEmpty()) return ""
        val sb = StringBuilder("{")
        parts.forEachIndexed { i, part ->
            val m = Regex("^([A-Za-z])[.．:：、)）]\\s*(.*)$").find(part)
            val key = m?.groupValues?.get(1)?.uppercase()
                ?: ('A'.code + i).toChar().toString()
            val value = m?.groupValues?.get(2) ?: part
            if (i > 0) sb.append(',')
            sb.append('"').append(key).append("\":\"").append(escapeJson(value)).append('"')
        }
        sb.append('}')
        return sb.toString()
    }

    fun jsonToOptions(json: String): String {
        if (json.isBlank()) return ""
        return try {
            val o = JSONObject(json)
            o.keys().asSequence().sorted()
                .joinToString("|") { "$it.${o.optString(it)}" }
        } catch (_: Exception) {
            ""
        }
    }

    /** 选择题答案可填字母或选项原文，统一归一为字母 */
    private fun normalizeAnswer(answer: String, optionsJson: String): String {
        val a = answer.trim()
        if (optionsJson.isBlank()) return a
        return try {
            val o = JSONObject(optionsJson)
            val keys = o.keys().asSequence().toList()
            val upper = a.uppercase()
            when {
                keys.contains(upper) -> upper
                else -> keys.firstOrNull { o.optString(it).trim() == a } ?: upper
            }
        } catch (_: Exception) {
            a
        }
    }

    private fun escapeJson(v: String): String = v
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
}
