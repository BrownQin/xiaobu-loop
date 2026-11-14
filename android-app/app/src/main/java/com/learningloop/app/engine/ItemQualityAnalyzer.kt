package com.learningloop.app.engine

import com.learningloop.app.data.dao.ItemAggregate
import com.learningloop.app.data.dao.LearningRecordDao
import com.learningloop.app.data.dao.QuestionDao
import com.learningloop.app.data.entity.Question

/**
 * 题目质量诊断：把作答证据反过来用于校准题库。
 *
 * 判据只依赖可观测的统计量，不做任何猜测：
 *  - 正确率 ≥ 95% 且样本 ≥ 3：太简单，升级层级或停用
 *  - 正确率 ≤ 25% 且样本 ≥ 3：太难，或题干/答案有误
 *  - 秒答占比过半：疑似背答案或选项可排除
 *
 * 数据不足时不下结论，避免小样本误导家长。
 */
object ItemQualityAnalyzer {

    const val MIN_ATTEMPTS = 3

    enum class Issue { NONE, TOO_EASY, TOO_HARD, FAST_GUESS }

    data class ItemQuality(
        val question: Question,
        val attempts: Int,
        val correct: Int,
        val avgDurationMs: Long,
        val fastGuesses: Int,
        val optionSwitches: Int,
        val answerChanges: Int
    ) {
        val accuracy: Int = if (attempts == 0) 0 else correct * 100 / attempts

        val issue: Issue = when {
            attempts < MIN_ATTEMPTS -> Issue.NONE
            accuracy >= 95 -> Issue.TOO_EASY
            accuracy <= 25 -> Issue.TOO_HARD
            fastGuesses * 2 >= attempts -> Issue.FAST_GUESS
            else -> Issue.NONE
        }

        val issueLabel: String = when (issue) {
            Issue.TOO_EASY -> "偏简单"
            Issue.TOO_HARD -> "偏难 / 可能标错"
            Issue.FAST_GUESS -> "疑似秒答"
            Issue.NONE -> "正常"
        }

        val advice: String = when (issue) {
            Issue.TOO_EASY -> "孩子已稳定掌握，可把层级上调，或减少该题出现频率"
            Issue.TOO_HARD -> "先确认题干和答案是否正确，再考虑下调层级"
            Issue.FAST_GUESS -> "正确但耗时过短，建议下次换个问法再确认一次"
            Issue.NONE -> "证据不足以判断"
        }
    }

    suspend fun analyze(
        questionDao: QuestionDao,
        recordDao: LearningRecordDao,
        userId: Long
    ): List<ItemQuality> {
        val aggregates = recordDao.itemAggregates(userId)
        if (aggregates.isEmpty()) return emptyList()

        val questionMap = questionDao.listAll().associateBy { it.id }
        return aggregates.mapNotNull { agg ->
            val q = questionMap[agg.questionId] ?: return@mapNotNull null
            ItemQuality(
                question = q,
                attempts = agg.attempts,
                correct = agg.correct,
                avgDurationMs = agg.avgDurationMs,
                fastGuesses = agg.fastGuesses,
                optionSwitches = agg.optionSwitches,
                answerChanges = agg.answerChanges
            )
        }.sortedWith(
            compareBy<ItemQuality> { it.issue == Issue.NONE }
                .thenByDescending { it.attempts }
        )
    }

    /** 供导出 / 后续归因使用：把一条作答证据压成一行 JSON 友好的文本 */
    fun evidenceLine(agg: ItemAggregate): String =
        "q=${agg.questionId} n=${agg.attempts} acc=${agg.correct}/${agg.attempts} " +
            "avg=${agg.avgDurationMs}ms fast=${agg.fastGuesses} " +
            "switches=${agg.optionSwitches} changes=${agg.answerChanges}"
}
