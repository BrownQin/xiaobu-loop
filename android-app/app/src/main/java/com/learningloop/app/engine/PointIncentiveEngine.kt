package com.learningloop.app.engine

import com.learningloop.app.data.dao.ChildProfileDao
import com.learningloop.app.data.dao.KnowledgeMasteryDao
import com.learningloop.app.data.dao.PointLogDao
import com.learningloop.app.data.entity.KnowledgeMastery
import com.learningloop.app.data.entity.PointLog
import kotlin.math.absoluteValue

/**
 * 积分激励引擎
 * ============================================================
 *
 * 积分单位是「颗星」，全部为个位数，低龄孩子能自己数清：
 *
 *   单次答对 = 1 颗
 *     + 难题（difficulty >= 3）  额外 1 颗
 *     + 连对第 3 题             额外 1 颗
 *
 *   突破奖励：知识点掌握度从 <60 跨越到 >=60，+3 颗
 *   周度奖励：10 个以上知识点达标，+5 颗
 *
 * 答错不扣星，但连对计数清零。
 */
class PointIncentiveEngine(
    private val profileDao: ChildProfileDao,
    private val pointLogDao: PointLogDao,
    private val kmDao: KnowledgeMasteryDao
) {

    private val basePoints = 1L

    private companion object {
        const val MAX_MANUAL_ADJUST = 10L
    }

    /**
     * 计算单次答对积分并写入日志 + 更新总积分。
     *
     * @param difficulty 题目难度 1-5
     * @param currentStreak 当前连击数（刚答对这道题之前的连续正确数）
     * @param knowledgePoint 知识点（用于判断进步奖励）
     * @param prevMastery 答题前该知识点的掌握度
     * @param newMastery 答题后该知识点的掌握度
     */
    suspend fun rewardCorrect(
        difficulty: Int,
        currentStreak: Int,
        knowledgePoint: String,
        prevMastery: Int,
        newMastery: Int,
        userId: Long = 1L
    ): Long {
        // 连对第 3 题额外 1 颗（一轮最多触发一次，不会无限累加）
        val streakAfter = currentStreak + 1
        val comboBonus = if (streakAfter == 3) 1L else 0L
        val difficultyBonus = if (difficulty >= 3) 1L else 0L
        val total = basePoints + comboBonus + difficultyBonus

        val reasonBase = buildString {
            append("答对")
            if (comboBonus > 0) append(" · 连对3题")
            if (difficultyBonus > 0) append(" · 挑战题")
        }

        grantPoints(total, reasonBase, userId)

        // 突破奖励：从 <60 跨越到 >=60
        if (prevMastery < 60 && newMastery >= 60) {
            grantPoints(3L, "进步奖励-[$knowledgePoint] 又上一层", userId)
        }

        return total
    }

    /**
     * 周度进步奖励扫描：一周内有 10 个知识点都正向提升 → +500。
     * MVP 阶段简化实现：仅统计当前所有 mastery >= 60 的数量。
     */
    suspend fun maybeWeeklyProgressBonus(userId: Long = 1L): Long {
        val mastered = kmDao.listAll(userId).count { it.mastery >= 60 }
        return if (mastered >= 10) {
            grantPoints(5L, "周度进步奖励：$mastered 个知识点达标", userId)
        } else 0L
    }

    /**
     * 兑换积分。积分不足返回 null；成功返回扣除的积分数。
     */
    suspend fun exchange(cost: Long, itemName: String, userId: Long = 1L): Long? {
        val profile = profileDao.getById(userId) ?: return null
        if (profile.totalPoints < cost) return null
        profileDao.setPoints(userId, profile.totalPoints - cost)
        pointLogDao.insert(PointLog(userId = userId, points = -cost, reason = "兑换:$itemName"))
        return cost
    }

    /**
     * 家长手工发放/扣减积分。
     *
     * - delta 为正表示奖励，为负表示扣减
     * - 单次绝对值上限 10 颗，扣减不会让总积分变成负数
     * - 返回实际生效的积分变化量（可能因余额不足而小于请求值）
     */
    suspend fun manualAdjust(delta: Long, reason: String, userId: Long = 1L): Long {
        if (delta == 0L) return 0L
        val profile = profileDao.getById(userId) ?: return 0L
        val request = delta.coerceIn(-MAX_MANUAL_ADJUST, MAX_MANUAL_ADJUST)
        val applied = if (request > 0) {
            request
        } else {
            -request.absoluteValue.coerceAtMost(profile.totalPoints)
        }
        if (applied == 0L) return 0L
        profileDao.addPoints(userId, applied)
        val label = if (applied > 0) "家长奖励" else "家长扣减"
        pointLogDao.insert(
            PointLog(userId = userId, points = applied, reason = "$label:${reason.ifBlank { "无备注" }}")
        )
        return applied
    }

    /** 计算连击倍率 */
    fun comboMultiplier(streak: Int): Double = when {
        streak >= 10 -> 3.0
        streak >= 5  -> 2.0
        streak >= 3  -> 1.5
        else         -> 1.0
    }

    private suspend fun grantPoints(points: Long, reason: String, userId: Long): Long {
        if (points <= 0) return 0L
        profileDao.addPoints(userId, points)
        pointLogDao.insert(PointLog(userId = userId, points = points, reason = reason))
        return points
    }
}
