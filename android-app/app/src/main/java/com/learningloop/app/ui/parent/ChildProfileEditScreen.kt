package com.learningloop.app.ui.parent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learningloop.app.ScreenHeader
import com.learningloop.app.data.Stage
import com.learningloop.app.data.entity.ChildProfile

private val avatarEmojis = listOf("🧒", "👧", "👦", "🐼", "🦊", "🐻", "🐰", "🐱")

@Composable
fun ChildProfileEditScreen(
    initial: ChildProfile = ChildProfile(),
    onSave: (ChildProfile) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var name by remember { mutableStateOf(initial.name) }
    var grade by remember { mutableStateOf(initial.grade) }
    var avatar by remember { mutableStateOf(initial.avatar) }
    var stage by remember { mutableStateOf(initial.stage) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScreenHeader(
                title = "编辑孩子画像",
                subtitle = "称谓与头像会影响孩子的使用体验",
                onBack = onBack
            )

            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("姓名") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = grade,
                onValueChange = {
                    grade = it
                    // 填「幼儿园中班」这类描述时自动切到幼儿园题库
                    stage = Stage.fromGrade(it)
                },
                label = { Text("年级，如 二年级 / 幼儿园中班") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Text("学段（决定使用哪套题库）", fontWeight = FontWeight.Medium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = stage == Stage.KG,
                    onClick = { stage = Stage.KG },
                    label = { Text("幼儿园") }
                )
                FilterChip(
                    selected = stage == Stage.PRIMARY_LOW,
                    onClick = { stage = Stage.PRIMARY_LOW },
                    label = { Text("小学低年级") }
                )
            }

            Text("选择头像", fontWeight = FontWeight.Medium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(avatarEmojis) { e ->
                    val selected = avatar == e
                    Surface(
                        shape = CircleShape,
                        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = e, fontSize = 32.sp,
                            modifier = Modifier.padding(10.dp).clickable { avatar = e }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        onSave(
                            initial.copy(
                                name = name, grade = grade, avatar = avatar, stage = stage
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("保存") }
            }
        }
    }
}
