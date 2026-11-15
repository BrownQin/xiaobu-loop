# 技术架构说明

**版本**：v0.12.0 ｜ **最后更新**：2026-11-13

## 一、技术选型

| 层 | 选型 | 版本 | 选择理由 |
|---|---|---|---|
| 语言 | Kotlin | 1.9.22 | 官方主推，协程原生支持 |
| UI | Jetpack Compose + Material 3 | BOM 2024.02 | 声明式 UI，快速迭代 |
| 本地存储 | Room | 2.6.1 | 编译期校验 SQL，支持 Flow 响应式 |
| 异步 | Kotlin Coroutines + Flow | — | 与 Room / Compose 无缝集成 |
| 构建 | AGP 8.2.2 / Gradle KTS | — | 标准 Android 工程 |
| 最低版本 | minSdk 24（Android 7.0） | — | 覆盖主流存量平板 |

**核心约束**：无公有云服务器。所有数据本地存储，LLM 仅作为可选增强，离线自动降级。

## 二、代码结构

```
app/src/main/java/com/learningloop/app/
├── MainActivity.kt              # 单 Activity + Screen 状态机 + LearningViewModel
├── SeedData.kt                  # 287 道种子题库（小学 187 / 幼儿园 100）
├── data/
│   ├── AppDatabase.kt           # Room 数据库 v7，含 v1→v2→…→v7 迁移
│   ├── VersionHistory.kt        # App 内置版本更新记录
│   ├── VoiceSettings.kt         # 朗读语速 / 音色（SharedPreferences）
│   ├── Stage.kt                 # 学段常量（KG / PRIMARY_LOW）与年级自动识别
│   ├── ActiveChildStore.kt      # 当前孩子 ID（SharedPreferences）
│   ├── ParentPinStore.kt        # 家长 4 位密码（SharedPreferences）
│   ├── DefaultRewards.kt        # 默认奖励项
│   ├── QuestionCsv.kt           # 题库 CSV 解析 / 序列化
│   ├── AnchorCatalog.kt         # 锚点定义、前置关系、BKT 参数
│   ├── AnchorSeeder.kt          # 老用户补题、锚点回填、状态初始化
│   ├── BackupCodec.kt           # 用户数据 JSON 导出 / 恢复
│   ├── DiagnosticsExporter.kt    # 诊断日志导出（v0.12）
│   ├── entity/                  # 8 张表实体
│   └── dao/                     # 8 个 DAO
├── engine/
│   ├── ItemQualityAnalyzer.kt   # 用作答证据校准题目质量（v0.11）
│   ├── AdaptiveAssessmentEngine.kt  # 精炼测评组卷 + 掌握度增量
│   ├── AnswerJudge.kt               # 三态判分
│   ├── WrongQuestionLoop.kt         # 错题三级闭环
│   ├── AnchorLoopEngine.kt          # 按锚点×层级组「今天 3 题」
│   ├── AnchorBktEngine.kt           # BKT 掌握概率与三档状态
│   ├── SignalAnalyzer.kt            # 秒答 / 犹豫信号识别
│   ├── PointIncentiveEngine.kt      # 积分 / 连击 / 手工调整
│   └── GrowthCurveAnalyzer.kt       # 成长曲线与薄弱点
├── llm/
│   ├── LLMClient.kt             # 可选远程调用 + 离线降级
│   └── PromptTemplates.kt       # 报告/鼓励语模板
└── ui/
    ├── child/                   # 首页、答题、结果、总结、奖励商城
    ├── parent/                  # 报告、孩子管理、奖励管理、密码、题库管理、备份
    └── theme/                   # 配色与主题
```

## 三、数据模型

数据库版本 **v6**，8 张表，全部按 `user_id`（= 孩子 ID）隔离；题库再按 `stage`（幼儿园 / 小学低年级）分流。

| 表 | 关键字段 | 说明 |
|---|---|---|
| `question` | subject, knowledge_point, difficulty, type, content, options, answer, explanation, tags, enabled, source, updated_at, anchor_code, cognitive_level, variant_type, stage | 题库，v0.13 起内置 524 道锚点题（assets/questions_v2.csv 增量导入）；锚点 × 层级标注；按 stage 分流 |
| `anchor_state` | user_id, anchor_code, cognitive_level, probability, confidence, status, attempts, correct | BKT 掌握状态，锚点 × 层级唯一 |
| `learning_record` | user_id, question_id, is_correct, duration_ms, timestamp | 每次作答流水 |
| `wrong_answer` | user_id, question_id, wrong_count, master_level, last_wrong_answer | 错题本，master_level 0-100 |
| `knowledge_mastery` | user_id, knowledge_point, mastery, updated_at | 知识点掌握度 |
| `child_profile` | id, name, grade, avatar, total_points, stage | 多孩子档案；stage 决定使用哪套题库 |
| `point_log` | user_id, points, reason, timestamp | 积分流水（含家长调整） |
| `reward_item` | name, emoji, points, description, enabled, sort_order | 奖励商城配置 |

**迁移策略**：Room 链式迁移 v1→v2→…→v6（v6 将历史积分清零并将奖励定价折算到「颗星」），已移除 `fallbackToDestructiveMigration()`，迁移失败宁可报错也不静默清库；升级不清空历史数据。

