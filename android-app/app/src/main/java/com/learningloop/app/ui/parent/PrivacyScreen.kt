package com.learningloop.app.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learningloop.app.BuildConfig
import com.learningloop.app.ScreenHeader
import com.learningloop.app.data.TelemetryStore

/**
 * App 内可读的隐私政策，内容与 project-docs/PRIVACY.md 保持一致。
 * 离线可查，不需要联网。
 */
@Composable
fun PrivacyScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val installId = TelemetryStore.installId(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScreenHeader(
            title = "隐私政策",
            subtitle = "本地优先，统计最小化 · v${BuildConfig.VERSION_NAME}",
            onBack = onBack
        )

        PrivacyCard(
            "一、本地优先",
            listOf(
                "孩子档案、题库、作答记录、错题、掌握度、星星与奖励只保存在本机数据库。",
                "卸载 App 或清除数据后，这些内容即被删除，开发者无法读取或恢复。",
                "只有家长主动导出备份、诊断日志或题库 CSV 时，数据才会以文件形式离开设备。"
            )
        )

        PrivacyCard(
            "二、匿名使用统计（默认开启，可关闭）",
            listOf(
                "为判断版本是否被使用、有没有人卡在启动或测评环节，App 会发送事件级匿名数据。",
                "采集字段只有 6 个：事件名、安装随机 ID、版本号、渠道、学段、时间戳。",
                "事件只有 4 种：首次启动、当天首次打开、完成练习、完成摸底测评。",
                "安装随机 ID 由 App 本地生成，重装后变化，不是设备标识，不能跨应用追踪。",
                "学段仅用于判断题目难度是否匹配，不包含年级、班级、学校等身份信息。"
            )
        )

        PrivacyCard(
            "三、明确不采集",
            listOf(
                "孩子姓名、头像、年级班级、学校；",
                "题目内容、孩子的作答内容、错题、得分、星星与奖励明细；",
                "设备型号、系统版本、IMEI、Android ID、广告 ID、MAC 地址；",
                "位置、通讯录、相册、通话记录、剪贴板。"
            )
        )

        PrivacyCard(
            "四、关闭方式",
            listOf(
                "家长区 → 管理 → 说明 → 关闭「匿名使用统计」。",
                "关闭后立即停止产生与发送新事件，本地待发送队列会被清空。",
                "关闭不影响任何学习功能。"
            )
        )

        PrivacyCard(
            "五、数据保留",
            listOf(
                "待发送事件发送成功后立即从本机删除；未发送最多保留 30 天或重试 3 次后丢弃。",
                "接收端只保存上述 6 个字段，明细保留 90 天，之后仅保留不含安装 ID 的聚合统计。",
                "删除请求可通过 GitHub Issues 提交，并附上下方安装标识。"
            )
        )

        PrivacyCard(
            "六、儿童信息保护",
            listOf(
                "不收集任何可识别儿童身份的个人信息。",
                "家长可随时关闭统计，或直接清除本地数据。"
            )
        )

        PrivacyCard(
            "七、第三方",
            listOf(
                "不接入广告 SDK，不接入第三方分析 SDK。",
                "统计接收端为飞书多维表格 Webhook，仅接收上述 6 个字段。"
            )
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("我的匿名安装标识", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(
                    installId,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Text(
                    "申请删除统计数据时提供此标识即可，不需要公开真实身份。",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                )
            }
        }

        Text(
            text = "完整政策：项目文档 PRIVACY.md · 生效日期 2026-11-14",
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
        )
    }
}

@Composable
private fun PrivacyCard(title: String, lines: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            lines.forEach { line ->
                Text(
                    text = "· $line",
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }
        }
    }
}
