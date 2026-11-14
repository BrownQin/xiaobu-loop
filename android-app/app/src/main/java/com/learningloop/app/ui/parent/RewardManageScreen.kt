package com.learningloop.app.ui.parent

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import com.learningloop.app.data.entity.RewardItem
import com.learningloop.app.ui.theme.LearningColors

/**
 * 家长端奖励管理：完全由家长自定义奖励名称、所需积分、图标与是否上架。
 */
@Composable
fun RewardManageScreen(
    rewards: List<RewardItem>,
    onSave: (RewardItem) -> Unit,
    onToggle: (RewardItem) -> Unit,
    onDelete: (RewardItem) -> Unit,
    onBack: () -> Unit
) {
    var editing by remember { mutableStateOf<RewardItem?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ScreenHeader(
            title = "奖励管理",
            subtitle = "自定义孩子能兑换的奖励",
            onBack = onBack
        )

        if (rewards.isEmpty()) {
            Text(
                "还没有奖励，点击下方按钮添加第一个。",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        rewards.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.emoji, fontSize = 26.sp)
                    Column(modifier = Modifier.padding(start = 10.dp).weight(1f)) {
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            "${item.points} 颗星" +
                                if (item.description.isNotBlank()) " · ${item.description}" else "",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Switch(checked = item.enabled, onCheckedChange = { onToggle(item) })
                    TextButton(onClick = {
                        editing = item
                        showDialog = true
                    }) { Text("编辑", fontSize = 12.sp) }
                    TextButton(onClick = { onDelete(item) }) {
                        Text("删除", fontSize = 12.sp, color = LearningColors.WrongRed)
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        Button(
            onClick = {
                editing = RewardItem(name = "")
                showDialog = true
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LearningColors.PrimaryLight),
            shape = RoundedCornerShape(14.dp)
        ) { Text("+ 添加奖励", fontWeight = FontWeight.Bold) }
    }

    if (showDialog) {
        RewardEditDialog(
            initial = editing ?: RewardItem(),
            onDismiss = { showDialog = false },
            onConfirm = {
                onSave(it)
                showDialog = false
            }
        )
    }
}

@Composable
private fun RewardEditDialog(
    initial: RewardItem,
    onDismiss: () -> Unit,
    onConfirm: (RewardItem) -> Unit
) {
    var name by remember { mutableStateOf(initial.name) }
    var emoji by remember { mutableStateOf(initial.emoji) }
    var points by remember { mutableStateOf(initial.points.toString()) }
    var desc by remember { mutableStateOf(initial.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial.id > 0) "编辑奖励" else "添加奖励") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("奖励名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = emoji,
                    onValueChange = { emoji = it },
                    label = { Text("图标（emoji）") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = points,
                    onValueChange = { points = it.filter { c -> c.isDigit() } },
                    label = { Text("所需星星（颗）") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("说明（可选）") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank() && (points.toLongOrNull() ?: 0L) > 0,
                onClick = {
                    onConfirm(
                        initial.copy(
                            name = name.trim(),
                            emoji = emoji.ifBlank { "🎁" },
                            points = points.toLongOrNull() ?: 100L,
                            description = desc.trim()
                        )
                    )
                }
            ) { Text("保存") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