## 四、Loop 闭环设计

```
        ┌──────────────────────────────────────────────┐
        │                                              │
        ▼                                              │
  ① 精炼测评（8 题）                                    │
        │   先错题 → 再薄弱知识点 → 补足难度              │
        ▼                                              │
  ② 三态判分 + 掌握度增量                               │
        │   正确 +10 / 接近 +3 / 未掌握 -15              │
        ▼                                              │
  ③ 错题入闭环                                        │
        │   master 0-39 重做 → 40-79 变形 → 80+ 延伸     │
        ▼                                              │
  ④ 积分激励（答错不扣）                                │
        │   基础 10 × 连击倍率 + 难度 + 突破奖励          │
        ▼                                              │
  ⑤ 家长报告 + 干预建议                                │
        │   薄弱点雷达 / 7 天正确率 / 4 周汇总            │
        └──────────────► 下一轮测评 ◄──────────────────┘
```

### 组卷规则（v0.13 起：AnchorLoopEngine 为主路径）

题库五层结构：学段 Stage → 领域 Domain → 主题 Topic → 锚点 Anchor → 题目 Item。

**第一步 选板块**：统计每个主题下是否所有锚点都已稳固；必修且未稳固的板块优先，已稳固的板块降权，避免连续多轮只在同一个点上打转。

**第二步 选锚点**：过滤前置锚点未达标的项；在最近发展区（掌握概率 0.55–0.85）内按「板块是否已稳固 → 掌握概率」排序，取最该补的一个锚点的一个认知层级（记忆 → 理解 → 应用，取第一个未稳固层）。

**第三步 选题**：
1. 先排除冷却期内做过的题（幼儿园 3 天、小学 7 天）；
2. 诊断题：同锚点同层级里最容易的一题；
3. 目标题：难度最接近 3 的一题；
4. 迁移题：优先换一种表征方式（文字 / 图示 / 情境 / 算式）；
5. 新鲜题不足 3 道时才放宽冷却限制兜底。

> 自由练习仍走 AdaptiveAssessmentEngine（错题 → 薄弱知识点 → 难度补齐），保证空池不死锁。

### 组卷规则（AdaptiveAssessmentEngine，自由练习）
1. 优先抽错题，最多 3 题；
2. 补充掌握度 < 60 的知识点题目；
3. 题目不足时放宽难度补齐；
4. 学科为空时使用全量题库兜底（防止空池死锁）。

### 判分规则（AnswerJudge）
- **选择题**：答案字母严格比对，不做包含匹配；
- **填空题**：去空格、全半角归一、去声调后严格相等；
- **应用题 / 开放题**：提取数值与关键词，数值一致即判定接近或正确，结果页展示「参考思路」而非标准答案。

### 积分规则（PointIncentiveEngine，单位：颗星）
| 场景 | 颗星 |
|---|---|
| 答对基础 | 1 |
| 难题（difficulty ≥ 3） | +1 |
| 连对第 3 题 | +1（一轮一次） |
| 掌握度突破 60 | +3 |
| 单周 10 个知识点达标 | +5 |
| 答错 | 0（不扣星，仅清零连对） |
| 家长手工调整 | ±1~10，需填原因，扣减不为负 |

奖励默认定价 5–30 颗，孩子端统一显示「⭐ N 颗」。

## 五、低成本 LLM 策略

- 做题、判分、组卷、积分**全部本地**，零 token 消耗；
- 仅在「诊断报告 / 干预方案 / 鼓励语」调用 LLM；
- 无网络或未配置 endpoint 时，自动降级为本地规则模板；
- 目标：单次报告成本 ≈ ¥0.3，MVP 月成本 < ¥500。

## 六、导航与安全

- 单 Activity + `sealed class Screen` 状态机，无导航框架依赖；
- `BackHandler` 统一接管系统返回键，所有子页面提供顶部返回栏；
- 家长区统一闸门：`openParentArea(target)` → 未验证先进入密码页；
- 密码验证状态仅存于内存，**回首页即失效**，不复用。

## 七、构建与安装

```bash
export JAVA_HOME=/path/to/jdk17
export ANDROID_HOME=/path/to/android-sdk
cd android-app
gradle :app:assembleDebug
# 产物：app/build/outputs/apk/debug/app-debug.apk
```

| 配置项 | 值 |
|---|---|
| applicationId | `com.learningloop.app` |
| versionCode / versionName | 19 / 0.17.1 |
| minSdk / targetSdk | 24 / 34 |
| 签名 | release 签名（`keystore.properties` + jks，均不入库）；debug 自动签名 |

安装：平板开启「允许安装未知来源」→ 点击 APK 安装。覆盖安装保留数据。

## 八、后续技术债

1. 语音输入占位，需接入 `SpeechRecognizer` + 声纹校验；
2. LLM endpoint 与密钥需外置配置，当前硬编码占位；
3. 家长密码应改为加密存储或生物识别；
4. 删除孩子应级联清理关联数据；
5. `MainActivity` 承担状态机与 ViewModel 双重职责，规模扩大后应拆分。
