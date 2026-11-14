package com.learningloop.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 首次启动的隐私告知与选择页。
 *
 * 合规要求：在用户（监护人）明确选择前不采集任何统计数据。
 * 两个选项都只是「是否开启匿名统计」的区别，不开启也完整保留学习功能。
 */
@Composable
fun PrivacyConsentScreen(
    onAgreeWithTelemetry: () -> Unit,
    onLocalOnly: () -> Unit,
    onOpenPolicy: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "开始前，先说清楚",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "小步是给小朋友用的，所以把隐私讲得简单一点。",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        ConsentCard(
            title = "学习数据只留在本机",
            lines = listOf(
                "孩子档案、作答记录、错题、星星都保存在这台设备上。",
                "卸载或清除数据后即删除，开发者无法读取。",
                "只有你主动导出备份 / 诊断日志时，数据才会离开设备。"
            )
        )

        ConsentCard(
            title = "可选的匿名统计",
            lines = listOf(
                "只上报 6 个字段：事件名、随机安装 ID、版本、渠道、学段、时间戳。",
                "只统计启动、打开、完成练习、完成测评，不含姓名、作答内容与设备标识。",
                "用于判断版本是否可用、孩子是否卡在测评环节。",
                "你可以现在拒绝，之后也能在家长区随时开关。"
            )
        )

        ConsentCard(
            title = "需要监护人确认",
            lines = listOf(
                "如果你未满 14 周岁，请让家长或监护人阅读并代为选择。",
                "选择「仅本地使用」不会影响任何学习功能。"
            )
        )

        Button(
            onClick = onAgreeWithTelemetry,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) { Text("同意并开启匿名统计", fontSize = 16.sp, fontWeight = FontWeight.Bold) }

        OutlinedButton(
            onClick = onLocalOnly,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) { Text("仅本地使用（不发送统计）", fontSize = 15.sp) }

        TextButton(
            onClick = onOpenPolicy,
            modifier = Modifier.fillMaxWidth()
        ) { Text("查看完整隐私政策", fontSize = 13.sp) }

        Text(
            "你可以随时在「管理 → 说明」中更改统计开关。",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ConsentCard(title: String, lines: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            lines.forEach { line ->
                Text(
                    "· $line",
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f)
                )
            }
        }
    }
}
