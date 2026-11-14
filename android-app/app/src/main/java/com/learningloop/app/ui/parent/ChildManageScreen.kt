package com.learningloop.app.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learningloop.app.ScreenHeader
import com.learningloop.app.data.Stage
import com.learningloop.app.data.entity.ChildProfile
import com.learningloop.app.ui.theme.LearningColors

/**
 * 孩子管理：一个家庭可以有多个孩子，各自的学习记录、错题、积分完全隔离。
 */
@Composable
fun ChildManageScreen(
    children: List<ChildProfile>,
    activeId: Long,
    onSwitch: (Long) -> Unit,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (ChildProfile) -> Unit,
    onManageRewards: () -> Unit,
    onManagePin: () -> Unit,
    onManageQuestions: () -> Unit,
    onManageBackup: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScreenHeader(
            title = "孩子管理",
            subtitle = "每个孩子的学习数据完全独立",
            onBack = onBack
        )

        children.forEach { child ->
            val isActive = child.id == activeId
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSwitch(child.id) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isActive)
                        LearningColors.PrimaryLight.copy(alpha = 0.14f)
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(LearningColors.SecondaryLight.copy(alpha = 0.2f)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) { Text(child.avatar, fontSize = 26.sp) }

                    Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(child.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            if (isActive) {
                                Text(
                                    " 使用中",
                                    fontSize = 11.sp,
                                    color = LearningColors.PrimaryLight,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            "${child.grade} · ${Stage.label(child.stage)} · ⭐ ${child.totalPoints} 颗星",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    TextButton(onClick = { onEdit(child.id) }) { Text("编辑", fontSize = 13.sp) }
                    if (children.size > 1) {
                        TextButton(onClick = { onDelete(child) }) {
                            Text("删除", fontSize = 13.sp, color = LearningColors.WrongRed)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        Button(
            onClick = onAdd,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LearningColors.PrimaryLight),
            shape = RoundedCornerShape(14.dp)
        ) { Text("+ 添加孩子", fontWeight = FontWeight.Bold) }

        Button(
            onClick = onManageRewards,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(14.dp)
        ) { Text("🎁 奖励管理", fontWeight = FontWeight.Bold) }

        Button(
            onClick = onManagePin,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(14.dp)
        ) { Text("🔒 修改家长密码", fontWeight = FontWeight.Bold) }

        Button(
            onClick = onManageQuestions,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(14.dp)
        ) { Text("📚 题库管理", fontWeight = FontWeight.Bold) }

        Button(
            onClick = onManageBackup,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(14.dp)
        ) { Text("💾 数据备份与恢复", fontWeight = FontWeight.Bold) }

        Text(
            "提示：切换孩子后，首页积分、错题本、家长报告都会切换到该孩子的数据。",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}
