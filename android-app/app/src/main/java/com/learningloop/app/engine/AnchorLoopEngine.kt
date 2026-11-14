package com.learningloop.app.engine

import com.learningloop.app.data.AnchorCatalog
import com.learningloop.app.data.AnchorDef
import com.learningloop.app.data.AppDatabase
import com.learningloop.app.data.GradeLadder
import com.learningloop.app.data.Stage
import com.learningloop.app.data.entity.AnchorState
import com.learningloop.app.data.entity.Question

/**
 * 今日 Loop 计划：锁定一个知识板块里的一个锚点、一个最该补的认知层级、最多 3 道题。
 */
data class LoopPlan(
    val anchorCode: String,
    val anchorName: String,
    val topicName: String,
    val level: String,
    val levelLabel: String,
    val questions: List<Question>,
    val reason: String
)

/**
 * 生成器：决定「今天练哪个板块、哪个点、哪一层、给哪几道题」。
 *
 * 三步：
 *  1. 选板块：必修且还没稳固的板块优先，避免连续多天只在同一个点上打转
 *  2. 选锚点：前置达标 → 最近发展区（0.55–0.85）→ 概率最低的
 *  3. 选题：先排除冷却期内做过的题，再按「诊断 → 目标 → 迁移」三档难度组卷
 *
 * 判定「是否通过」不在这里，由 AnchorBktEngine 负责。
 */
object AnchorLoopEngine {

    private const val ZPD_LOW = 0.55f
    private const val ZPD_HIGH = 0.85f

    /** 冷却期：同一道题在这么多天内不重复出现 */
    private const val COOLDOWN_DAYS_KG = 3
    private const val COOLDOWN_DAYS_PRIMARY = 7

    suspend fun plan(
        db: AppDatabase,
        userId: Long,
        stage: String = Stage.PRIMARY_LOW,
        maxQuestions: Int = 3
    ): LoopPlan? {
        val states = db.anchorStateDao().listByUser(userId)
        val map = states.associateBy { it.anchorCode to it.cognitiveLevel }

        val cooldownDays = if (stage == Stage.KG) COOLDOWN_DAYS_KG else COOLDOWN_DAYS_PRIMARY
        val since = System.currentTimeMillis() - cooldownDays * 24L * 60 * 60 * 1000
        val recentIds = runCatching { db.learningRecordDao().questionIdsSince(userId, since) }
            .getOrDefault(emptyList())
            .toSet()

        val anchors = AnchorCatalog.forStage(stage)

        // 年级阶梯：一年级未达标只推一年级；达标后切到二年级，简单题靠 ZPD 与冷却自然跳过
        val ladder = GradeLadder.of(states, stage)
        val grade1Achieved = stage != Stage.KG &&
            ladder.grades.firstOrNull { it.grade == 1 }?.achieved == true
        val targetGrade = when {
            stage == Stage.KG -> 0
            grade1Achieved -> 2
            else -> 1
        }

        val topicHasSolid = anchors
            .filter { a -> AnchorCatalog.levels.all { l -> map[a.code to l]?.status == AnchorState.STATUS_SOLID } }
            .map { it.topic }
            .toSet()

        val scored = anchors
            .filter { it.grade <= targetGrade }
            .filter { prereqReady(it, map) }
            .mapNotNull { anchor ->
                val level = AnchorCatalog.levels.firstOrNull { lvl ->
                    map[anchor.code to lvl]?.status != AnchorState.STATUS_SOLID
                } ?: return@mapNotNull null
                val p = map[anchor.code to level]?.probability ?: anchor.bkt.init
                Candidate(anchor, level, p, anchor.topic in topicHasSolid, anchor.grade)
            }
            .sortedWith(
                compareBy(
                    { if (it.grade == targetGrade) 0 else 1 },
                    { it.topicSettled },
                    { it.probability }
                )
            )

        val chosen = scored.filter { it.probability in ZPD_LOW..ZPD_HIGH }.firstOrNull()
            ?: scored.firstOrNull()
            ?: return null

        val anchor = chosen.anchor
        val level = chosen.level
        val questions = pickQuestions(db, anchor.code, level, stage, userId, recentIds, maxQuestions)
        if (questions.isEmpty()) return null

        val state = map[anchor.code to level]
        val topicName = AnchorCatalog.topicOf(anchor.code)?.name ?: "综合"
        val reason = when {
            state == null || state.attempts == 0 -> "第一次接触「$topicName」，先看看你在哪一层"
            level == Question.LEVEL_UNDERSTANDING -> "记忆层已经过关，理解层还没通过"
            level == Question.LEVEL_APPLICATION -> "记忆和理解都过关了，试试在情境里用出来"
            else -> "这个点还没稳固，今天把它练熟"
        }

        return LoopPlan(
            anchorCode = anchor.code,
            anchorName = anchor.name,
            topicName = topicName,
            level = level,
            levelLabel = AnchorCatalog.levelLabel(level),
            questions = questions,
            reason = reason
        )
    }

