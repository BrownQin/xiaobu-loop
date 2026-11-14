package com.learningloop.app.ui.child

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learningloop.app.ScreenHeader
import com.learningloop.app.data.Stage
import com.learningloop.app.data.entity.Question
import com.learningloop.app.ui.theme.LearningColors
import com.learningloop.app.ui.tts.rememberTtsController
import kotlinx.coroutines.delay

/**
 * 做题屏：
 *  - 顶部进度条 + 计时器；
 *  - 中间题目区，按题型渲染（选择题 / 填空题 / 应用题）；
 *  - 底部朗读 + 提交按钮。
 *
 * 回调 onAnswer(question, userAnswer, durationMs, answerChanges, optionSwitches) 会在提交时触发，
 * 由外层引擎判分、更新掌握度、计算积分。
 */
@Composable
fun QuizScreen(
    questions: List<Question>,
    startIndex: Int = 0,
    total: Int = questions.size,
    stage: String = Stage.PRIMARY_LOW,
    onAnswer: (Question, String, Long, Int, Int) -> Unit = { _, _, _, _, _ -> },
    onExit: () -> Unit = {}
) {
    var userAnswer by remember(startIndex) { mutableStateOf("") }
    var elapsedMs by remember(startIndex) { mutableStateOf(0L) }
    var submitted by remember(startIndex) { mutableStateOf(false) }

    // 过程证据：改答次数 / 选项切换次数。切换多说明不确定，是「猜对」的重要信号
    var answerChanges by remember(startIndex) { mutableStateOf(0) }
    var optionSwitches by remember(startIndex) { mutableStateOf(0) }

    val current = questions.getOrNull(startIndex)

    // 读题：低龄孩子不识字，用系统 TTS 把题干和选项念出来
    val tts = rememberTtsController()

    fun speakQuestion(q: Question) {
        val options = if (q.type == Question.TYPE_CHOICE) {
            parseOptions(q.options).joinToString("，") { "${it.first}，${it.second}" }
        } else ""
        val text = q.content + if (options.isNotBlank()) "。选项：$options" else ""
        tts.speak(text)
    }

    // 幼儿园学段进入新题自动朗读一遍
    LaunchedEffect(current?.id, stage) {
        if (stage == Stage.KG && current != null) {
            delay(500)
            speakQuestion(current)
        }
    }

    // 计时器：每题上限 60s，超时按空答案提交
    LaunchedEffect(startIndex) {
        elapsedMs = 0L
        submitted = false
        while (elapsedMs < 60_000) {
            delay(1000)
            elapsedMs += 1000
        }
        if (!submitted) {
            submitted = true
            current?.let { onAnswer(it, "", elapsedMs, answerChanges, optionSwitches) }
        }
    }

    if (current == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("题目已失效，请返回重试")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScreenHeader(
            title = "第 ${startIndex + 1} 题",
            subtitle = "共 $total 题 · 专注答题",
            onBack = onExit
        )

        // 进度条
        LinearProgressIndicator(
            progress = { (startIndex + 1) / total.toFloat() },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "进度 ${startIndex + 1} / $total",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Text(
                text = "⏱ ${elapsedMs / 1000}s",
                fontWeight = FontWeight.Bold,
                color = if (elapsedMs > 45_000) LearningColors.WrongRed.copy(alpha = 0.9f)
                        else MaterialTheme.colorScheme.onBackground
            )
        }

        // 题目卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row {
                    BadgeText(subjectLabel(current.subject, stage), LearningColors.PrimaryLight)
                    Spacer(Modifier.width(8.dp))
                    BadgeText(typeLabel(current.type), LearningColors.SecondaryLight)
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = current.content,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 答题区
        AnswerArea(
            question = current,
            value = userAnswer,
            onValueChange = { next ->
                if (next != userAnswer) {
                    if (userAnswer.isNotBlank()) {
                        if (current.type == Question.TYPE_CHOICE) optionSwitches++ else answerChanges++
                    }
                    userAnswer = next
                }
            }
        )

        Spacer(Modifier.height(4.dp))

        // 底部：朗读 + 提交
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { speakQuestion(current) },
                modifier = Modifier.height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LearningColors.SecondaryLight,
                    contentColor = LearningColors.OnSecondary
                )
            ) { Text("🔊 读题", fontSize = 16.sp) }

            Button(
                onClick = {
                    if (!submitted) {
                        submitted = true
                        onAnswer(current, userAnswer.trim(), elapsedMs, answerChanges, optionSwitches)
                    }
                },
                modifier = Modifier.weight(1f).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LearningColors.PrimaryLight),
                shape = RoundedCornerShape(16.dp)
            ) { Text("提交答案", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun AnswerArea(question: Question, value: String, onValueChange: (String) -> Unit) {
    when (question.type) {
        Question.TYPE_CHOICE -> {
            val options = parseOptions(question.options)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { (key, text) ->
                    val selected = value == key
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (selected) LearningColors.PrimaryLight.copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) LearningColors.PrimaryLight
                                       else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onValueChange(key) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (selected) LearningColors.PrimaryLight
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                key, fontWeight = FontWeight.Bold,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(text, fontSize = 16.sp)
                    }
                }
            }
        }
        else -> {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(if (question.type == Question.TYPE_FILL) "请填写答案" else "请写出你的解答") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LearningColors.PrimaryLight,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
private fun BadgeText(text: String, bg: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg.copy(alpha = 0.25f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) { Text(text, fontSize = 13.sp, fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface) }
}

private fun subjectLabel(subject: String, stage: String): String = when (subject) {
    Question.SUBJECT_CHINESE -> if (stage == Stage.KG) "语言" else "语文"
    Question.SUBJECT_MATH -> if (stage == Stage.KG) "思维" else "数学"
    else -> subject
}

private fun typeLabel(type: String): String = when (type) {
    Question.TYPE_CHOICE -> "选择题"
    Question.TYPE_FILL -> "填空题"
    Question.TYPE_WORD -> "应用题"
    else -> type
}

/** 解析简单 JSON 选项，格式: {"A":"...", "B":"..."} */
private fun parseOptions(json: String): List<Pair<String, String>> {
    if (json.isBlank()) return emptyList()
    val body = json.trim().removeSurrounding("{", "}")
    val result = mutableListOf<Pair<String, String>>()
    body.split(",").forEach { chunk ->
        val parts = chunk.split(":", limit = 2)
        if (parts.size == 2) {
            val key = parts[0].trim().removeSurrounding("\"")
            val value = parts[1].trim().removeSurrounding("\"")
            result += key to value
        }
    }
    return result
}


