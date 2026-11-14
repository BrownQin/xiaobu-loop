package com.learningloop.app.engine

import com.learningloop.app.data.dao.KnowledgeMasteryDao
import com.learningloop.app.data.dao.QuestionDao
import com.learningloop.app.data.dao.WrongAnswerDao
import com.learningloop.app.data.entity.WrongAnswer

/**
 * 错题闭环引擎 —— "重做 → 变形 → 延伸" 三级循环
 * ============================================================
 *
 * 1) 重做 (Replay)：直接让孩子再做一遍原题，检验"看过解析后是否能立刻做对"；
 * 2) 变形 (Variant)：从同一知识点、同难度的题库中随机换一道同型题，
 *    检验是否真正掌握，而非靠记忆解题；
 * 3) 延伸 (Extend)：变形题答对后，再出一道同知识点但难度 +1 的题，
 *    确保触达该知识点的上限。
 *
 * 触发时机：每次答题判错即进入 Step 1；错题本中 masterLevel 未达 80 的题
 * 会在下次测评前优先进入闭环流程。
 */
class WrongQuestionLoop(
    private val wrongAnswerDao: WrongAnswerDao,
    private val questionDao: QuestionDao,
    private val kmDao: KnowledgeMasteryDao,
    private val assessmentEngine: AdaptiveAssessmentEngine
) {

    /**
     * 登记一道错题。如果已存在，则 wrongCount +1 并回退 masterLevel。
     */
    suspend fun recordWrong(
        questionId: Long,
        userAnswer: String,
        userId: Long = 1L,
        updateMastery: Boolean = true
    ): WrongAnswer {
        val existing = wrongAnswerDao.getByUserAndQuestion(userId, questionId)
        val wa = if (existing != null) {
            existing.copy(
                wrongCount = existing.wrongCount + 1,
                userAnswer = userAnswer,
                lastWrongAt = System.currentTimeMillis(),
                masterLevel = (existing.masterLevel - 10).coerceAtLeast(0)
            )
        } else {
            WrongAnswer(
                questionId = questionId,
                userId = userId,
                userAnswer = userAnswer,
                wrongCount = 1,
                masterLevel = 0
            )
        }
        wrongAnswerDao.insertOrReplace(wa)

        // 同步更新知识点掌握度（调用方已按三态自行更新时传 false，避免重复扣分）
        if (updateMastery) {
            questionDao.getById(questionId)?.let { q ->
                assessmentEngine.applyAnswerResult(q.knowledgePoint, isCorrect = false, userId = userId)
            }
        }
        return wa
    }

    /**
     * 某道错题重做答对。masterLevel +20，对应知识点掌握度 +10。
     * 当 masterLevel >= 80 时视为已掌握，下次启动会被清理出错题本。
     */
    suspend fun markCorrect(
        questionId: Long,
        userId: Long = 1L,
        updateMastery: Boolean = true
    ): WrongAnswer? {
        val wa = wrongAnswerDao.getByUserAndQuestion(userId, questionId) ?: return null
        val updated = wa.copy(
            masterLevel = (wa.masterLevel + 20).coerceIn(0, 100),
            lastWrongAt = System.currentTimeMillis()
        )
        wrongAnswerDao.update(updated)

        if (updateMastery) {
            questionDao.getById(questionId)?.let { q ->
                assessmentEngine.applyAnswerResult(q.knowledgePoint, isCorrect = true, userId = userId)
            }
        }
        return updated
    }

    /**
     * 生成这道错题的下一级练习题。
     *  - phase = REPLAY  → 返回原题（已在调用侧直接使用）
     *  - phase = VARIANT → 返回同知识点、同难度的新题
     *  - phase = EXTEND  → 返回同知识点、难度 +1 的挑战题
     *
     * 返回 null 表示题库中找不到合适题目（Phase 已到顶）。
     */
    suspend fun nextLoopExercise(
        wrongAnswer: WrongAnswer,
        phase: Phase
    ): com.learningloop.app.data.entity.Question? {
        val origin = questionDao.getById(wrongAnswer.questionId) ?: return null
        return when (phase) {
            Phase.REPLAY -> origin
            Phase.VARIANT -> questionDao.randomBySubjectKpStage(
                origin.subject, origin.knowledgePoint, origin.stage, limit = 5
            ).firstOrNull { it.id != origin.id && it.difficulty == origin.difficulty }
            Phase.EXTEND -> questionDao.listByKnowledgePointAndDifficulty(
                origin.knowledgePoint,
                minDiff = (origin.difficulty + 1).coerceAtMost(5),
                maxDiff = 5
            ).shuffled().firstOrNull()
        }
    }

    /**
     * 根据当前 masterLevel 决定应该进入哪个阶段。
     *  0..39 → REPLAY（先做熟原题）
     *  40..79 → VARIANT（变形题）
     *  80+  → EXTEND 或视为已掌握
     */
    fun decidePhase(masterLevel: Int): Phase = when {
        masterLevel < 40 -> Phase.REPLAY
        masterLevel < 80 -> Phase.VARIANT
        else -> Phase.EXTEND
    }

    enum class Phase { REPLAY, VARIANT, EXTEND }
}
