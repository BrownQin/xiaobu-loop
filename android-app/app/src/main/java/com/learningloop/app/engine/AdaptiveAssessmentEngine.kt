package com.learningloop.app.engine

import com.learningloop.app.data.Stage
import com.learningloop.app.data.dao.KnowledgeMasteryDao
import com.learningloop.app.data.dao.QuestionDao
import com.learningloop.app.data.dao.WrongAnswerDao
import com.learningloop.app.data.entity.KnowledgeMastery
import com.learningloop.app.data.entity.Question

/**
 * 自适应测评引擎 —— 极度精炼测评
 * ============================================================
 *
 * 【为什么极度精炼？】
 *  二年级孩子的有效专注窗口只有 10-15 分钟，题海战术会直接消磨学习兴趣。
 *  本引擎每次仅出 5~10 题，核心策略：
 *
 *   1) 优先从「薄弱知识点」抽题（mastery < 60），直接命中痛点；
 *   2) 结合错题本里「最近/次数最多」的错题，让测评同时承担"重做"的功能；
 *   3) 控制总题量上限（默认 10 题），单次测评 3~5 分钟内完成；
 *   4) 对已掌握（mastery >= 80）的知识点，只在题库整体需要凑数时才抽 1 题保持记忆。
 *
 *  这种设计让"测评"与"练习"合二为一，避免反复做简单题，
 *  同时保证每次测评都能输出一份「有实际增量」的掌握度报告。
 */
class AdaptiveAssessmentEngine(
    private val questionDao: QuestionDao,
    private val kmDao: KnowledgeMasteryDao,
    private val wrongAnswerDao: WrongAnswerDao
) {
    /** 测评题量上限 */
    private val maxQuestions = 10

    /** 薄弱阈值：低于该值视为薄弱 */
    private val weakThreshold = 60

    /** 已掌握阈值：高于该值视为掌握 */
    private val masteredThreshold = 80

    /**
     * 生成一份精炼测评。
     *
     * @param subject 学科：chinese / math；null 表示跨学科混合
     * @param targetCount 目标题量，最终返回 <= targetCount
     * @param stage 学段：只取该学段题库，避免 4 岁孩子抽到二年级题
     */
    suspend fun generateAssessment(
        subject: String? = null,
        targetCount: Int = maxQuestions,
        userId: Long = 1L,
        stage: String = Stage.PRIMARY_LOW
    ): List<Question> {
        // Step 1: 取薄弱知识点 Top N（优先最薄弱的）
        val allMastery = kmDao.listAll(userId)
            .filter { subject == null || it.knowledgePoint.startsWith(subject) }

        val weakKms = allMastery
            .filter { it.mastery < weakThreshold }
            .sortedBy { it.mastery } // 升序：掌握度最低优先

        // Step 2: 取错题对应的 questionId（用于定向重做）
        val activeWrongs = wrongAnswerDao.listActiveWrong(userId)
        val wrongQuestionIds = activeWrongs.map { it.questionId }.toSet()

        val picked = ArrayList<Question>()
        val seenIds = HashSet<Long>()

        // 2a. 先直接放入错题（最有针对性）——最多 3 题
        wrongQuestionIds.take(3).forEach { qid ->
            questionDao.getById(qid)?.let { q ->
                if (q.stage == stage && q.type == Question.TYPE_CHOICE &&
                    (q.subject == subject || subject == null)
                ) {
                    picked += q
                    seenIds += q.id
                }
            }
        }

        // 2b. 再从薄弱知识点选题，每个薄弱知识点 1-2 题
        weakKms.forEach { km ->
            if (picked.size >= targetCount) return@forEach
            val take = if (km.mastery < 30) 2 else 1
            val qs = if (subject != null) {
                questionDao.randomBySubjectKpStage(subject, km.knowledgePoint, stage, take)
            } else {
                // 跨学科测评：不对学科做限制，避免查询条件为空导致抽不到题
                questionDao.randomByKpStage(km.knowledgePoint, stage, take)
            }
            qs.forEach { q ->
                if (picked.size < targetCount && q.id !in seenIds) {
                    picked += q
                    seenIds += q.id
                }
            }
        }

        // 2c. 还不够，就从题库随机补齐（保持多样性）
        // 注意：subject 为 null（综合测评 / 错题本）时兜底池必须是全量题库，
        // 否则会返回空列表，界面就会永久停在“题库准备中”。
        val remain = targetCount - picked.size
        if (remain > 0) {
            val pool = (if (subject != null) {
                questionDao.listBySubjectAndStage(subject, stage)
            } else {
                questionDao.listByStage(stage)
            }).filter { it.type == Question.TYPE_CHOICE }
            pool.shuffled()
                .filter { it.id !in seenIds && it.difficulty in 2..4 }
                .take(remain)
                .forEach { picked += it; seenIds += it.id }

            // 2d. 二次兜底：中等难度题不够时，放开难度限制
            if (picked.size < targetCount) {
                pool.shuffled()
                    .filter { it.id !in seenIds }
                    .take(targetCount - picked.size)
                    .forEach { picked += it; seenIds += it.id }
            }
        }

        return picked.distinctBy { it.id }.take(targetCount)
    }

    /**
     * 答题后更新知识点掌握度。
     * 答对 +10（上限 100），答错 -15（下限 0），同时刷新 lastPracticedAt。
     */
    suspend fun applyAnswerResult(
        kp: String,
        isCorrect: Boolean,
        userId: Long = 1L
    ): KnowledgeMastery = applyMasteryDelta(kp, if (isCorrect) 10 else -15, userId)

    /**
     * 按增量更新掌握度，支持判分三态：
     *  - 完全正确 +10
     *  - 思路接近 +3（不给满分，避免虚假掌握）
     *  - 未掌握   -15
     */
    suspend fun applyMasteryDelta(
        kp: String,
        delta: Int,
        userId: Long = 1L
    ): KnowledgeMastery {
        val existing = kmDao.get(userId, kp)
        val oldMastery = existing?.mastery ?: 50
        val newMastery = (oldMastery + delta).coerceIn(0, 100)
        val km = (existing ?: KnowledgeMastery(userId = userId, knowledgePoint = kp))
            .copy(mastery = newMastery, lastPracticedAt = System.currentTimeMillis())
        kmDao.upsert(km)
        return km
    }
}
