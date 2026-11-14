package com.learningloop.app.data

import com.learningloop.app.data.entity.AnchorState

/**
 * 年级阶梯：不拆题库，用一个连续的锚点梯度表示「孩子现在到哪一步了」。
 *
 * 判定规则（每级独立）：
 *  - 某年级的必修锚点里，记忆 / 理解 / 应用三层全部稳固的比例 ≥ 80%，即视为达到该年级；
 *  - 已达到的年级不重复测评：生成器会优先推下一个年级的锚点；
 *  - 已稳固的简单锚点由 ZPD 与冷却自然跳过，不额外浪费时间。
 */
object GradeLadder {

    /** 一个年级的必修锚点稳固率达到该阈值即判定「已达到」 */
    const val ACHIEVE_RATIO = 0.8f

    data class GradeProgress(
        val grade: Int,
        val label: String,
        val requiredTotal: Int,
        val solid: Int,
        val achieved: Boolean
    ) {
        val ratio: Float = if (requiredTotal == 0) 1f else solid.toFloat() / requiredTotal
    }

    data class Status(
        val stage: String,
        /** 当前所在年级：幼儿园 0，一年级 1，二年级 2 */
        val currentGrade: Int,
        /** 「已达到一年级水平」这类结论 */
        val headline: String,
        /** 每个年级的明细，幼儿园为空 */
        val grades: List<GradeProgress>,
        /** 给家长的下一步建议 */
        val suggestion: String,
        /** 是否已通关当前学段最高年级 */
        val completed: Boolean
    )

    fun labelOf(stage: String, grade: Int): String = when {
        stage == Stage.KG -> "幼儿园"
        grade >= 2 -> "二年级"
        grade == 1 -> "一年级"
        else -> "一年级起步"
    }

    /**
     * @param states 当前孩子的锚点 × 层级状态
     */
    fun of(states: List<AnchorState>, stage: String): Status {
        if (stage == Stage.KG) {
            val anchors = AnchorCatalog.forStage(stage).filter { it.required }
            val solid = anchors.count { a -> isSolid(states, a.code) }
            return Status(
                stage = stage,
                currentGrade = 0,
                headline = "幼儿园阶段",
                grades = emptyList(),
                suggestion = if (anchors.isNotEmpty() && solid == anchors.size) {
                    "幼儿园必修能力已全部稳固，可以尝试进入小学一年级的内容。"
                } else {
                    "先把认读、数感和听说玩熟，再自然过渡到一年级。"
                },
                completed = anchors.isNotEmpty() && solid == anchors.size
            )
        }

        val byCode = states.groupBy { it.anchorCode }
        val grades = listOf(1, 2).map { grade ->
            val required = AnchorCatalog.requiredAnchorsOfGrade(stage, grade)
            val solid = required.count { a -> isSolid(byCode[a.code] ?: emptyList(), a.code) }
            GradeProgress(
                grade = grade,
                label = labelOf(stage, grade),
                requiredTotal = required.size,
                solid = solid,
                achieved = required.isNotEmpty() &&
                    solid.toFloat() / required.size >= ACHIEVE_RATIO
            )
        }

        val achieved1 = grades.first { it.grade == 1 }.achieved
        val achieved2 = grades.first { it.grade == 2 }.achieved
        val current = when {
            achieved2 -> 2
            achieved1 -> 2
            else -> 1
        }
        val headline = when {
            achieved2 -> "已达到二年级水平"
            achieved1 -> "已达到一年级水平"
            else -> "一年级起步"
        }
        val suggestion = when {
            achieved2 -> "二年级必修内容已基本稳固，可以继续上探更难的内容，或做综合迁移练习。"
            achieved1 -> "一年级内容已达标，接下来优先推二年级锚点；过于简单的一年级题会自动跳过。"
            else -> "先按一年级锚点打基础，稳固率达到 80% 后自动升到二年级内容。"
        }
        return Status(
            stage = stage,
            currentGrade = current,
            headline = headline,
            grades = grades,
            suggestion = suggestion,
            completed = achieved2
        )
    }

    /** 一个锚点的记忆 / 理解 / 应用三层都稳固，才算真正掌握 */
    fun isSolid(states: List<AnchorState>, anchorCode: String): Boolean {
        val list = states.filter { it.anchorCode == anchorCode }
        if (list.isEmpty()) return false
        return AnchorCatalog.levels.all { level ->
            list.firstOrNull { it.cognitiveLevel == level }?.status == AnchorState.STATUS_SOLID
        }
    }
}
