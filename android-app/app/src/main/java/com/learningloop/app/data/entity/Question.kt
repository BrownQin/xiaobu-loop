package com.learningloop.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.learningloop.app.data.Stage

/**
 * 题库主表：按学段（幼儿园 / 小学低年级）区分。
 *
 * subject："chinese" 语文 / "math" 数学
 * difficulty：1 (易) ~ 5 (难)
 * type：choice 选择题 / fill 填空题（已停用）/ word 应用题（已停用）
 * options：JSON 字符串，存储选择题的选项；非选择题可为空
 */
@Entity(
    tableName = "question",
    indices = [
        Index(value = ["subject"], name = "idx_question_subject"),
        Index(value = ["knowledge_point"], name = "idx_question_kp"),
        Index(value = ["subject", "knowledge_point"], name = "idx_question_sub_kp"),
        Index(value = ["stage"], name = "idx_question_stage")
    ]
)
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 学科：chinese / math */
    val subject: String,

    /** 知识点：如 "拼音-声母韵母", "数学-表内乘法-2的乘法口诀" */
    @ColumnInfo(name = "knowledge_point")
    val knowledgePoint: String,

    /** 难度 1-5 */
    val difficulty: Int,

    /** 题型：choice / fill / word */
    val type: String,

    /** 题目内容 */
    val content: String,

    /** 选项（JSON），选择题必填，其它可为空字符串 */
    val options: String = "",

    /** 标准答案。选择题存选项索引 (如 "A"、"B")，填空/应用题存文本 */
    val answer: String,

    /** 解析 / 讲解文本 */
    val explanation: String,

    /** 标签，用于后期扩展，如 "易错" "高频" */
    val tags: String = "",

    /** 是否启用：停用的题目不会进入测评，但保留历史数据 */
    val enabled: Boolean = true,

    /** 来源：builtin 内置 / imported 导入 / custom 手工新增 */
    val source: String = SOURCE_BUILTIN,

    /** 最后一次修改时间，0 表示未修改过 */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = 0L,

    /** 所属锚点 code，空串表示未标注（未标注题不进入锚点 Loop） */
    @ColumnInfo(name = "anchor_code")
    val anchorCode: String = "",

    /** 认知层级：MEMORY 记忆 / UNDERSTANDING 理解 / APPLICATION 应用 */
    @ColumnInfo(name = "cognitive_level")
    val cognitiveLevel: String = LEVEL_MEMORY,

    /** 表征方式：EXPRESSION 算式 / DIAGRAM 图示 / CONTEXT 情境 / TEXT 文字 */
    @ColumnInfo(name = "variant_type")
    val variantType: String = "",

    /** 学段：KG 幼儿园 / PRIMARY_LOW 小学低年级，组卷时只取当前孩子学段的题 */
    val stage: String = Stage.PRIMARY_LOW,

    /**
     * 自动质量结论：空串正常 / TOO_EASY / TOO_HARD / FAST_GUESS。
     * 由作答证据定期刷新，TOO_HARD 与 FAST_GUESS 不进入组卷。
     */
    @ColumnInfo(name = "quality_state")
    val qualityState: String = ""
) {
    companion object {
        // 题型常量
        const val TYPE_CHOICE = "choice"
        const val TYPE_FILL = "fill"
        const val TYPE_WORD = "word"

        // 学科常量
        const val SUBJECT_CHINESE = "chinese"
        const val SUBJECT_MATH = "math"

        // 来源常量
        const val SOURCE_BUILTIN = "builtin"
        const val SOURCE_IMPORTED = "imported"
        const val SOURCE_CUSTOM = "custom"

        // 认知层级
        const val LEVEL_MEMORY = "MEMORY"
        const val LEVEL_UNDERSTANDING = "UNDERSTANDING"
        const val LEVEL_APPLICATION = "APPLICATION"

        // 表征方式
        const val VARIANT_EXPRESSION = "EXPRESSION"
        const val VARIANT_DIAGRAM = "DIAGRAM"
        const val VARIANT_CONTEXT = "CONTEXT"
        const val VARIANT_TEXT = "TEXT"
    }
}
