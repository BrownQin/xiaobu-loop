package com.learningloop.app.engine

import com.learningloop.app.data.entity.AnchorState

/**
 * 答题信号质量：用「相对自己」的耗时基线识别秒答与犹豫。
 *
 * 目的是防虚假反馈（乱点快速过关），但绝不因此扣分或质问孩子，
 * 只是把这次作答标记为「待验证」，下一篇换表征再确认一次。
 */
object SignalAnalyzer {

    private const val DEFAULT_BASELINE_MS = 5_000L
    private const val MIN_SAMPLES = 5

    /** 个性化基线：样本不足时退回全局默认 */
    fun baselineOf(recentDurations: List<Long>): Long {
        val valid = recentDurations.filter { it in 500L..120_000L }
        if (valid.size < MIN_SAMPLES) return DEFAULT_BASELINE_MS
        return median(valid)
    }

    fun classify(durationMs: Long, baselineMs: Long, correct: Boolean): String {
        val base = if (baselineMs <= 0L) DEFAULT_BASELINE_MS else baselineMs
        return when {
            correct && durationMs < (base * 0.4f).toLong() -> AnchorState.SIGNAL_FAST_SUSPECT
            durationMs > (base * 1.8f).toLong() -> AnchorState.SIGNAL_HESITANT
            else -> AnchorState.SIGNAL_NORMAL
        }
    }

    private fun median(values: List<Long>): Long {
        val sorted = values.sorted()
        val mid = sorted.size / 2
        return if (sorted.size % 2 == 0) (sorted[mid - 1] + sorted[mid]) / 2 else sorted[mid]
    }
}
