# 诊断日志：导出、读取与分析方法

**适用版本**：v0.12.0 及以上
**用途**：把线上问题变成可分析的数据，避免靠截图和口头描述传话。

---

## 一、怎么导出

1. 孩子首页 → 右上角「⚙ 管理」→ 输入家长密码
2. 管理 → 题库与数据 → **导出诊断日志**
3. 系统弹出保存框，文件名形如 `伴学诊断-0.12.0-20261113-1830.json`，保存到「下载」即可
4. 用微信 / 飞书把文件发回给开发者

导出只读数据库，不修改任何用户数据；不含照片、录音、通讯录。

---

## 二、文件结构

```json
{
  "app": "LearningLoop",
  "schema": 1,
  "versionName": "0.12.0",
  "versionCode": 12,
  "exportedAt": 1763000000000,
  "exportedAtText": "2026-11-13 18:30:00",
  "device": { "brand": "...", "model": "...", "androidRelease": "13", "sdk": 33 },
  "counts": { "questions": 287, "records": 96, "children": 2, "wrongAnswers": 7 },
  "children": [ ... ],
  "records": [ ... ],
  "itemQuality": [ ... ]
}
```

| 顶层字段 | 含义 | 分析用途 |
| --- | --- | --- |
| `versionName` / `versionCode` | App 版本 | 判断问题是否已被修复 |
| `device` | 品牌、机型、Android 版本、SDK | 定位 TTS、布局、兼容性问题 |
| `counts` | 题量、记录数、孩子数、错题数 | 判断样本是否足够 |
| `children` | 每个孩子的档案与锚点状态 | 看个体差异，不看平均值 |
| `records` | 最近 3000 条作答证据 | 主力数据 |
| `itemQuality` | 只列「有问题」的题目 | 题库校准 |

### 2.1 records 字段

| 字段 | 含义 | 怎么用 |
| --- | --- | --- |
| `ts` | 毫秒时间戳 | 看每天做几题、是否断档 |
| `userId` | 孩子 ID | 按孩子分组，不比跨孩子平均 |
| `questionId` / `content` | 题目与题干前 40 字 | 定位具体题 |
| `anchorCode` / `level` | 锚点与认知层级 | 判断难点落在哪一层 |
| `correct` | 是否判对 | 正确率 |
| `durationMs` | 耗时 | 与自身基线比，不看绝对值 |
| `signal` | NORMAL / FAST_SUSPECT / HESITANT | 判断「对」可信不可信 |
| `optionSwitches` | 切换选项次数 | ≥2 视为不稳定 |
| `answerChanges` | 改答次数 | ≥2 视为掌握不稳 |

### 2.2 children[].anchorStates 字段

`anchorCode`、`level`、`status`（UNTOUCHED / IN_PROGRESS / SOLID）、`probability`、`confidence`（HIGH / LOW）、`attempts`、`correct`、`updatedAt`。

### 2.3 itemQuality 字段

`issue` 取值：`TOO_EASY`（正确率 ≥95%）、`TOO_HARD`（≤25%）、`FAST_GUESS`（秒答过半）。均要求样本 ≥3。

---

## 三、分析步骤（固定顺序）

**第 1 步：先看数据够不够**
`records` 少于 15 条、或单锚点样本少于 3 条时，不下结论，只记录现象。

**第 2 步：看完成率与节奏**
按 `ts` 分天统计题量。一天 3 题是设计目标；若多数天不足 3 题，先解决体验问题，别急着改算法。

**第 3 步：看正确率分布**
按 `anchorCode × level` 分组算正确率。关注「记忆层高、理解层骤降」的锚点——那是断层，不是粗心。

**第 4 步：看信号质量**
统计 `signal` 分布与 `optionSwitches ≥ 2` 的比例。若「答对但切换多」占比高，说明存在侥幸正确，掌握度被高估。

**第 5 步：看题目质量**
`itemQuality` 里 `TOO_EASY` 的题，通常是出题时低估了孩子；`TOO_HARD` 的题要先核对答案是否标错，再怀疑难度。

**第 6 步：看断层一句话**
对照 `anchorStates`，找出「记忆 SOLID、理解 IN_PROGRESS 且 attempts ≥3」的锚点，这就是下一版该做「讲一遍」的地方。

---

## 四、判读阈值

| 现象 | 阈值 | 结论 |
| --- | --- | --- |
| 题目偏简单 | 正确率 ≥95%，n≥3 | 上调层级或降频 |
| 题目偏难 / 标错 | 正确率 ≤25%，n≥3 | 先核对题干与答案 |
| 疑似背答案 | 秒答占比 ≥50% | 换问法复验，不扣星 |
| 侥幸正确 | 答对且切换 ≥2 次 | 置信度降为 LOW |
| 明显断层 | 记忆层 ≥0.85、理解层 ≤0.5 | 优先补理解层 |
| 挫败信号 | 连续 2 次退出或超时 | 降题量、下沉一层 |

---

## 五、常见问题 → 从日志看什么

| 用户描述 | 先看 | 判断依据 |
| --- | --- | --- |
| 「朗读没声音 / 调不了」 | `device.model`、`androidRelease` | 多为设备无中文离线语音包，不是代码问题 |
| 「点了按钮没反应」 | `device` + 版本号 | 确认是否已升级到修复版本 |
| 「题目太难」 | `itemQuality.TOO_HARD` | 看是否集中在某锚点某层 |
| 「孩子答对了但不升级」 | `records` 的 `signal` 与 `optionSwitches` | 秒答或多次切换会压低置信度，属于设计行为 |
| 「两个孩子的数据串了」 | `records[].userId` 与 `children[].id` | 检查是否同一 userId 下混入不同 stage 的题 |

---

## 六、分析结论的写法

统一成三句话，便于对比不同周次：

```
现象：某锚点理解层 4 次作答全错，其中 3 次耗时低于 8 秒。
证据：records 中 math-sub-borrow/UNDERSTANDING，signal 均为 FAST_SUSPECT。
动作：先换问法复验；若仍错，进入 v0.12 的「讲一遍」流程。
```

---

## 七、隐私与保留

- 日志含孩子昵称（家长自填）与作答内容，不含真实姓名、学校、位置。
- 导出的文件由家长自己保存与发送，App 不上传任何数据。
- 建议每次试用结束导出一次，本地保留最近两版即可；旧文件可删。
