package com.learningloop.app.data

import android.content.Context
import com.learningloop.app.data.entity.Question

/**
 * assets 题库包加载器。
 *
 * 内容包随 APK 发版（app/src/main/assets/questions_v2.csv），
 * 启动时由 AnchorSeeder 按题干去重增量导入，老用户升级也能拿到新题。
 * 内容包由 /workspace/content-packs 下的分包校验合并生成。
 */
object AssetQuestions {

    const val ASSET_NAME = "questions_v2.csv"

    fun load(context: Context): List<Question> = try {
        val text = context.assets.open(ASSET_NAME).bufferedReader().use { it.readText() }
        QuestionCsv.parse(text).questions
            .filter { it.type != Question.TYPE_FILL }
            .map { it.copy(source = Question.SOURCE_BUILTIN) }
    } catch (_: Exception) {
        emptyList()
    }
}
