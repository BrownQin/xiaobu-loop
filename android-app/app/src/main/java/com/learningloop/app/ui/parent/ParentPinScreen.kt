package com.learningloop.app.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learningloop.app.ScreenHeader
import com.learningloop.app.data.ParentPinStore
import com.learningloop.app.ui.theme.LearningColors
import kotlinx.coroutines.delay

/**
 * 家长端 4 位数字密码闸门。
 *
 * - 从未设置过密码：引导设置两次
 * - 已设置：校验
 * - forceSet = true：跳过校验直接重设（调用方须已通过验证）
 */
@Composable
fun ParentPinScreen(
    pinStore: ParentPinStore,
    forceSet: Boolean = false,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var setting by remember(forceSet) { mutableStateOf(forceSet || !pinStore.hasPin()) }
    var confirming by remember(forceSet) { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }
    var firstPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    fun submit(value: String) {
        if (setting) {
            when {
                !confirming -> {
                    firstPin = value
                    confirming = true
                    input = ""
                    error = null
                }
                value == firstPin -> {
                    pinStore.pin = value
                    onSuccess()
                }
                else -> {
                    confirming = false
                    firstPin = ""
                    input = ""
                    error = "两次输入不一致，请重新设置"
                }
            }
        } else {
            if (pinStore.verify(value)) {
                onSuccess()
            } else {
                input = ""
                error = "密码不正确，请重试"
            }
        }
    }

    LaunchedEffect(input) {
        if (input.length == 4) {
            delay(120)
            submit(input)
        }
    }

    val title = when {
        setting && confirming -> "请再次输入确认"
        setting -> "设置家长密码"
        else -> "家长验证"
    }
    val subtitle = when {
        setting && confirming -> "两次输入需要一致"
        setting -> "首次进入请设置 4 位数字密码，防止孩子误操作"
        else -> "请输入 4 位家长密码"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeader(title = "家长中心", onBack = onBack)
        Spacer(Modifier.height(36.dp))

        Text("🔒", fontSize = 42.sp)
        Spacer(Modifier.height(14.dp))
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(
            subtitle,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(Modifier.height(26.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            repeat(4) { i ->
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (i < input.length) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            error ?: " ",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.height(20.dp)
        )

        Spacer(Modifier.height(18.dp))

        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("⌫", "0", "✓")
        )
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                row.forEach { key ->
                    Spacer(Modifier.size(6.dp))
                    KeyButton(key) {
                        when (key) {
                            "⌫" -> {
                                if (input.isNotEmpty()) input = input.dropLast(1)
                                error = null
                            }
                            "✓" -> {
                                if (input.length == 4) submit(input)
                                else error = "请输入完整的 4 位密码"
                            }
                            else -> {
                                if (input.length < 4) {
                                    input += key
                                    error = null
                                }
                            }
                        }
                    }
                    Spacer(Modifier.size(6.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun KeyButton(label: String, onClick: () -> Unit) {
    val isConfirm = label == "✓"
    Surface(
        modifier = Modifier
            .size(74.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = if (isConfirm) LearningColors.PrimaryLight.copy(alpha = 0.18f)
        else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                label,
                fontSize = 24.sp,
                fontWeight = if (isConfirm) FontWeight.Bold else FontWeight.Medium,
                color = if (isConfirm) LearningColors.PrimaryLight
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
