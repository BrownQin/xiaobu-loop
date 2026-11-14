package com.learningloop.app.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learningloop.app.BuildConfig
import com.learningloop.app.ScreenHeader
import com.learningloop.app.ui.theme.LearningColors

/**
 * 关于小步：面向家长和孩子解释 App 在做什么、术语是什么意思、为什么这样设计。
 * 全部内置文案，离线可读。
 */
@Composable
fun AboutScreen(onBack: () -> Unit = {}, onOpenPrivacy: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScreenHeader(
            title = "关于小步",
            subtitle = "不催作业，只帮孩子把卡住的那一小步走通",
            onBack = onBack
        )

        AboutCard(
            "小步是什么",
            listOf(
                "小步是一个装在手机或平板上的伴学 App，所有学习记录都保存在本机，不上传云端。",
                "它不追求「多做题」，而是每天找到孩子最该练的那一个点，用 3 道题确认有没有真正学会。",
                "家长看到的是结论和下一步怎么做，而不是一长串分数。"
            )
        )

        AboutCard(
            "为什么一天只做 3 道题",
            listOf(
                "低龄孩子的注意力和挫败耐受都很有限。题一多，后半段的数据就不再可信。",
                "3 道题足够判断「这个点会不会」，也足够让孩子带着完成感结束。",
                "如果 3 道题还没通过，说明这一步确实卡住了，第二天会换更小的台阶，而不是继续加量。"
            )
        )

        AboutCard(
            "什么是「锚点」",
            listOf(
                "锚点是被拆到最小、又能被观察和练习的能力单元，比如「退位减法的算理」「多音字辨读」。",
                "它和普通「知识点」的区别是：锚点一定要能说清「孩子做到什么样子才算会」。",
                "每个锚点都有前置关系。前置没过，系统不会硬推后面的内容。"
            )
        )

        AboutCard(
            "记忆 / 理解 / 应用 是什么",
            listOf(
                "记忆：能认出来、能说出名称。例如看到 53 减 27 知道要退位。",
                "理解：能解释为什么。例如能说清「借一当十」到底借的是什么。",
                "应用：能在新情境里用出来。例如换成购物找零、换成竖式也能算对。",
                "三层单独记录。很多孩子是「记忆过了、理解没过」——会做但说不清，这正是家长最该介入的地方。"
            )
        )

        AboutCard(
            "星星怎么来",
            listOf(
                "答对 1 道题得 1 颗星，挑战题多 1 颗，连对 3 道再奖励 1 颗。",
                "某个能力从「攻坚中」变成「已稳固」，额外 +3 颗。",
                "答错不扣星。错题会进入错题本，过几天再遇到一次。",
                "家长也可以手工发星，用来鼓励整理书包、主动阅读这类好习惯。"
            )
        )

        AboutCard(
            "家长可以做什么",
            listOf(
                "看「锚点掌握矩阵」：一眼看出孩子卡在记忆、理解还是应用。",
                "看断层提示：例如「记忆层已过关，理解层还没通过」，据此决定是让孩子讲一遍，还是继续练。",
                "改题库：支持 CSV 导入导出，学校发了新卷子可以直接补进来。",
                "备份数据：换设备或重装前导出一份，回来再恢复。"
            )
        )

        AboutCard(
            "数据与隐私",
            listOf(
                "题库、答题记录、星星和奖励都只存在本机数据库里。",
                "只有家长主动导出时，数据才会以文件形式离开设备。",
                "匿名使用统计默认开启，只上报事件级数据，不含孩子与作答内容，可随时关闭。",
                "联网只用于匿名统计与可选的 AI 反馈文案，孩子做题本身完全离线。"
            )
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenPrivacy() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("隐私政策", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "采集什么、不采集什么、如何关闭",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                    )
                }
                Text(
                    "›",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
                )
            }
        }

        Text(
            text = "小步 v${BuildConfig.VERSION_NAME} · 本地伴学",
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
        )
    }
}

@Composable
private fun AboutCard(title: String, lines: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
