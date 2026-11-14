package com.learningloop.app.data

import android.content.Context
import android.os.Build
import com.learningloop.app.BuildConfig
import com.learningloop.app.data.entity.Question
import com.learningloop.app.engine.ItemQualityAnalyzer
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 诊断日志导出。
 *
 * 目的是「一次导出，足以复现和分析」：把作答证据、锚点状态、题目质量判断、
 * 设备与版本信息打包成一个 JSON 文件，微信/飞书发给开发者即可分析。
 *
 * 设计约束：
 *  - 只读数据库，不写入、不修改任何用户数据；
 *  - 记录上限 3000 条，避免文件过大；
 *  - 不含姓名以外的隐私字段（姓名是家长自己填的昵称，用于区分孩子）。
 */
object DiagnosticsExporter {

    private const val MAX_RECORDS = 3000

    suspend fun build(context: Context, db: AppDatabase): String {
        val children = db.childProfileDao().listAll()
        val records = db.learningRecordDao().dumpAll().takeLast(MAX_RECORDS)
        val questions = db.questionDao().listAll().associateBy { it.id }
        val stamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())

        val root = JSONObject().apply {
            put("app", "LearningLoop")
            put("schema", 1)
            put("versionName", BuildConfig.VERSION_NAME)
            put("versionCode", BuildConfig.VERSION_CODE)
            put("exportedAt", System.currentTimeMillis())
            put("exportedAtText", stamp)
            put("device", JSONObject().apply {
                put("brand", Build.BRAND)
                put("manufacturer", Build.MANUFACTURER)
                put("model", Build.MODEL)
                put("androidRelease", Build.VERSION.RELEASE)
                put("sdk", Build.VERSION.SDK_INT)
            })
            put("counts", JSONObject().apply {
                put("questions", db.questionDao().count())
                put("records", records.size)
                put("children", children.size)
                put("wrongAnswers", db.wrongAnswerDao().countActive())
            })
            put("children", JSONArray().apply {
                children.forEach { c ->
                    put(JSONObject().apply {
                        put("id", c.id)
                        put("name", c.name)
                        put("grade", c.grade)
                        put("stage", c.stage)
                        put("stars", c.totalPoints)
                        put("anchorStates", JSONArray().apply {
                            db.anchorStateDao().listByUser(c.id).forEach { s ->
                                put(JSONObject().apply {
                                    put("anchorCode", s.anchorCode)
                                    put("anchorName", AnchorCatalog.nameOf(s.anchorCode))
                                    put("level", s.cognitiveLevel)
                                    put("status", s.status)
                                    put("probability", s.probability.toDouble())
                                    put("confidence", s.confidence)
                                    put("attempts", s.attempts)
                                    put("correct", s.correct)
                                    put("updatedAt", s.updatedAt)
                                })
                            }
                        })
                    })
                }
            })
            put("records", JSONArray().apply {
                records.forEach { r ->
                    val q = questions[r.questionId]
                    put(JSONObject().apply {
                        put("ts", r.timestamp)
                        put("userId", r.userId)
                        put("questionId", r.questionId)
                        put("content", q?.content?.take(40) ?: "")
                        put("type", q?.type ?: "")
                        put("anchorCode", r.anchorCode)
                        put("level", r.cognitiveLevel)
                        put("correct", r.isCorrect)
                        put("durationMs", r.durationMs)
                        put("signal", r.signalQuality)
                        put("optionSwitches", r.optionSwitches)
                        put("answerChanges", r.answerChanges)
                    })
                }
            })
            put("itemQuality", JSONArray().apply {
                children.forEach { c ->
                    ItemQualityAnalyzer.analyze(
                        db.questionDao(), db.learningRecordDao(), c.id
                    ).filter { it.issue != ItemQualityAnalyzer.Issue.NONE }.forEach { iq ->
                        put(JSONObject().apply {
                            put("userId", c.id)
                            put("questionId", iq.question.id)
                            put("content", iq.question.content.take(40))
                            put("anchorCode", iq.question.anchorCode)
                            put("level", iq.question.cognitiveLevel)
                            put("attempts", iq.attempts)
                            put("accuracy", iq.accuracy)
                            put("avgDurationMs", iq.avgDurationMs)
                            put("fastGuesses", iq.fastGuesses)
                            put("optionSwitches", iq.optionSwitches)
                            put("answerChanges", iq.answerChanges)
                            put("issue", iq.issue.name)
                        })
                    }
                }
            })
        }
        return root.toString(2)
    }

    /** 文件名：伴学诊断-0.12.0-20261113-1830.json */
    fun fileName(): String {
        val t = SimpleDateFormat("yyyyMMdd-HHmm", Locale.CHINA).format(Date())
        return "伴学诊断-${BuildConfig.VERSION_NAME}-$t.json"
    }
}
