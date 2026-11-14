package com.learningloop.app.ui.child

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.learningloop.app.data.entity.Question
import com.learningloop.app.engine.Verdict
import com.learningloop.app.ui.theme.LearningColors
import kotlinx.coroutines.delay

/**
 * 答题结果页，支持判分三态：
 *  - CORRECT   完全正确 → 庆祝 + 积分
 *  - PARTIAL   思路接近 → 明确肯定“方向对”，但不发积分，提示补全
 *  - INCORRECT 未掌握   → 温和提示 + 参考思路 + 进入错题闭环
 *
 * 只有 CORRECT 才会显示积分，避免“答错也发积分”的误导。
 */
@Composable
fun ResultScreen(
    question: Question,
    userAnswer: String,
    verdict: Verdict,
    earnedPoints: Long = 0L,
    explanation: String? = null,
    message: String = "",
    index: Int = 0,
    total: Int = 1,
    onNext: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    val isCorrect = verdict == Verdict.CORRECT
    val isOpen = question.type == Question.TYPE_WORD

    val bgColor = when (verdict) {
        Verdict.CORRECT -> LearningColors.CorrectGreen.copy(alpha = 0.18f)
        Verdict.PARTIAL -> LearningColors.AccentStar.copy(alpha = 0.20f)
        Verdict.INCORRECT -> LearningColors.WrongRed.copy(alpha = 0.14f)
    }
    val titleColor = when (verdict) {
        Verdict.CORRECT -> LearningColors.CorrectGreen
        Verdict.PARTIAL -> LearningColors.AccentStar
        Verdict.INCORRECT -> LearningColors.WrongRed
    }
    val (emoji, title) = when (verdict) {
        Verdict.CORRECT -> "✨" to "太棒了，完全正确！"
        Verdict.PARTIAL -> "💡" to "思路很接近了"
        Verdict.INCORRECT -> "🤔" to "这一步还没掌握"
    }
    val answerLabel = if (isOpen) "参考思路" else "正确答案"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        ScreenHeader(
            title = "本题结果",
            subtitle = "第 ${index + 1} / $total 题",
            onBack = onBack
        )
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.weight(1f),
            enter = fadeIn(tween(400)) + scaleIn(initialScale = 0.8f),
            exit = fadeOut() + scaleOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(emoji, fontSize = 64.sp)
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                    modifier = Modifier.padding(top = 8.dp)
                )

                if (message.isNotBlank()) {
                    Text(
                        text = message,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                // 积分仅在完全正确时展示
                if (isCorrect && earnedPoints > 0) {
                    Text(
                        text = "+${earnedPoints} 颗星 🌟",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = LearningColors.AccentStar,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = question.content,
                            fontSize = 17.sp, fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "你的答案：$userAnswer",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Text(
                            "$answerLabel：${question.answer}",
                            fontWeight = FontWeight.Bold
                        )

                        val exp = explanation?.takeIf { it.isNotBlank() } ?: question.explanation
                        if (!exp.isNullOrBlank()) {
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "思路：$exp",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                if (isCorrect) {
                    PrimaryButton("继续学习 👉", LearningColors.PrimaryLight, onNext)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PrimaryButton("看懂了，下一题", LearningColors.PrimaryLight, onNext)
                        PrimaryButton("先休息一下（回首页）", LearningColors.SecondaryLight, onBack)
                    }
                }
            }
        }
    }
}

@Composable
private fun PrimaryButton(label: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(label, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}
