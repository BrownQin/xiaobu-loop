# 小步 · LoopLearning

「小步」智能伴学：面向学龄前与小学二年级的端侧自适应学习 Loop 引擎与认知层级评价 Agent。用最少的题找到最该攻的那一个点，说清孩子学到了哪一层。

Kotlin + Jetpack Compose + Room，**完全离线**，数据不出端。

## 仓库结构

| 路径 | 说明 |
|---|---|
| `android-app/` | Android 应用源码（单 Activity + Compose + Room，无服务端） |
| `project-docs/` | 项目文档：总览、架构、变更日志、题库结构、AI 演进、诊断方法 |
| `content-packs/` | 题库内容包：生成规范、分包 CSV、校验合并脚本、合并产物 |
| `lean-canvas/` | 商业模式画布 |
| `题库CSV模板.csv` | 家长 / 教研可直接编辑的题库导入模板 |

## 题库结构（v0.13.0）

五层模型：**学段 → 领域 → 主题 → 锚点 → 题目**

- 18 个知识板块（幼儿园 8 个、二年级 10 个）
- 55 个可观察锚点，每个锚点在记忆 / 理解 / 应用三层独立追踪
- 524 道带标签的锚点题，随 APK 内置 `android-app/app/src/main/assets/questions_v2.csv`

出题策略：先选板块（必修未稳固优先）→ 再选锚点（前置达标 + 最近发展区）→ 再组卷（冷却期过滤 + 诊断/目标/迁移三档）。

详见 [project-docs/QUESTION-BANK.md](project-docs/QUESTION-BANK.md)。

## 文档索引

- [项目总览与版本记录](project-docs/README.md)
- [技术架构](project-docs/ARCHITECTURE.md)
- [变更日志](project-docs/CHANGELOG.md)
- [题库结构模型与出题策略](project-docs/QUESTION-BANK.md)
- [AI 智能测评演进方案](project-docs/AI-EVOLUTION.md)
- [诊断日志：导出、读取与分析方法](project-docs/DIAGNOSTICS.md)

## 构建

```bash
cd android-app
gradle :app:assembleDebug
# 产物：app/build/outputs/apk/debug/app-debug.apk
```

需要 JDK 17、Android SDK 34。构建说明见 [project-docs/ARCHITECTURE.md](project-docs/ARCHITECTURE.md)。

## 发布

- 安装包：飞书云空间 `智能伴学-LoopLearning/dist/`，同时归档到 GitHub Releases
- 版本号规则与发版动作见 [project-docs/CHANGELOG.md](project-docs/CHANGELOG.md)

当前版本：**v0.13.0**（2026-11-14）
