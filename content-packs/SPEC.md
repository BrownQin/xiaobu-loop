# 题库内容包生成规范（给内容生成使用）

目标：为小步 App 生成可直接导入的题目 CSV 片段。每个文件只包含题目行，不包含任何说明文字。

## 文件格式

第一行必须是表头，严格按以下 15 列，顺序不能变：

```
id,subject,knowledgePoint,difficulty,type,content,options,answer,explanation,tags,anchorCode,cognitiveLevel,variantType,enabled,stage
```

- `id`：一律留空
- `subject`：`math` 或 `chinese`
- `knowledgePoint`：中文短语，如 `拼音-声母`、`数学-退位减法`
- `difficulty`：1-5 的整数。记忆层用 1-2，理解层用 2-3，应用层用 3-4
- `type`：`choice`（选择）/ `fill`（填空）/ `word`（应用题/开放题）
- `content`：题干。全库必须唯一；不要出现「同上」「略」
- `options`：选择题必填，格式 `A.选项一|B.选项二|C.选项三`（3 个选项，少数可用 4 个）；非选择题留空
- `answer`：选择题填字母 `A`/`B`/`C`；填空题填答案文本；应用题填参考答案要点
- `explanation`：一句孩子能听懂的话，不超过 40 字。不要写「见课本」
- `tags`：逗号分隔的中文标签，但**逗号会破坏 CSV**，请用顿号 `、`，如 `形近字、易错`
- `anchorCode`：必须使用下方给定的锚点编码，不得自造
- `cognitiveLevel`：`MEMORY` / `UNDERSTANDING` / `APPLICATION`
- `variantType`：`TEXT`（纯文字）/ `DIAGRAM`（用文字描述图，如「图中有 3 个苹果」）/ `CONTEXT`（生活情境）/ `EXPRESSION`（算式）
- `enabled`：`1`
- `stage`：`KG` 或 `PRIMARY_LOW`，必须与锚点所属学段一致

## 内容要求

1. 面向 4-7 岁孩子：句子短、用词生活化、不出现生僻字（幼儿园题目尽量用一、二年级常用字）
2. 幼儿园题目以图景描述代替真图，例如「盘子里有 3 个苹果，又放进去 2 个」
3. 选择题的错误选项必须是**典型错误**，不能随便凑数
4. 同一锚点下的题目要覆盖不同表征（TEXT / DIAGRAM / CONTEXT / EXPRESSION），不要全是同一种
5. 每个锚点的三个认知层级都要有题：
   - MEMORY：直接认读、直接计算、直接回忆
   - UNDERSTANDING：说理、比较、判断对错、解释为什么
   - APPLICATION：放进生活情境、换一种问法、两步小问题
6. 严禁重复题干；同一道题不要换数字反复出
7. 不要出现教材原文段落，不要抄教辅

## 锚点编码与学段

- 幼儿园（KG）：kg- 开头
- 二年级（PRIMARY_LOW）：math- / g2- / chinese- 开头

具体可用编码见各任务说明。写完后自查：行数正确、每行列数都是 15、没有未转义的英文逗号出现在字段内部（题干里需要停顿用中文逗号 `，`）。

## 交付

只输出一个 CSV 文件到指定路径，不加代码块、不加解释。完成后回复一句话：文件名 + 行数。
