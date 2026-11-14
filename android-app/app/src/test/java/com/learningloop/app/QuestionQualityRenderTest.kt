package com.learningloop.app

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.learningloop.app.data.AppDatabase
import com.learningloop.app.data.Stage
import com.learningloop.app.data.entity.LearningRecord
import com.learningloop.app.data.entity.Question
import com.learningloop.app.ui.parent.QuestionQualityScreen
import com.learningloop.app.ui.theme.LearningLoopTheme
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * 复现「点击题目质量异常退出」：用内存数据库把证据链跑通，
 * 覆盖 Room 聚合查询 + 页面组合渲染。
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class QuestionQualityRenderTest {

    @get:Rule
    val compose = createComposeRule()

    private var db: AppDatabase? = null

    @After
    fun tearDown() {
        db?.close()
    }

    @Test
    fun rendersWithEvidence() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val database = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        db = database

        runBlocking {
            val id = database.questionDao().insert(
                Question(
                    subject = Question.SUBJECT_MATH,
                    knowledgePoint = "数学-10以内加减",
                    difficulty = 2,
                    type = Question.TYPE_CHOICE,
                    content = "1 加 1 等于几？",
                    options = "{\"A\":\"1\",\"B\":\"2\",\"C\":\"3\"}",
                    answer = "B",
                    explanation = "1 加 1 等于 2。",
                    anchorCode = "kg-add-10",
                    cognitiveLevel = Question.LEVEL_MEMORY,
                    variantType = Question.VARIANT_TEXT,
                    stage = Stage.KG
                )
            )
            repeat(3) {
                database.learningRecordDao().insert(
                    LearningRecord(
                        userId = 1L,
                        questionId = id,
                        isCorrect = true,
                        durationMs = 2000
                    )
                )
            }
        }

        compose.setContent {
            LearningLoopTheme {
                QuestionQualityScreen(userId = 1L, db = database, onBack = {})
            }
        }

        compose.waitUntil(timeoutMillis = 5000) {
            compose.onAllNodesWithText("偏简单").fetchSemanticsNodes().isNotEmpty()
        }
    }
}
