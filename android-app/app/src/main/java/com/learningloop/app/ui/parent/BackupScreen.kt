package com.learningloop.app.ui.parent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learningloop.app.ScreenHeader
import com.learningloop.app.ui.theme.LearningColors

/**
 * 数据备份：换机 / 重装前导出，导入后恢复到导出时的状态。
 */
@Composable
fun BackupScreen(
    stats: String,
    message: String?,
    onExport: () -> Unit,
    onImport: () -> Unit,
    onBack: () -> Unit
) {
    var confirmImport by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScreenHeader(title = "数据备份与恢复", subtitle = "换机、重装前请先导出", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("当前数据", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(stats, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "导出内容包含：孩子档案、学习记录、错题本、知识点掌握度、积分流水、奖励配置。\n" +
                "题库不包含在内，题库请用「题库管理」的 CSV 单独备份。",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onExport,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LearningColors.PrimaryLight),
            shape = RoundedCornerShape(12.dp)
        ) { Text("导出备份文件", fontWeight = FontWeight.Bold) }

        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = { confirmImport = true },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) { Text("从备份文件恢复") }

        message?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(14.dp))
            Text(it, fontSize = 12.sp, color = LearningColors.CorrectGreen)
        }
    }

    if (confirmImport) {
        AlertDialog(
            onDismissRequest = { confirmImport = false },
            title = { Text("覆盖当前数据？") },
            text = { Text("恢复会用备份文件的内容替换当前所有孩子与学习数据，且不可撤销。建议先导出一份当前数据。") },
            confirmButton = {
                TextButton(onClick = {
                    confirmImport = false
                    onImport()
                }) { Text("确认恢复", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmImport = false }) { Text("取消") }
            }
        )
    }
}
