package com.learningloop.app.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learningloop.app.ScreenHeader
import com.learningloop.app.data.AnchorCatalog
import com.learningloop.app.data.AppDatabase
import com.learningloop.app.data.QualitySettings
import com.learningloop.app.engine.ItemQualityAnalyzer
import com.learningloop.app.engine.ItemQualityAnalyzer.Issue
import com.learningloop.app.ui.theme.LearningColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 题目质量看板：用孩子真实作答证据反过来校准题库。
 *
 * v0.14 起默认开启「自动排除」：偏难 / 疑似秒答的题不再进入组卷；
 * 家长可以随时关掉，也可以对单道题手动停用或恢复。
 */
@Composable
fun QuestionQualityScreen(
    userId: Long,
    db: AppDatabase,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var items by remember(userId) { mutableStateOf<List<ItemQualityAnalyzer.ItemQuality>>(emptyList()) }
    var loading by remember(userId) { mutableStateOf(true) }
    var autoExclude by remember { mutableStateOf(QualitySettings.autoExclude(context)) }
    var reloadKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(userId, autoExclude, reloadKey) {
        loading = true
        withContext(Dispatchers.IO) {
            ItemQualityAnalyzer.refreshStates(db.questionDao(), db.learningRecordDao(), userId, autoExclude)
            ItemQualityAnalyzer.analyze(db.questionDao(), db.learningRecordDao(), userId)
        }.let { items = it }
        loading = false
    }

    val flagged = items.filter { it.issue != Issue.NONE }
    val sampled = items.filter { it.attempts >= ItemQualityAnalyzer.MIN_ATTEMPTS }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScreenHeader(
            title = "题目质量",
            subtitle = "用真实作答数据检查题目是否合适",
            onBack = onBack
        )

        // 自动排除开关：默认开启
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = LearningColors.PrimaryLight.copy(alpha = 0.10f)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("自动排除不合适的题", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "偏难或疑似秒答的题不再进入组卷；样本不足时不动。",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
                Switch(
                    checked = autoExclude,
                    onCheckedChange = {
                        autoExclude = it
                        QualitySettings.setAutoExclude(context, it)
                    }
                )
            }
        }

        if (loading) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text("正在整理作答证据…", fontSize = 13.sp, modifier = Modifier.padding(top = 10.dp))
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LearningColors.PrimaryLight.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        QStat("有作答的题", "${items.size}")
                        QStat("样本足够", "${sampled.size}")
                        QStat("建议调整", "${flagged.size}")
                    }
                    Text(
                        "每道题至少作答 ${ItemQualityAnalyzer.MIN_ATTEMPTS} 次才下结论，样本不足时保持原样。",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            if (items.isEmpty()) {
                Text(
                    "还没有作答记录。让孩子做几轮题，这里就会长出证据。",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            } else {
                if (flagged.isEmpty()) {
                    Text(
                        "目前没有需要调整的题目。继续积累样本，结论会更稳。",
                        fontSize = 14.sp,
                        color = LearningColors.CorrectGreen
                    )
                } else {
                    Text("需要关注", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    flagged.forEach { item ->
                        QualityCard(
                            item = item,
                            autoExclude = autoExclude,
                            onToggleEnabled = {
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        db.questionDao().setEnabled(
                                            item.question.id,
                                            !item.question.enabled,
                                            System.currentTimeMillis()
                                        )
                                    }
                                    reloadKey++
                                }
                            }
                        )
                    }
                }

                Text(
                    "这类证据接下来会用于三件事：识别「猜对」、判断该升还是该降层级、给大模型提供归因依据。",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun QStat(label: String, value: String) {
    Column {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun QualityCard(
    item: ItemQualityAnalyzer.ItemQuality,
    autoExclude: Boolean,
    onToggleEnabled: () -> Unit
) {
    val color = when (item.issue) {
        Issue.TOO_EASY -> LearningColors.AccentStar
        Issue.TOO_HARD -> LearningColors.WrongRed
        else -> LearningColors.SecondaryLight
    }
    val excluded = autoExclude &&
        (item.issue == Issue.TOO_HARD || item.issue == Issue.FAST_GUESS)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(item.issueLabel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
                Text(
                    text = buildString {
                        AnchorCatalog.nameOf(item.question.anchorCode).let { append(it); append(" · ") }
                        append(AnchorCatalog.levelLabel(item.question.cognitiveLevel))
                    },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
            Text(item.question.content, fontSize = 15.sp, lineHeight = 22.sp)
            Text(
                text = "作答 ${item.attempts} 次 · 正确率 ${item.accuracy}% · " +
                    "平均 ${item.avgDurationMs / 1000} 秒 · 秒答 ${item.fastGuesses} 次",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Text(item.advice, fontSize = 12.sp, color = color)
            if (excluded) {
                Text(
                    "已自动排除，不进入组卷",
                    fontSize = 11.sp,
                    color = LearningColors.WrongRed
                )
            }
            TextButton(onClick = onToggleEnabled, modifier = Modifier.align(Alignment.End)) {
                Text(
                    if (item.question.enabled) "停用这道题" else "恢复这道题",
                    fontSize = 12.sp
                )
            }
        }
    }
}
