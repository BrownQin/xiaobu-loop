package com.learningloop.app.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learningloop.app.ScreenHeader
import com.learningloop.app.ui.theme.LearningColors

/**
 * 家长管理区入口。
 *
 * 与「学习报告」分离：报告是看结论，管理是改配置，两个维度不混在同一屏。
 * 所有子页面的返回都回到这里，不再跳回首页。
 */
@Composable
fun ManageScreen(
    childName: String,
    onOpenReport: () -> Unit = {},
    onOpenChildren: () -> Unit = {},
    onOpenPoints: () -> Unit = {},
    onOpenRewards: () -> Unit = {},
    onOpenQuestions: () -> Unit = {},
    onOpenQuality: () -> Unit = {},
    onRediagnose: () -> Unit = {},
    onOpenBackup: () -> Unit = {},
    onExportLog: () -> Unit = {},
    onOpenVersions: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    telemetryEnabled: Boolean = true,
    onToggleTelemetry: (Boolean) -> Unit = {},
    onOpenPrivacy: () -> Unit = {},
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
        ScreenHeader(title = "管理", subtitle = "当前孩子：$childName", onBack = onBack)

        ManageCard("查看") {
            ManageItem("📈", "学习报告", "锚点矩阵、薄弱点、趋势与建议", onOpenReport)
            ManageItem("🗺", "重新测评", "12 道题重新定位当前年级水平", onRediagnose)
        }

        ManageCard("孩子与激励") {
            ManageItem("👶", "孩子档案", "新增、编辑、切换、密码", onOpenChildren)
            ManageItem("⭐", "星星调整", "手工奖励或扣减", onOpenPoints)
            ManageItem("🎁", "奖励设置", "定义可以用星星换什么", onOpenRewards)
        }

        ManageCard("题库与数据") {
            ManageItem("📚", "题库管理", "查看、修正、导入导出题目", onOpenQuestions)
            ManageItem("📊", "题目质量", "用作答数据检查题目是否合适", onOpenQuality)
            ManageItem("💾", "数据备份", "换设备前导出，以免丢失", onOpenBackup)
            ManageItem("🧾", "导出诊断日志", "导出后发给开发者分析", onExportLog)
        }

        ManageCard("说明") {
            ManageItem("🔒", "隐私政策", "采集什么、不采集什么、如何关闭", onOpenPrivacy)
            ManageToggle(
                emoji = "📶",
                title = "匿名使用统计",
                desc = "只报事件级数据，不含孩子与作答内容",
                checked = telemetryEnabled,
                onCheckedChange = onToggleTelemetry
            )
            ManageItem("📋", "更新记录", "每个版本改了什么", onOpenVersions)
            ManageItem("ℹ️", "关于小步", "术语与设计原理", onOpenAbout)
        }
    }
}

/** 家长手工调整星星。单次上限 10 颗，避免一次给出孩子数不清的量。 */
@Composable
fun AdjustPointsScreen(
    childName: String,
    currentPoints: Long,
    onAdjust: (Long, String) -> Unit,
    onBack: () -> Unit = {}
) {
    var amount by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var hint by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenHeader(title = "星星调整", subtitle = "$childName · 当前 $currentPoints 颗星", onBack = onBack)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "用于即时肯定主动行为，也可对违反约定做少量扣减。单次最多 10 颗。",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1L, 2L, 5L).forEach { v ->
                        TextButton(onClick = { amount = v.toString() }) { Text("+$v") }
                    }
                    TextButton(onClick = { amount = "-1" }) { Text("-1") }
                }
                OutlinedTextField(
                    value = amount,
                    onValueChange = { s ->
                        amount = s.filter { it.isDigit() || it == '-' }.take(3)
                    },
                    label = { Text("颗数（正数奖励 / 负数扣减）") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it.take(20) },
                    label = { Text("原因，如：主动整理书包") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        val delta = amount.toLongOrNull()
                        hint = when {
                            delta == null || delta == 0L -> "请输入非 0 的颗数"
                            delta > 10 || delta < -10 -> "单次最多 10 颗"
                            else -> {
                                onAdjust(delta, reason.trim())
                                amount = ""
                                reason = ""
                                if (delta > 0) "已发放 $delta 颗星" else "已扣减 ${-delta} 颗星"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LearningColors.PrimaryLight),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("确认调整", fontWeight = FontWeight.Bold) }
                hint?.let {
                    Text(it, fontSize = 12.sp, color = LearningColors.CorrectGreen)
                }
            }
        }
    }
}

@Composable
private fun ManageCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                title,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            content()
        }
    }
}

@Composable
private fun ManageItem(emoji: String, title: String, desc: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(emoji, fontSize = 20.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(
                desc,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
        Text("›", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
    }
}

@Composable
private fun ManageToggle(
    emoji: String,
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(emoji, fontSize = 20.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(
                desc,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
        // 整行可点，减少家长的操作精度要求
        Switch(checked = checked, onCheckedChange = null)
    }
}
