package com.learningloop.app.engine

import com.learningloop.app.data.dao.KnowledgeMasteryDao
import com.learningloop.app.data.dao.LearningRecordDao
import com.learningloop.app.data.entity.KnowledgeMastery
import java.util.Calendar

/**
 * 成长曲线分析器
 * ============================================================
 *
 * 输出维度：
 *  - 周视图：最近 7 天每天的正确率、答题量；
 *  - 月视图：最近 4 周每周的知识点掌握度均值；
 *  - 薄弱点排序：mastery 最低的 Top N 知识点，给家长做雷达图 + 干预建议。
 *
 * 实现上完全基于本地 LearningRecord 和 KnowledgeMastery，零网络依赖。
 */
class GrowthCurveAnalyzer(
    private val recordDao: LearningRecordDao,
    private val kmDao: KnowledgeMasteryDao
) {

    data class DailyPoint(
        /** yyyy-MM-dd */
        val day: String,
        val accuracy: Double,   // 0.0 ~ 1.0
        val count: Int
    )

    data class WeeklySummary(
        /** 该周起始日 yyyy-MM-dd */
        val weekStart: String,
        val avgMastery: Double,
        val questionCount: Int
    )

    data class WeakKpItem(
        val kp: String,
        val mastery: Int,
        val subject: String
    )

    /** 最近 7 天每日正确率（日粒度） */
    suspend fun dailyAccuracyLastNDays(userId: Long = 1L, days: Int = 7): List<DailyPoint> {
        val calendar = Calendar.getInstance()
        val points = ArrayList<DailyPoint>()
        repeat(days) {
            val endOfDay = calendar.timeInMillis
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis

            val records = recordDao.listBetween(userId, startOfDay, endOfDay)
            val acc = if (records.isNotEmpty()) {
                records.count { it.isCorrect }.toDouble() / records.size
            } else 0.0

            val dayStr = buildString {
                val y = calendar.get(Calendar.YEAR)
                val m = calendar.get(Calendar.MONTH) + 1
                val d = calendar.get(Calendar.DAY_OF_MONTH)
                append("$y-${"%02d".format(m)}-${"%02d".format(d)}")
            }
            points.add(DailyPoint(day = dayStr, accuracy = acc, count = records.size))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        return points.reversed()
    }

    /** 最近 N 周的周度汇总 */
    suspend fun weeklySummary(userId: Long = 1L, weeks: Int = 4): List<WeeklySummary> {
        val km = kmDao.listAll(userId)
        val baseMastery = km.map { it.mastery }.average()
            .let { if (it.isNaN()) 0.0 else it }

        val calendar = Calendar.getInstance()
        val summaries = ArrayList<WeeklySummary>()
        repeat(weeks) { i ->
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            calendar.add(Calendar.WEEK_OF_YEAR, -i)
            val weekStartMillis = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val weekEndMillis = weekStartMillis + 7L * 24 * 60 * 60 * 1000

            val count = recordDao.listBetween(userId, weekStartMillis, weekEndMillis).size
            val weekStartStr = buildString {
                val y = calendar.get(Calendar.YEAR)
                val m = calendar.get(Calendar.MONTH) + 1
                val d = calendar.get(Calendar.DAY_OF_MONTH)
                append("$y-${"%02d".format(m)}-${"%02d".format(d)}")
            }
            summaries.add(
                WeeklySummary(
                    weekStart = weekStartStr,
                    avgMastery = baseMastery, // MVP 简化：用全局均值代替每周快照
                    questionCount = count
                )
            )
        }
        return summaries.reversed()
    }

    /**
     * 薄弱点 Top N —— 用于家长端雷达图。
     * subject 从 knowledgePoint 字符串前缀解析（"chinese-..." / "math-..."）。
     */
    suspend fun listWeakKnowledgePoints(
        userId: Long = 1L,
        topN: Int = 6
    ): List<WeakKpItem> {
        return kmDao.listAll(userId)
            .sortedBy { it.mastery }
            .take(topN)
            .map { km: KnowledgeMastery ->
                val subject = when {
                    km.knowledgePoint.startsWith("chinese") -> "语文"
                    km.knowledgePoint.startsWith("math")    -> "数学"
                    else -> "未知"
                }
                WeakKpItem(kp = km.knowledgePoint, mastery = km.mastery, subject = subject)
            }
    }

    /** 整体掌握度均值 */
    suspend fun overallMastery(userId: Long = 1L): Double {
        val all = kmDao.listAll(userId)
        if (all.isEmpty()) return 0.0
        return all.map { it.mastery.toDouble() }.average()
    }
}
