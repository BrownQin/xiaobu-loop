package com.learningloop.app.engine

import com.learningloop.app.data.AnchorCatalog
import com.learningloop.app.data.AppDatabase
import com.learningloop.app.data.entity.Question

/**
 * 摸底测评：12 道题摸清孩子当前在哪个年级水平。
 *
 * 设计取舍：
 *  - 不按年级拆题库，只按年级给锚点排优先级，所以一次测评可以跨年级；
 *  - 尽量覆盖每个知识板块，避免只测到加减法；
 *  - 每个板块优先取「最低年级的必修锚点」，记忆层 → 理解层 → 应用层；
 *  - 忽略冷却期：新孩子本来就没有历史，老孩子重测也应看到真实水平。
 */
object DiagnosticEngine {

    const val DEFAULT_COUNT = 12

    suspend fun generate(
        db: AppDatabase,
        stage: String,
        count: Int = DEFAULT_COUNT
    ): List<Question> {
        val picked = mutableListOf<Question>()
        val used = HashSet<Long>()

        suspend fun pickOfAnchor(anchorCode: String): Question? {
            val levels = listOf(
                Question.LEVEL_MEMORY,
                Question.LEVEL_UNDERSTANDING,
                Question.LEVEL_APPLICATION
            )
            for (level in levels) {
                db.questionDao().listByAnchorLevelStage(anchorCode, level, stage)
                    .firstOrNull { it.id !in used }
                    ?.let { return it }
            }
            return db.questionDao().listByAnchorStage(anchorCode, stage)
                .firstOrNull { it.id !in used }
        }

        // 第一轮：每个板块 1 题
        val topics = AnchorCatalog.topicsOf(stage)
        for (topic in topics) {
            val anchor = AnchorCatalog.anchorsOfTopic(topic.code)
                .filter { it.stageCode == stage && it.required }
                .minWithOrNull(compareBy({ it.grade }, { it.stage }))
                ?: continue
            val q = pickOfAnchor(anchor.code) ?: continue
            picked += q
            used += q.id
            if (picked.size >= count) return picked
        }

        // 第二轮：按板块轮转补足，换锚点、换题
        while (picked.size < count) {
            var added = false
            for (topic in topics) {
                if (picked.size >= count) break
                val anchors = AnchorCatalog.anchorsOfTopic(topic.code)
                    .filter { it.stageCode == stage && it.required }
                    .sortedWith(compareBy({ it.grade }, { it.stage }))
                for (anchor in anchors) {
                    val q = pickOfAnchor(anchor.code) ?: continue
                    picked += q
                    used += q.id
                    added = true
                    break
                }
            }
            if (!added) break
        }

        // 兜底：从本学段题库补齐，优先中等难度
        if (picked.size < count) {
            val pool = db.questionDao().listByStage(stage)
                .filter { it.enabled && it.type != Question.TYPE_FILL && it.id !in used }
            pool.filter { it.difficulty in 2..4 }.shuffled()
                .take(count - picked.size)
                .forEach { picked += it; used += it.id }
            if (picked.size < count) {
                pool.filter { it.id !in used }.shuffled()
                    .take(count - picked.size)
                    .forEach { picked += it }
            }
        }

        return picked.distinctBy { it.id }.take(count)
    }
}
