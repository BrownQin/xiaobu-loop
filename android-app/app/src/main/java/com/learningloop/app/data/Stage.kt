package com.learningloop.app.data

/**
 * 学段：决定题库范围、锚点集合与首页入口文案。
 *
 * - KG：幼儿园中班 / 大班（4-6 岁），题目以看图、数数、听说为主；
 * - PRIMARY_LOW：小学一二年级，对应统编教材。
 */
object Stage {

    const val KG = "KG"
    const val PRIMARY_LOW = "PRIMARY_LOW"

    val all: List<String> = listOf(KG, PRIMARY_LOW)

    fun label(stage: String): String = if (stage == KG) "幼儿园" else "小学低年级"

    /** 家长填写「幼儿园中班」「大班」等年级描述时，自动落到幼儿园学段 */
    fun fromGrade(grade: String): String =
        if (listOf("幼儿", "学前", "小班", "中班", "大班").any { grade.contains(it) }) KG
        else PRIMARY_LOW
}
