package com.learningloop.app.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.learningloop.app.data.AnchorCatalog
import com.learningloop.app.data.GradeLadder
import com.learningloop.app.data.Stage
import com.learningloop.app.data.entity.AnchorState
import com.learningloop.app.data.entity.Question
import com.learningloop.app.engine.GrowthCurveAnalyzer
import com.learningloop.app.ScreenHeader
import com.learningloop.app.engine.GrowthCurveAnalyzer.DailyPoint
import com.learningloop.app.engine.GrowthCurveAnalyzer.WeakKpItem
import com.learningloop.app.engine.GrowthCurveAnalyzer.WeeklySummary
import com.learningloop.app.ui.theme.LearningColors

/**
 * 家长仪表盘 —— 核心可视化面板
 * 包含：
 *   1) 错题分布（按学科比例条）
 *   2) 薄弱点雷达（Top 知识点掌握度进度条）
 *   3) 成长曲线（最近 7 天正确率折线，MVP 用简化柱状条呈现）
 *   4) 干预建议（给家长的行动建议，MVP 本地规则生成）
 *
 * 所有数据由 GrowthCurveAnalyzer 提供，100% 本地计算。
 */
@Composable
fun ParentDashboardScreen(
    childName: String = "小朋友",
    overallMastery: Double = 62.3,
    wrongCount: Int = 5,
    weakKps: List<WeakKpItem> = emptyList(),
    dailyAccuracy: List<DailyPoint> = emptyList(),
    weeklySummary: List<WeeklySummary> = emptyList(),
    suggestion: String? = null,
    anchorStates: List<AnchorState> = emptyList(),
    stage: String = Stage.PRIMARY_LOW,
    ladder: GradeLadder.Status? = null,
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenHeader(
            title = "学习报告",
            subtitle = "$childName 的个性化学习情况",
            onBack = onBack
        )

        // 顶部概览
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = LearningColors.PrimaryLight.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatBlock("总体掌握度", "${overallMastery.toInt()}%")
                StatBlock("错题数量", "$wrongCount")
            }
        }

        // 当前水平：不分年级题库，用阶梯说明孩子到哪一步了
        ladder?.let { status ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LearningColors.CorrectGreen.copy(alpha = 0.12f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "当前水平",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                    )
                    Text(status.headline, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    status.grades.forEach { g ->
                        Text(
                            "${g.label}必修锚点：${g.solid}/${g.requiredTotal}" +
                                if (g.achieved) " · 已达标" else " · 进行中",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        status.suggestion,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                    )
                }
            }
        }

        // 能力地图：主题概览 → 点开看锚点 × 三层
        if (anchorStates.isNotEmpty()) {
            SectionHeader("🧭 能力地图")
            TopicMap(anchorStates, stage)
        }

        // 薄弱点雷达（MVP 简化：Top 6 知识点掌握度条形列表）
        SectionHeader("🎯 薄弱点雷达")
        if (weakKps.isEmpty()) {
            Text("太棒了！所有知识点都掌握良好 💪", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        } else {
            weakKps.take(6).forEach { item ->
                WeakKpBar(item)
            }
        }

        // 成长曲线（最近 7 天正确率）
        SectionHeader("📈 近7天正确率")
        WeeklyBars(dailyAccuracy)

        // 周度总结
        if (weeklySummary.isNotEmpty()) {
            SectionHeader("🗓 近4周汇总")
            weeklySummary.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(it.weekStart, fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    Text("答题 ${it.questionCount} 次 · 平均掌握 ${it.avgMastery.toInt()}%")
                }
            }
        }

        // 干预建议
        SectionHeader("💡 干预建议")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = LearningColors.SecondaryLight.copy(alpha = 0.18f))
        ) {
            Text(
                text = suggestion ?: defaultSuggestion(weakKps, dailyAccuracy),
                modifier = Modifier.padding(14.dp),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun StatBlock(label: String, value: String) {
    Column {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(text, fontSize = 17.sp, fontWeight = FontWeight.Bold)
}

/** 锚点 × 认知层级：只展示三档状态，不暴露小数概率 */
/**
 * 能力地图：先给主题级结论（这是家长最需要的），点开再看锚点 × 三层。
 * 不暴露小数概率，只给「已稳固 / 攻坚中 / 未触发」三档。
 */
@Composable
private fun TopicMap(states: List<AnchorState>, stage: String) {
    var expandedTopic by remember { mutableStateOf<String?>(null) }
    val byAnchor = states.groupBy { it.anchorCode }

    val visibleTopics = AnchorCatalog.topics.filter { topic ->
        AnchorCatalog.anchorsOfTopic(topic.code).any { byAnchor.containsKey(it.code) }
    }

    visibleTopics.forEach { topic ->
        val anchors = AnchorCatalog.anchorsOfTopic(topic.code)
            .filter { byAnchor.containsKey(it.code) }
        if (anchors.isEmpty()) return@forEach

        val solid = anchors.count { a -> isSolid(byAnchor, a.code) }
        val required = anchors.filter { it.required }
        val requiredSolid = required.count { a -> isSolid(byAnchor, a.code) }
        val started = anchors.count { a ->
            AnchorCatalog.levels.any { l ->
                statusOf(byAnchor, a.code, l) != AnchorState.STATUS_UNTOUCHED
            }
        }
        val expanded = expandedTopic == topic.code

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable {
                        expandedTopic = if (expanded) null else topic.code
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(topic.name, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text(
                            "必修 $requiredSolid/${required.size} · 已稳固 $solid/${anchors.size} · " +
                                "${topic.domain} · ${gradeLabel(stage, topic.grade)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        if (expanded) "收起 ▲" else "看锚点 ▼",
                        fontSize = 12.sp,
                        color = LearningColors.PrimaryLight
                    )
                }
                LinearProgressIndicator(
                    progress = { if (anchors.isEmpty()) 0f else solid.toFloat() / anchors.size },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = if (solid == anchors.size) LearningColors.CorrectGreen else LearningColors.PrimaryLight
                )
                if (started == 0) {
                    Text(
                        "还没开始，先从最简单的锚点入手",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
                if (expanded) {
                    anchors.forEach { anchor ->
                        AnchorRow(anchor.name, byAnchor[anchor.code] ?: emptyList())
                    }
                }
            }
        }
    }
}

@Composable
private fun AnchorRow(anchorName: String, list: List<AnchorState>) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(anchorName, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AnchorCatalog.levels.forEach { level ->
                val status = list.firstOrNull { it.cognitiveLevel == level }?.status
                    ?: AnchorState.STATUS_UNTOUCHED
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor(status))
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(AnchorCatalog.levelLabel(level), fontSize = 10.sp, color = Color.White)
                    Text(
                        AnchorCatalog.statusLabel(status),
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
        val gap = AnchorCatalog.levels.firstOrNull { level ->
            (list.firstOrNull { it.cognitiveLevel == level }?.status
                ?: AnchorState.STATUS_UNTOUCHED) != AnchorState.STATUS_SOLID
        }
        if (gap != null) {
            Text(
                gapHint(anchorName, gap),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}

private fun isSolid(byAnchor: Map<String, List<AnchorState>>, code: String): Boolean {
    val list = byAnchor[code] ?: return false
    return AnchorCatalog.levels.all { l ->
        list.firstOrNull { it.cognitiveLevel == l }?.status == AnchorState.STATUS_SOLID
    }
}

private fun statusOf(
    byAnchor: Map<String, List<AnchorState>>,
    code: String,
    level: String
): String = byAnchor[code]?.firstOrNull { it.cognitiveLevel == level }?.status
    ?: AnchorState.STATUS_UNTOUCHED

private fun statusColor(status: String): Color = when (status) {
    AnchorState.STATUS_SOLID -> LearningColors.CorrectGreen
    AnchorState.STATUS_IN_PROGRESS -> LearningColors.AccentStar
    else -> Color(0xFFBDBDBD)
}

private fun gradeLabel(stage: String, grade: Int): String =
    if (stage == Stage.KG) "幼儿园" else if (grade >= 2) "二年级" else "一年级"

private fun gapHint(anchorName: String, level: String): String = when (level) {
    Question.LEVEL_MEMORY -> "$anchorName 的记忆层还没打通，先从认读和基础计算起步"
    Question.LEVEL_UNDERSTANDING -> "$anchorName 的记忆层已过关，理解层还没通过——会做但说不清"
    else -> "$anchorName 的记忆和理解都过关了，应用层还需要在情境中练"
}

@Composable
private fun WeakKpBar(item: WeakKpItem) {
    val progress = (item.mastery / 100f).coerceIn(0f, 1f)
    val barColor = when {
        item.mastery < 40 -> LearningColors.WrongRed
        item.mastery < 60 -> LearningColors.AccentStar
        else -> LearningColors.CorrectGreen
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.kp.substringAfter("-").ifBlank { item.kp },
            modifier = Modifier.weight(1f),
            fontSize = 13.sp
        )
        Text(item.subject, fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
        Spacer(Modifier.width(6.dp))
        Box(modifier = Modifier.width(100.dp).height(8.dp).clip(RoundedCornerShape(4.dp))) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = barColor,
                trackColor = barColor.copy(alpha = 0.2f)
            )
        }
        Spacer(Modifier.width(6.dp))
        Text("${item.mastery}%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun WeeklyBars(points: List<DailyPoint>) {
    if (points.isEmpty()) {
        Text("暂无数据，让孩子先做几道题吧~",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        return
    }
    Row(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        points.forEach { p ->
            val h = (p.accuracy * 80 + 6).toInt().coerceAtLeast(6)
            val label = p.day.takeLast(2) // 日 "11" / "12"
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${(p.accuracy * 100).toInt()}%", fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .height(h.dp)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(LearningColors.PrimaryLight.copy(alpha = 0.7f))
                )
                Text(label, fontSize = 11.sp)
            }
        }
    }
}

/**
 * MVP 阶段的规则式建议生成。
 * 后续会由 LLMClient + PromptTemplates 接管，输出更个性化的方案。
 */
private fun defaultSuggestion(weak: List<WeakKpItem>, daily: List<DailyPoint>): String {
    val low = weak.firstOrNull { it.mastery < 30 }
    val recent = daily.takeLast(3)
    val anyCorrect = recent.any { it.accuracy > 0.7 }

    return when {
        low != null -> {
            val kp = low.kp.substringAfter("-")
            // 直接引用 WeakKpItem 上的字段
            "「$kp」掌握度较低（${low.mastery}%），建议今天花 10 分钟针对性练 3-5 道同知识点题目。"
        }
        !anyCorrect -> {
            "最近 3 天正确率有所下滑，建议先从错题本开始重做，让孩子重新找回自信。"
        }
        weak.isEmpty() -> {
            "所有知识点掌握良好！可以开始做一些有挑战性的延伸题，帮助孩子保持兴趣。"
        }
        else -> {
            "保持当前节奏，关注错题闭环，建议每周安排 1-2 次 20 分钟的短测评巩固。"
        }
    }
}

// 让 GrowthCurveAnalyzer 引用不告警（在 parent Dashboard 中）
@Suppress("UNUSED_PARAMETER")
private fun _ignoreAnalyzerRef(analyzer: GrowthCurveAnalyzer) = Unit
