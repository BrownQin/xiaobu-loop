package com.learningloop.app.data

import android.content.Context
import com.learningloop.app.SeedData
import com.learningloop.app.data.entity.AnchorState
import com.learningloop.app.data.entity.Question

/**
 * 锚点数据初始化：
 *  1. 导入 assets 题库内容包（按题干去重增量，老用户升级也能拿到新题）
 *  2. 给老库里未标注的内置题回填锚点与认知层级（不丢题、不改题干）
 *  3. 为当前孩子补齐「锚点 × 层级」的空白状态格，保证家长矩阵完整
 */
object AnchorSeeder {

    /** 内容包每个进程只需尝试导入一次 */
    private var assetImported = false

    suspend fun run(
        db: AppDatabase,
        userId: Long,
        stage: String = Stage.PRIMARY_LOW,
        context: Context? = null
    ) {
        ensureAssetQuestions(context, db)
        ensureSeedQuestions(db)
        annotateBuiltin(db)
        seedStates(db, userId, stage)
    }

    /**
     * 导入 assets/questions_v2.csv。
     * 已存在的同题干老题：若还没标注锚点，则用内容包的标注就地升级（保留 id 与启停状态）。
     */
    private suspend fun ensureAssetQuestions(context: Context?, db: AppDatabase) {
        if (assetImported || context == null) return
        val fromAssets = AssetQuestions.load(context)
        if (fromAssets.isEmpty()) {
            assetImported = true
            return
        }

        val existing = db.questionDao().listAll().associateBy { it.content }
        val toWrite = mutableListOf<Question>()
        fromAssets.forEach { q ->
            val old = existing[q.content]
            when {
                old == null -> toWrite += q
                old.anchorCode.isBlank() ->
                    toWrite += q.copy(id = old.id, enabled = old.enabled)
            }
        }
        if (toWrite.isNotEmpty()) db.questionDao().insertAll(toWrite)
        assetImported = true
    }

    /** 老用户升级后也能拿到新版本新增的题：按题干去重增量补入 */
    private suspend fun ensureSeedQuestions(db: AppDatabase) {
        val existing = db.questionDao().listAll().map { it.content }.toSet()
        val missing = SeedData.all().filter { it.content !in existing }
        if (missing.isNotEmpty()) db.questionDao().insertAll(missing)
    }

    private suspend fun annotateBuiltin(db: AppDatabase) {
        val pending = db.questionDao().listUnannotatedBuiltin()
        if (pending.isEmpty()) return

        val changed = pending.mapNotNull { q ->
            val code = AnchorCatalog.legacyAnchorOf(q.knowledgePoint) ?: return@mapNotNull null
            val level = when {
                q.type == Question.TYPE_WORD -> Question.LEVEL_APPLICATION
                q.type == Question.TYPE_CHOICE && q.difficulty >= 3 -> Question.LEVEL_UNDERSTANDING
                else -> Question.LEVEL_MEMORY
            }
            val variant = when (q.type) {
                Question.TYPE_CHOICE -> Question.VARIANT_TEXT
                Question.TYPE_WORD -> Question.VARIANT_CONTEXT
                else -> Question.VARIANT_EXPRESSION
            }
            q.copy(anchorCode = code, cognitiveLevel = level, variantType = variant)
        }

        if (changed.isNotEmpty()) db.questionDao().insertAll(changed)
    }

    private suspend fun seedStates(db: AppDatabase, userId: Long, stage: String) {
        val existing = db.anchorStateDao().listByUser(userId)
            .map { it.anchorCode to it.cognitiveLevel }
            .toSet()

        val missing = mutableListOf<AnchorState>()
        AnchorCatalog.coreOf(stage).forEach { anchor ->
            AnchorCatalog.levels.forEach { level ->
                if ((anchor.code to level) !in existing) {
                    missing += AnchorState(
                        userId = userId,
                        anchorCode = anchor.code,
                        cognitiveLevel = level,
                        probability = anchor.bkt.init,
                        confidence = AnchorState.CONFIDENCE_LOW,
                        status = AnchorState.STATUS_UNTOUCHED
                    )
                }
            }
        }
        if (missing.isNotEmpty()) db.anchorStateDao().upsertAll(missing)
    }
}
