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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import com.learningloop.app.data.Stage
import com.learningloop.app.data.QuestionCsv
import com.learningloop.app.data.entity.Question
import com.learningloop.app.ui.theme.LearningColors

/**
 * 题库管理：让题库从「写死在代码里」变成「可看、可改、可导入导出」的显性资产。
 */
@Composable
fun QuestionManageScreen(
    questions: List<Question>,
    message: String?,
    onSave: (Question) -> Unit,
    onToggle: (Question, Boolean) -> Unit,
    onDelete: (Question) -> Unit,
    onCreate: () -> Unit,
    onImport: () -> Unit,
    onExport: () -> Unit,
    onBack: () -> Unit
) {
    var editing by remember { mutableStateOf<Question?>(null) }
    var creating by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var stageFilter by remember { mutableStateOf<String?>(null) }

    if (creating || editing != null) {
        QuestionEditor(
            initial = editing,
            onSave = {
                creating = false
                editing = null
                onSave(it)
            },
            onDelete = {
                creating = false
                editing = null
                onDelete(it)
            },
            onCancel = {
                creating = false
                editing = null
            }
        )
        return
    }

    val filtered = remember(questions, query, stageFilter) {
        questions.filter { q ->
            (stageFilter == null || q.stage == stageFilter) &&
                (query.isBlank() ||
                    q.content.contains(query, true) ||
                    q.knowledgePoint.contains(query, true))
        }
    }
    val enabledCount = questions.count { it.enabled }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScreenHeader(title = "题库管理", subtitle = "可修正错题、停用不合适的题、批量导入扩展", onBack = onBack)
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton(onClick = { creating = true }) { Text("＋ 新增", fontSize = 13.sp) }
            TextButton(onClick = onImport) { Text("导入 CSV", fontSize = 13.sp) }
            TextButton(onClick = onExport) { Text("导出 CSV", fontSize = 13.sp) }
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("搜索题干或知识点") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(
                null to "全部",
                Stage.KG to "幼儿园",
                Stage.PRIMARY_LOW to "小学低年级"
            ).forEach { (value, label) ->
                FilterChip(
                    selected = stageFilter == value,
                    onClick = { stageFilter = value },
                    label = { Text(label, fontSize = 12.sp) }
                )
            }
        }
        Text(
            "共 ${questions.size} 题 · 启用 $enabledCount 题 · 当前显示 ${filtered.size} 题",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
        )
        message?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(6.dp))
            Text(it, fontSize = 12.sp, color = LearningColors.CorrectGreen)
        }
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered, key = { it.id }) { q ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { editing = q }
                        ) {
                            Text(
                                q.content,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${Stage.label(q.stage)} · ${subjectLabel(q.subject)} · ${q.knowledgePoint} · 难度${q.difficulty} · ${typeLabel(q.type)}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                        Switch(
                            checked = q.enabled,
                            onCheckedChange = { onToggle(q, it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionEditor(
    initial: Question?,
    onSave: (Question) -> Unit,
    onDelete: (Question) -> Unit,
    onCancel: () -> Unit
) {
    var subject by remember { mutableStateOf(initial?.subject ?: Question.SUBJECT_CHINESE) }
    var knowledgePoint by remember { mutableStateOf(initial?.knowledgePoint ?: "") }
    var difficulty by remember { mutableStateOf(initial?.difficulty ?: 3) }
    var type by remember { mutableStateOf(initial?.type ?: Question.TYPE_CHOICE) }
    var content by remember { mutableStateOf(initial?.content ?: "") }
    var options by remember { mutableStateOf(QuestionCsv.jsonToOptions(initial?.options ?: "")) }
    var answer by remember { mutableStateOf(initial?.answer ?: "") }
    var explanation by remember { mutableStateOf(initial?.explanation ?: "") }
    var enabled by remember { mutableStateOf(initial?.enabled ?: true) }
    var stage by remember { mutableStateOf(initial?.stage ?: Stage.PRIMARY_LOW) }
    var error by remember { mutableStateOf<String?>(null) }
    var confirmDelete by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ScreenHeader(
            title = if (initial == null) "新增题目" else "编辑题目",
            subtitle = if (initial == null) "新增的题目会立即进入题库" else "修改会立即生效，不影响历史作答记录",
            onBack = onCancel
        )
        Spacer(Modifier.height(10.dp))

        Text("学段", fontSize = 13.sp, fontWeight = FontWeight.Bold)
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

        Spacer(Modifier.height(8.dp))
        Text("学科", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = subject == Question.SUBJECT_CHINESE,
                onClick = { subject = Question.SUBJECT_CHINESE },
                label = { Text("语文") }
            )
            FilterChip(
                selected = subject == Question.SUBJECT_MATH,
                onClick = { subject = Question.SUBJECT_MATH },
                label = { Text("数学") }
            )
        }

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = knowledgePoint,
            onValueChange = { knowledgePoint = it },
            label = { Text("知识点，如 chinese-古诗-登鹳雀楼") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        Text("题型", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val typeOptions = buildList {
                add(Question.TYPE_CHOICE to "选择")
                // 仅保留历史填空题的管理入口，新建题目不再提供填空
                if (type == Question.TYPE_FILL) add(Question.TYPE_FILL to "填空（已停用）")
                add(Question.TYPE_WORD to "应用/开放")
            }
            typeOptions.forEach { (v, label) ->
                FilterChip(
                    selected = type == v,
                    onClick = { type = v },
                    label = { Text(label) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("难度（1 最易，5 最难）", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            (1..5).forEach { d ->
                FilterChip(
                    selected = difficulty == d,
                    onClick = { difficulty = d },
                    label = { Text("$d") }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("题干") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        if (type == Question.TYPE_CHOICE) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = options,
                onValueChange = { options = it },
                label = { Text("选项，用 | 分隔，如 A.妈妈|B.马马|C.吗吗") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = answer,
            onValueChange = { answer = it },
            label = { Text(if (type == Question.TYPE_CHOICE) "正确答案（填字母，如 A）" else "正确答案") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = explanation,
            onValueChange = { explanation = it },
            label = { Text("解析 / 参考思路") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("启用此题", fontSize = 14.sp, modifier = Modifier.weight(1f))
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }

        error?.let {
            Spacer(Modifier.height(6.dp))
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        Spacer(Modifier.height(14.dp))
        Button(
            onClick = {
                when {
                    content.isBlank() -> error = "题干不能为空"
                    answer.isBlank() -> error = "答案不能为空"
                    else -> onSave(
                        Question(
                            id = initial?.id ?: 0L,
                            subject = subject,
                            knowledgePoint = knowledgePoint.ifBlank { "$subject-未分类" },
                            difficulty = difficulty,
                            type = type,
                            content = content.trim(),
                            options = if (type == Question.TYPE_CHOICE)
                                QuestionCsv.optionsToJson(options) else "",
                            answer = answer.trim(),
                            explanation = explanation.trim(),
                            tags = initial?.tags ?: "",
                            enabled = enabled,
                            source = initial?.source ?: Question.SOURCE_CUSTOM,
                            updatedAt = System.currentTimeMillis(),
                            anchorCode = initial?.anchorCode ?: "",
                            cognitiveLevel = initial?.cognitiveLevel ?: Question.LEVEL_MEMORY,
                            variantType = initial?.variantType ?: "",
                            stage = stage
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) { Text("保存", fontWeight = FontWeight.Bold) }

        if (initial != null) {
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = { confirmDelete = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("删除此题", color = MaterialTheme.colorScheme.error)
            }
        }
        Spacer(Modifier.height(20.dp))
    }

    if (confirmDelete && initial != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("删除题目") },
            text = { Text("删除后不可恢复。若只是暂时不用，建议改为「停用」。确定删除吗？") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    onDelete(initial)
                }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("取消") }
            }
        )
    }
}

private fun subjectLabel(subject: String): String =
    if (subject == Question.SUBJECT_MATH) "数学" else "语文"

private fun typeLabel(type: String): String = when (type) {
    Question.TYPE_CHOICE -> "选择"
    Question.TYPE_FILL -> "填空（已停用）"
    else -> "开放"
}