    private data class Candidate(
        val anchor: AnchorDef,
        val level: String,
        val probability: Float,
        val topicSettled: Boolean,
        val grade: Int
    )

    /** 前置锚点达到 0.6 或已稳固才算可推进；前置未追踪时视为不阻塞 */
    private fun prereqReady(
        anchor: AnchorDef,
        map: Map<Pair<String, String>, AnchorState>
    ): Boolean = anchor.prerequisites.all { code ->
        val s = map[code to Question.LEVEL_MEMORY]
        s == null || s.status == AnchorState.STATUS_SOLID || s.probability >= 0.6f
    }

    /**
     * 组卷：诊断（易）→ 目标（中）→ 迁移（换表征）。
     * 冷却期内的题优先排除；若题量不足导致凑不满 3 题，再逐步放宽。
     */
    private suspend fun pickQuestions(
        db: AppDatabase,
        anchorCode: String,
        level: String,
        stage: String,
        userId: Long,
        recentIds: Set<Long>,
        maxQuestions: Int
    ): List<Question> {
        var pool = db.questionDao().listByAnchorLevelStage(anchorCode, level, stage)
        if (pool.isEmpty()) pool = db.questionDao().listByAnchorStage(anchorCode, stage)
        if (pool.isEmpty()) {
            for (a in AnchorCatalog.forStage(stage)) {
                pool = db.questionDao().listByAnchorStage(a.code, stage)
                if (pool.isNotEmpty()) break
            }
        }
        if (pool.isEmpty()) return emptyList()

        val fresh = pool.filter { it.id !in recentIds }
        val source = if (fresh.size >= maxQuestions) fresh else pool

        val picked = mutableListOf<Question>()
        val usedVariants = mutableSetOf<String>()

        // 1) 诊断题：最容易的一档
        source.minByOrNull { it.difficulty }?.let {
            picked += it
            if (it.variantType.isNotEmpty()) usedVariants += it.variantType
        }

        // 2) 目标题：难度居中的
        source.filter { q -> picked.none { it.id == q.id } }
            .minByOrNull { kotlin.math.abs(it.difficulty - 3) }
            ?.let {
                picked += it
                if (it.variantType.isNotEmpty()) usedVariants += it.variantType
            }

        // 3) 迁移题：换一种表征，验证是不是真的懂
        source.filter { q -> picked.none { it.id == q.id } }
            .sortedWith(
                compareByDescending<Question> { q ->
                    q.variantType.isNotEmpty() && q.variantType !in usedVariants
                }.thenByDescending { it.difficulty }
            )
            .firstOrNull()
            ?.let { picked += it }

        // 兜底补足
        for (q in source) {
            if (picked.size >= maxQuestions) break
            if (picked.none { it.id == q.id }) picked += q
        }
        return picked.take(maxQuestions)
    }
}
