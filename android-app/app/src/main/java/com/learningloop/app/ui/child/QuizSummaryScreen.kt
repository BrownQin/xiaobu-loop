package com.learningloop.app.ui.child

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learningloop.app.data.GradeLadder
import com.learningloop.app.ui.theme.LearningColors

/**
 * 一次测评结束后的总结页。
 *
 * 设计原则：
 *  - 先肯定、再建议，永远不以分数评判孩子；
 *  - 无论对错都明确告知“下一步做什么”，让孩子有掌控感；
 *  - 不使用“失败 / 不及格”等伤害自尊的措辞。
 */
@Composable
fun QuizSummaryScreen(
    total: Int,
    correct: Int,
    earnedPoints: Long,
    /** 摸底测评结束时展示年级阶梯结论；普通练习为 null */
    ladder: GradeLadder.Status? = null,
    onRetry: () -> Unit = {},
    onHome: () -> Unit = {}
) {
    val rate = if (total > 0) correct * 100 / total else 0
    val emoji = when {
        rate >= 80 -> "🏆"
        rate >= 60 -> "🌟"
        else -> "💪"
    }
    val title = when {
        rate >= 80 -> "今天的表现很棒！"
        rate >= 60 -> "稳步前进中"
        else -> "别灰心，进步就在眼前"
    }
    val tip = when {
        rate >= 80 -> "你已经掌握了大部分知识点，保持这个节奏就好。"
        rate >= 60 -> "大部分题都做对了，去错题本看看剩下的几道题吧。"
        else -> "错题已经帮你整理好了，下一轮我们从最薄弱的地方开始。"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 64.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (rate >= 60) LearningColors.CorrectGreen else LearningColors.WrongRed
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "本次测评完成",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatBlock(label = "答对", value = "$correct / $total", unit = "题")
                Column(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {}
                StatBlock(label = "获得", value = "+$earnedPoints", unit = "颗星")
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            tip,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
        )

        ladder?.let { status ->
            Spacer(Modifier.height(18.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LearningColors.CorrectGreen.copy(alpha = 0.12f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "🗺 本次测评结论",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        status.headline,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (status.grades.isNotEmpty()) {
                        Text(
                            status.grades.joinToString(" · ") {
                                "${it.label} ${it.solid}/${it.requiredTotal}"
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        status.suggestion,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LearningColors.PrimaryLight),
            shape = RoundedCornerShape(14.dp)
        ) { Text("再练一组", fontSize = 17.sp, fontWeight = FontWeight.Bold) }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = onHome,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(14.dp)
        ) { Text("回到首页", fontSize = 15.sp) }
    }
}

@Composable
private fun StatBlock(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(3.dp))
            Text(unit, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
    }
}
