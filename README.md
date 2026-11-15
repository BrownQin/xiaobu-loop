# 小步 · LoopLearning

**开源的端侧自适应学习引擎与 Android 伴学 App**：面向幼小衔接与小学低年级（约 4–8 岁），用最少的题找到最该练的那一个能力点，并说清孩子学到了哪一层。

[![Android](https://img.shields.io/badge/Android-7.0%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Offline](https://img.shields.io/badge/Offline--first-100%25-success)]()
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

> 传统刷题 App 在「知识点平铺 + 随机抽题」上打转：题做了不少，家长却说不清孩子到底掌握了什么。
> 小步把题库拆成 **可观察的锚点**，按 **最近发展区** 选题，用 **记忆 / 理解 / 应用** 三层认知状态给家长一份能看懂的能力地图。

小步不预设孩子的成绩起点，也不把「补差」当作唯一场景：刚起步、稳步巩固、学有余力想拓展，都按当前掌握水平推送。当前内容覆盖语文与数学基础能力，题库的 **领域 → 主题 → 锚点** 结构可直接扩展到常识、科学素养、儿童心理成长等通识板块。

## 开源仓库与下载

- **GitHub**：[BrownQin/xiaobu-loop](https://github.com/BrownQin/xiaobu-loop)
- **Gitee**：[nmd568/xiaobu-loop](https://gitee.com/nmd568/xiaobu-loop)
- **当前版本**：v0.17.1（versionCode 19）
- **APK 下载**：[GitHub Release（公开下载）](https://github.com/BrownQin/xiaobu-loop/releases/download/v0.17.1/app-release.apk) · [飞书文件](https://my.feishu.cn/file/L9zqbIXnLosFQyxVYeJcOR626Xb)

外部下载建议优先使用 GitHub Release；飞书文件的访问范围取决于组织与分享权限。

## 面向的孩子

| 阶段 | 使用方式 |
|---|---|
| 幼小衔接（约 4–6 岁） | 从数感、拼音、听说与生活常识起步，先建立坐得住、敢作答的习惯 |
| 小学低年级（1–2 年级） | 按当前掌握水平推送语文、数学锚点，不按考试分数分层 |
| 学有余力 | 自动上探更高年级锚点，或做跨情境的迁移练习 |

## 核心能力

- **五层题库结构**：学段 → 领域 → 主题 → 锚点 → 题目。18 个知识板块、61 个可观察锚点、584 道带标签题目。
- **认知层级追踪**：每个锚点在记忆（MEMORY）/ 理解（UNDERSTANDING）/ 应用（APPLICATION）三层独立建模，简化 BKT 更新掌握概率。
- **最近发展区出题**：只推掌握概率 0.55–0.85 的锚点，先选板块、再选锚点、最后组卷，避免多轮重复。
- **冷却期机制**：幼儿园 3 天、小学 7 天内同一道题不再出现；同锚点新鲜题不足时才放宽。
- **证据链**：记录改答次数、选项切换、作答耗时，识别「秒答猜对」「犹豫掌握」，并输出题目质量诊断。
- **家长能力地图**：主题完成度 → 锚点三层状态，只给「已稳固 / 攻坚中 / 未触发」，不暴露模型小数。
- **完全离线**：Kotlin + Jetpack Compose + Room，单 Activity，无服务端，孩子数据不出端；支持多孩子隔离、家长 PIN、备份恢复、CSV 题库导入导出。

## 为什么值得看

| 常见做法 | 小步的做法 |
|---|---|
| 按知识点刷题 | 按可观察锚点 + 认知层级推送 |
| 随机 / 顺序出题 | 板块优先 + 最近发展区 + 冷却期 + 三档组卷 |
| 正确率一个数字 | 记忆 / 理解 / 应用三层状态与证据 |
| 云端账号体系 | 端侧离线，本地数据库，家长可控 |
| 题库写死在代码里 | CSV 内容包 + 校验脚本，可批量加工与增量导入 |

## 技术栈

- **语言 / UI**：Kotlin、Jetpack Compose、Material 3
- **数据**：Room（SQLite），迁移链 v1 → v7，无破坏性迁移
- **架构**：单 Activity + Screen 状态机 + 手动 ViewModel，零第三方依赖
- **内容管线**：CSV（RFC4180 简化）→ 校验脚本 → assets 内容包 → 启动时按题干去重增量导入
- **测评模型**：简化 BKT（init / guess / slip / learn）、ZPD 选题、信号质量分析、题目质量诊断

## 快速开始

```bash
# 环境：JDK 17、Android SDK 34
cd android-app
gradle :app:assembleDebug
# 产物：app/build/outputs/apk/debug/app-debug.apk
```

安装到平板即可离线使用。首次启动会自动建库并导入内置题库。

跑回归测试（含「题目质量」页面渲染）：

```bash
cd android-app
gradle :app:testDebugUnitTest
```

## 项目结构

```
android-app/        Android 应用源码
  app/src/main/java/com/learningloop/app/
    data/           实体、DAO、迁移、锚点目录、题库内容包加载
    engine/         BKT、ZPD 组卷、信号分析、题目质量诊断
    ui/             Compose 页面（孩子端 / 家长端）
  app/src/main/assets/questions_v2.csv   内置题库内容包
project-docs/       架构、变更日志、题库模型、AI 演进、诊断方法
content-packs/      题库分包、生成规范、校验合并脚本
lean-canvas/        商业模式画布
```

## 文档

- [项目总览与版本记录](project-docs/README.md)
- [技术架构](project-docs/ARCHITECTURE.md)
- [题库结构模型与出题策略](project-docs/QUESTION-BANK.md)
- [变更日志](project-docs/CHANGELOG.md)
- [AI 智能测评演进方案](project-docs/AI-EVOLUTION.md)
- [诊断日志：导出、读取与分析方法](project-docs/DIAGNOSTICS.md)
- [飞书项目管理与版本规划](https://my.feishu.cn/docx/UviHdb60Ho4Rz7xUBFAcjisi6Pg)

## 路线图

- [x] 锚点认知测评 + 正循环 Loop
- [x] 五层题库结构 + 584 题内容包
- [x] 冷却期与三档组卷
- [x] 家长能力地图
- [ ] 题目质量看板自动降权 / 停用
- [ ] 大模型归因与家长话术（不进入判分链路）
- [ ] 更多学段、学科与通识板块内容包（常识 / 科学素养 / 儿童心理成长）

## 适合谁

- 想做 **端侧自适应学习** 的 Android / Kotlin 开发者
- 研究 **知识追踪、认知诊断、最近发展区** 的教育技术同学
- 需要 **离线、数据可控** 的幼小衔接 / 小学低年级家长、托管班与小型教研团队

## License

[MIT](LICENSE)
