# 智能伴学 · 二年级语文数学 伴学 MVP

一个完全离线运行的 Android Kotlin 应用骨架，采用 **Kotlin + Jetpack Compose + Material3 + Room + Lifecycle ViewModel**。

## 一、设计理念

二年级孩子的有效专注窗口只有 10~15 分钟，题海战术反而是反教育的。本 MVP 围绕"**极度精炼测评 + 错题闭环 + 积分激励**"三件事，让每一次答题都产生实际的掌握度增量：

| 模块 | 做了什么 |
|---|---|
| 自适应测评引擎 | 每次仅出 **5~10 题**，优先抽取薄弱知识点 + 错题本，避免题海 |
| 错题闭环 | 答错自动进入 **重做 → 变形 → 延伸** 三级循环 |
| 积分激励 | 答对 10 分 + 连击倍率 (×1.5/×2/×3) + 进步奖励 (+500 分) |
| 成长曲线 | 按日/周输出掌握度变化，给家长薄弱点雷达图 |
| LLM | **做题全本地**；只在生成诊断报告、干预方案、鼓励语时才调用，并内置离线降级 |

## 二、技术栈

- **语言**: Kotlin 1.9.22
- **UI**: Jetpack Compose BOM 2024.02.01 + Material3
- **架构**: Lifecycle ViewModel + StateFlow
- **持久化**: Room 2.6.1 (6 张表全部本地，零服务器依赖)
- **网络**: OkHttp（仅 LLM 调用；无网络时走本地规则）
- **最低 SDK**: 24 / 编译 SDK: 34 / 目标 SDK: 34

## 三、项目结构

```
android-app/
├── settings.gradle.kts
├── build.gradle.kts                # 根 Gradle，声明 AGP + Kotlin + KSP
├── gradle.properties
├── gradle/wrapper/gradle-wrapper.properties
├── app/
│   ├── build.gradle.kts            # app 模块依赖集中地
│   ├── src/main/
│   │   ├── AndroidManifest.xml     # 声明 INTERNET / RECORD_AUDIO 权限
│   │   ├── java/com/learningloop/app/
│   │   │   ├── MainActivity.kt     # 入口 + 页面状态机 + ViewModel
│   │   │   ├── SeedData.kt         # 30 道二年级语文数学种子题
│   │   │   ├── data/
│   │   │   │   ├── AppDatabase.kt
│   │   │   │   ├── entity/         # Question / WrongAnswer / LearningRecord
│   │   │   │   │                   # ChildProfile / PointLog / KnowledgeMastery
│   │   │   │   └── dao/            # 以上 6 张表各一个 DAO
│   │   │   ├── engine/
│   │   │   │   ├── AdaptiveAssessmentEngine.kt   # ⭐ 极度精炼测评
│   │   │   │   ├── WrongQuestionLoop.kt          # 错题三级闭环
│   │   │   │   ├── PointIncentiveEngine.kt       # 积分体系
│   │   │   │   └── GrowthCurveAnalyzer.kt         # 成长曲线 / 薄弱点雷达
│   │   │   ├── ui/
│   │   │   │   ├── child/          # 孩子端 4 屏
│   │   │   │   ├── parent/         # 家长端 2 屏
│   │   │   │   └── theme/Theme.kt  # Material3 亮/暗双主题
│   │   │   └── llm/
│   │   │       ├── LLMClient.kt    # 离线降级 + 低成本策略
│   │   │       └── PromptTemplates.kt
│   │   └── res/values/strings.xml
```

## 四、关键约束已实现

| 约束 | 实现位置 |
|---|---|
| 数据全部本地 Room | `data/AppDatabase.kt` |
| AdaptiveAssessmentEngine "极度精炼" 注释 | `engine/AdaptiveAssessmentEngine.kt` 文件头注释 |
| LLM 离线降级 | `llm/LLMClient.kt` 中 `fallbackGentleFeedback / fallbackAssessmentReport` |
| 语音输入占位 | `ui/child/QuizScreen.kt` 中 `// TODO:` 注释 + Manifest RECORD_AUDIO 权限 |
| 积分公式：base10 + 连击倍率 + 进步奖励 | `engine/PointIncentiveEngine.kt` |
| 题库 ≥30 题 (语文 15 + 数学 15) | `SeedData.kt` |

## 五、MVP 功能清单

### 孩子端
- 首页：问候、当前积分、学科选择、错题本/奖励商城入口
- 做题：计时 (60s/题)、选择题/填空/应用题三种题型、语音输入按钮占位、自动提交
- 结果：答对庆祝动画 + 积分变动；答错温和提示 + 解析
- 奖励商城：8 种预设奖品，积分实时校验

### 家长端
- 仪表盘：总体掌握度 / 薄弱点雷达 (Top 6) / 近 7 天正确率 / 近 4 周汇总 / 干预建议
- 孩子画像编辑：姓名、年级、emoji 头像

## 六、编译方式

1. 用 Android Studio (Hedgehog+) 打开 `android-app/` 目录；
2. 同步 Gradle（第一次会下载依赖）；
3. 连接一台 Android 设备 (API 24+)；
4. Run `app`。

或命令行：

```bash
cd android-app
./gradlew :app:assembleDebug
# 输出: app/build/outputs/apk/debug/app-debug.apk
```

## 七、已知 MVP 简化点（上线前要补上）

- **导航**: 用 `sealed class Screen` + 条件渲染，正式版应切换到 Navigation Compose；
- **网络 LLM**: `LLMClient` 的 `endpoint` / `apiKey` 目前是 null，接入真实服务时在 `MainActivity.initDependencies` 里传入即可；
- **语音输入**: Manifest 已声明权限，UI 上有占位按钮，接 `SpeechRecognizer` 或 Whisper on-device 即可；
- **多孩子**: 目前 `userId` 固定为 1L，所有表都带了 userId 字段，扩多用户只需加一个选择器；
- **题库丰富**: SeedData 30 题是启动基础，正式版应配套一个"同步题库"按钮从远端 JSON 拉取。
