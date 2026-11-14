package com.learningloop.app.engine

import com.learningloop.app.data.BktParams
import com.learningloop.app.data.entity.AnchorState

/**
 * 简化 BKT：按「锚点 × 认知层级」逐格更新掌握概率。
 *
 * 只做两件事：用一次作答更新后验概率，再把后验概率映射成三档状态。
 * 不向孩子和家长暴露小数概率。
 */
object AnchorBktEngine {

    /** 一次作答后的新掌握概率 */
    fun update(prior: Float, correct: Boolean, params: BktParams): Float {
        val post = if (correct) {
            val num = prior * (1f - params.slip)
            val den = num + (1f - prior) * params.guess
            if (den <= 0f) prior else num / den
        } else {
            val num = prior * params.slip
            val den = num + (1f - prior) * (1f - params.guess)
            if (den <= 0f) prior else num / den
        }
        // 学习率：即使没有作答，也可能缓慢遗忘/成长，这里只做正向学习
        val learned = post + (1f - post) * params.learn
        return learned.coerceIn(0.01f, 0.99f)
    }

    /** 三档状态 */
    fun statusOf(probability: Float, attempts: Int, confidence: String): String = when {
        attempts <= 0 -> AnchorState.STATUS_UNTOUCHED
        probability >= 0.85f && confidence == AnchorState.CONFIDENCE_HIGH ->
            AnchorState.STATUS_SOLID
        else -> AnchorState.STATUS_IN_PROGRESS
    }

    /**
     * 置信度：
     *  - 高：已答对至少 2 次，且本次不是秒答/犹豫这类弱信号
     *  - 低：样本不足，或信号可疑
     */
    fun confidenceOf(correctCount: Int, signalQuality: String, correct: Boolean): String = when {
        !correct -> AnchorState.CONFIDENCE_LOW
        signalQuality != AnchorState.SIGNAL_NORMAL -> AnchorState.CONFIDENCE_LOW
        correctCount >= 2 -> AnchorState.CONFIDENCE_HIGH
        else -> AnchorState.CONFIDENCE_LOW
    }
}
