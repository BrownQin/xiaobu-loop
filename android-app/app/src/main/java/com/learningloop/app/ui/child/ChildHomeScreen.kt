package com.learningloop.app.ui.child

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learningloop.app.data.Stage
import com.learningloop.app.data.entity.ChildProfile
import com.learningloop.app.engine.LoopPlan
import com.learningloop.app.ui.theme.LearningColors

/**
 * 孩子端首页。
 *  - 顶部问候 + 孩子头像 + 当前积分；
 *  - 中部"今日任务卡片"（自适应测评入口 + 错题重做入口）；
 *  - 底部"奖励商城"入口。
 *
 * MVP 阶段点击导航用回调参数由外层 Activity 处理，不引入导航框架。
 */
@Composable
fun ChildHomeScreen(
    childName: String = "小朋友",
    avatar: String = "🧒",
    totalPoints: Long = 120L,
    level: Int = 3,
    wrongCount: Int = 2,
    stage: String = Stage.PRIMARY_LOW,
    todayPlan: LoopPlan? = null,
    /** 年级阶梯结论，如「已达到一年级水平」 */
    levelText: String? = null,
    /** 还没做过题时，首页先引导一次 12 题摸底测评 */
    showDiagnosticEntry: Boolean = false,
    children: List<ChildProfile> = emptyList(),
    activeId: Long = 0L,
    onSelectChild: (Long) -> Unit = {},
    onStartQuiz: (String?) -> Unit = {},
    onStartToday: () -> Unit = {},
    onStartDiagnostic: () -> Unit = {},
    onOpenWrongBook: () -> Unit = {},
    onOpenReward: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    onOpenParentDashboard: () -> Unit = {}
) {
    var showSwitcher by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 顶部问候
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(LearningColors.PrimaryLight.copy(alpha = 0.2f))
                        .clickable { showSwitcher = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = avatar, fontSize = 28.sp)
                    // 右下角「换人」角标，一眼看出头像可切换
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⇄", fontSize = 11.sp, color = LearningColors.PrimaryLight)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "你好, $childName! 👋",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = listOfNotNull(
                            "等级 $level",
                            levelText?.takeIf { it.isNotBlank() }
                        ).joinToString(" · ") + " · 今日也要加油哦",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = "⚙ 管理",
                    fontSize = 13.sp,
                    color = LearningColors.PrimaryLight,
                    modifier = Modifier.clickable { onOpenParentDashboard() }
                )
            }

            // 星星卡片：点进去就是兑换商城
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenReward() },
                colors = CardDefaults.cardColors(containerColor = LearningColors.AccentStar.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = LearningColors.AccentStar)
                    Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                        Text("我的星星", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "$totalPoints 颗",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text("去兑换 →", fontSize = 13.sp, color = LearningColors.PrimaryLight)
                }
            }

            // 新孩子先做一次 12 题摸底测评：不分年级题库，用测评找到起点
            if (showDiagnosticEntry) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStartDiagnostic() },
                    colors = CardDefaults.cardColors(
                        containerColor = LearningColors.CorrectGreen.copy(alpha = 0.14f)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "🗺 先做一次小测评",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "12 道题，大概 5 分钟。摸清你在哪个水平，之后只练刚好不会太难的内容。",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // 今日 Loop：只练一个点
            todayPlan?.let { plan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStartToday() },
                    colors = CardDefaults.cardColors(
                        containerColor = LearningColors.PrimaryLight.copy(alpha = 0.14f)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "今天的挑战",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            "${plan.topicName} · ${plan.anchorName}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${plan.levelLabel}层 · ${plan.reason}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                        )
                        Text(
                            "只有 ${plan.questions.size} 道题，做完就结束",
                            fontSize = 12.sp,
                            color = LearningColors.PrimaryLight,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 学科选择卡片（开始学习）：幼儿园显示「语言 / 思维」，小学显示「语文 / 数学」
            val isKg = stage == Stage.KG
            Text(
                if (isKg) "今天玩什么 🧸" else "今日任务 📚",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SubjectCard(
                    emoji = if (isKg) "🗣" else "🌸",
                    subject = if (isKg) "语言" else "语文",
                    modifier = Modifier.weight(1f),
                    selected = false
                ) {
                    onStartQuiz("chinese")
                }
                SubjectCard(
                    emoji = if (isKg) "🧠" else "🔢",
                    subject = if (isKg) "思维" else "数学",
                    modifier = Modifier.weight(1f),
                    selected = false
                ) {
                    onStartQuiz("math")
                }
            }

            // 错题本入口
            if (wrongCount > 0) {
                ActionCard(
                    emoji = "📒",
                    title = "错题本 ($wrongCount)",
                    subtitle = "把做错的题再做一次吧！",
                    color = LearningColors.WrongRed
                ) { onOpenWrongBook() }
            }

            TextButton(
                onClick = onOpenAbout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ℹ️ 关于小步 · 这是怎么玩的", fontSize = 13.sp)
            }
        }
    }

    if (showSwitcher) {
        ChildSwitcherDialog(
            children = children,
            activeId = activeId,
            onSelect = { id ->
                showSwitcher = false
                onSelectChild(id)
            },
            onDismiss = { showSwitcher = false }
        )
    }
}

/** 免密切换孩子：只做「切换」，不提供新增 / 修改 / 删除 */
@Composable
private fun ChildSwitcherDialog(
    children: List<ChildProfile>,
    activeId: Long,
    onSelect: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("换一个小朋友") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (children.isEmpty()) {
                    Text("还没有小朋友档案，请到家长面板添加", fontSize = 14.sp)
                }
                children.forEach { child ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(child.id) }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(child.avatar.ifBlank { "🧒" }, fontSize = 26.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = child.name,
                                fontSize = 16.sp,
                                fontWeight = if (child.id == activeId) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "${child.grade.ifBlank { "未填年级" }} · ${Stage.label(child.stage)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                            )
                        }
                        if (child.id == activeId) {
                            Text("✓", color = LearningColors.CorrectGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("关闭") }
        }
    )
}

@Composable
private fun SubjectCard(
    emoji: String, subject: String, selected: Boolean,
    modifier: Modifier = Modifier, onClick: () -> Unit
) {
    val bg = if (selected) LearningColors.PrimaryLight.copy(alpha = 0.3f)
             else MaterialTheme.colorScheme.surface
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = bg)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 36.sp)
            Text(subject, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ActionCard(
    emoji: String, title: String, subtitle: String,
    color: androidx.compose.ui.graphics.Color, onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 26.sp) }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

// 为避免未使用 import 警告
private val _heightHint = Modifier.height(0.dp)
