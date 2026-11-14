package com.learningloop.app.data

import com.learningloop.app.data.entity.Question

/** BKT 四参数 */
data class BktParams(
    val init: Float,
    val guess: Float,
    val slip: Float,
    val learn: Float
)

/** 知识板块：家长能力地图的第一层，例如「拼音启蒙」「100 以内加减」 */
data class TopicDef(
    val code: String,
    val name: String,
    val stage: String,
    /** 领域：数与代数 / 图形与几何 / 语言与阅读 */
    val domain: String,
    val order: Int
)

/**
 * 锚点定义：一个可观察、可测评、可干预的最小能力单元。
 *
 * observable 描述「看到什么算过」，这是锚点区别于普通知识点的关键——
 * 它必须有可观察行为，而不是一个抽象标签。
 *
 * required=true 表示课标要求本学段必须掌握，家长地图会单独统计完成度。
 */
data class AnchorDef(
    val code: String,
    val subject: String,
    val name: String,
    /** RECOGNIZE 认读 / REASONING 算理 / MEANING 意义理解 */
    val type: String,
    /** 所属知识板块 */
    val topic: String,
    /** 板块内推进顺序 1..n */
    val stage: Int,
    val prerequisites: List<String>,
    val observable: String,
    val bkt: BktParams,
    /** 所属学段：KG 幼儿园 / PRIMARY_LOW 小学低年级 */
    val stageCode: String = Stage.PRIMARY_LOW,
    /** 是否为课标必修能力 */
    val required: Boolean = true
)

object AnchorCatalog {

    const val TYPE_RECOGNIZE = "RECOGNIZE"
    const val TYPE_REASONING = "REASONING"
    const val TYPE_MEANING = "MEANING"

    // ---------- 知识板块 ----------

    val topics: List<TopicDef> = listOf(
        // 幼儿园（中班 / 大班）
        TopicDef("kg-num", "数感与数量", Stage.KG, "数与代数", 1),
        TopicDef("kg-addsub", "10 以内加减", Stage.KG, "数与代数", 2),
        TopicDef("kg-shape", "图形与空间", Stage.KG, "图形与几何", 3),
        TopicDef("kg-measure", "量的比较", Stage.KG, "图形与几何", 4),
        TopicDef("kg-pinyin", "拼音启蒙", Stage.KG, "语言与阅读", 5),
        TopicDef("kg-literacy", "识字启蒙", Stage.KG, "语言与阅读", 6),
        TopicDef("kg-language", "倾听与表达", Stage.KG, "语言与阅读", 7),
        TopicDef("kg-life", "生活中的数学", Stage.KG, "数与代数", 8),
        // 小学二年级
        TopicDef("g2-addsub", "100 以内加减", Stage.PRIMARY_LOW, "数与代数", 1),
        TopicDef("g2-mul", "表内乘法", Stage.PRIMARY_LOW, "数与代数", 2),
        TopicDef("g2-div", "表内除法", Stage.PRIMARY_LOW, "数与代数", 3),
        TopicDef("g2-num", "万以内数的认识", Stage.PRIMARY_LOW, "数与代数", 4),
        TopicDef("g2-measure", "量与计量", Stage.PRIMARY_LOW, "数与代数", 5),
        TopicDef("g2-geometry", "图形与几何", Stage.PRIMARY_LOW, "图形与几何", 6),
        TopicDef("g2-stat", "数据与规律", Stage.PRIMARY_LOW, "数与代数", 7),
        TopicDef("g2-pinyin", "拼音与语音", Stage.PRIMARY_LOW, "语言与阅读", 8),
        TopicDef("g2-literacy", "识字与写字", Stage.PRIMARY_LOW, "语言与阅读", 9),
        TopicDef("g2-reading", "阅读与表达", Stage.PRIMARY_LOW, "语言与阅读", 10)
    )

    // ---------- BKT 默认参数（按锚点类型） ----------

    private fun recognize() = BktParams(init = 0.30f, guess = 0.15f, slip = 0.05f, learn = 0.12f)
    private fun reasoning() = BktParams(init = 0.20f, guess = 0.10f, slip = 0.15f, learn = 0.18f)
    private fun meaning() = BktParams(init = 0.20f, guess = 0.15f, slip = 0.12f, learn = 0.15f)

    // ---------- 锚点 ----------

    val all: List<AnchorDef> = listOf(
        // ===== 幼儿园：数感与数量 =====
        AnchorDef(
            code = "kg-count-10", subject = Question.SUBJECT_MATH, name = "10 以内数与量",
            type = TYPE_RECOGNIZE, topic = "kg-num", stage = 1, prerequisites = emptyList(),
            observable = "不用手指，能目测 5 以内数量；能手口一致点数到 10",
            bkt = recognize(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-num-compare", subject = Question.SUBJECT_MATH, name = "比多少与大小",
            type = TYPE_RECOGNIZE, topic = "kg-num", stage = 2, prerequisites = listOf("kg-count-10"),
            observable = "能比较两组物品多少，并说出「多 / 少 / 一样多」",
            bkt = recognize(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-num-compose", subject = Question.SUBJECT_MATH, name = "10 以内数的分与合",
            type = TYPE_REASONING, topic = "kg-num", stage = 3, prerequisites = listOf("kg-count-10"),
            observable = "能说出 5 可以分成 2 和 3，并能反过来合起来",
            bkt = reasoning(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-classify-compare", subject = Question.SUBJECT_MATH, name = "分类与排序",
            type = TYPE_REASONING, topic = "kg-num", stage = 4, prerequisites = listOf("kg-num-compare"),
            observable = "能按颜色或形状分类，并把 3 个物体按大小排序",
            bkt = reasoning(), stageCode = Stage.KG
        ),

        // ===== 幼儿园：10 以内加减 =====
        AnchorDef(
            code = "kg-add-10", subject = Question.SUBJECT_MATH, name = "合起来（加法意义）",
            type = TYPE_MEANING, topic = "kg-addsub", stage = 1, prerequisites = listOf("kg-count-10"),
            observable = "能把「又来了 2 个」说成合起来，并列出加法算式",
            bkt = meaning(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-sub-10", subject = Question.SUBJECT_MATH, name = "剩下（减法意义）",
            type = TYPE_MEANING, topic = "kg-addsub", stage = 2, prerequisites = listOf("kg-add-10"),
            observable = "能把「吃掉了 2 个」说成剩下，并列出减法算式",
            bkt = meaning(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-add-sub-10", subject = Question.SUBJECT_MATH, name = "10 以内加减计算",
            type = TYPE_REASONING, topic = "kg-addsub", stage = 3,
            prerequisites = listOf("kg-add-10", "kg-sub-10"),
            observable = "能看图列式并算对 3 道 10 以内加减，能说出「合起来」「剩下」",
            bkt = reasoning(), stageCode = Stage.KG
        ),

        // ===== 幼儿园：图形与空间 =====
        AnchorDef(
            code = "kg-shape-space", subject = Question.SUBJECT_MATH, name = "认识图形",
            type = TYPE_RECOGNIZE, topic = "kg-shape", stage = 1, prerequisites = emptyList(),
            observable = "能说出圆形、正方形、三角形，并从生活中指认",
            bkt = recognize(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-position", subject = Question.SUBJECT_MATH, name = "方位与位置",
            type = TYPE_RECOGNIZE, topic = "kg-shape", stage = 2, prerequisites = listOf("kg-shape-space"),
            observable = "能说清「上下、前后、里外、旁边」",
            bkt = recognize(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-pattern", subject = Question.SUBJECT_MATH, name = "规律排序",
            type = TYPE_REASONING, topic = "kg-shape", stage = 3, prerequisites = listOf("kg-shape-space"),
            observable = "能接着排完 ABAB、AABB 的图形序列",
            bkt = reasoning(), stageCode = Stage.KG
        ),

        // ===== 幼儿园：量的比较 =====
        AnchorDef(
            code = "kg-size", subject = Question.SUBJECT_MATH, name = "大小长短高矮",
            type = TYPE_RECOGNIZE, topic = "kg-measure", stage = 1, prerequisites = emptyList(),
            observable = "能直接比较两个物体的大小、长短、高矮并说出理由",
            bkt = recognize(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-mass", subject = Question.SUBJECT_MATH, name = "轻重与容量",
            type = TYPE_REASONING, topic = "kg-measure", stage = 2, prerequisites = listOf("kg-size"),
            observable = "能用手掂或看容器判断轻重、多少，并说出依据",
            bkt = reasoning(), stageCode = Stage.KG
        ),

        // ===== 幼儿园：拼音启蒙 =====
        AnchorDef(
            code = "kg-pinyin-entry", subject = Question.SUBJECT_CHINESE, name = "拼音入门",
            type = TYPE_RECOGNIZE, topic = "kg-pinyin", stage = 1, prerequisites = emptyList(),
            observable = "能认读常见声母韵母，并给「妈、爸、大」这类熟字标出第一个音节",
            bkt = recognize(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-pinyin-initial", subject = Question.SUBJECT_CHINESE, name = "声母认读",
            type = TYPE_RECOGNIZE, topic = "kg-pinyin", stage = 2, prerequisites = listOf("kg-pinyin-entry"),
            observable = "能准确认读 b p m f d t n l 等常见声母，不与其他声母混淆",
            bkt = recognize(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-pinyin-final", subject = Question.SUBJECT_CHINESE, name = "韵母认读",
            type = TYPE_RECOGNIZE, topic = "kg-pinyin", stage = 3, prerequisites = listOf("kg-pinyin-entry"),
            observable = "能认读单韵母 a o e i u ü 与常见复韵母 ai ei ao ou",
            bkt = recognize(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-pinyin-tone", subject = Question.SUBJECT_CHINESE, name = "四声辨认",
            type = TYPE_RECOGNIZE, topic = "kg-pinyin", stage = 4,
            prerequisites = listOf("kg-pinyin-final"),
            observable = "能听出并标出 mā má mǎ mà 的声调",
            bkt = recognize(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-pinyin-blend", subject = Question.SUBJECT_CHINESE, name = "音节拼读",
            type = TYPE_REASONING, topic = "kg-pinyin", stage = 5,
            prerequisites = listOf("kg-pinyin-initial", "kg-pinyin-tone"),
            observable = "能把 b-a 拼成 bā，并读出带声调的音节",
            bkt = reasoning(), stageCode = Stage.KG
        ),

        // ===== 幼儿园：识字启蒙 =====
        AnchorDef(
            code = "kg-char-recognize", subject = Question.SUBJECT_CHINESE, name = "常见字认读",
            type = TYPE_RECOGNIZE, topic = "kg-literacy", stage = 1, prerequisites = emptyList(),
            observable = "能认读 300 个左右生活中常见汉字，看到能读出来",
            bkt = recognize(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-char-stroke", subject = Question.SUBJECT_CHINESE, name = "笔顺与结构",
            type = TYPE_REASONING, topic = "kg-literacy", stage = 2,
            prerequisites = listOf("kg-char-recognize"),
            observable = "能按正确笔顺书空「大、小、上、下」，说出先横后竖",
            bkt = reasoning(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-word-meaning", subject = Question.SUBJECT_CHINESE, name = "词义配对",
            type = TYPE_MEANING, topic = "kg-literacy", stage = 3,
            prerequisites = listOf("kg-char-recognize"),
            observable = "能把「苹果」和图片配对，说出「苹果是水果」",
            bkt = meaning(), stageCode = Stage.KG
        ),

        // ===== 幼儿园：倾听与表达 =====
        AnchorDef(
            code = "kg-listen-express", subject = Question.SUBJECT_CHINESE, name = "倾听与表达",
            type = TYPE_MEANING, topic = "kg-language", stage = 1, prerequisites = emptyList(),
            observable = "听完两步指令能按顺序完成，并用完整句说出看到的事",
            bkt = meaning(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-speak-sentence", subject = Question.SUBJECT_CHINESE, name = "完整句表达",
            type = TYPE_MEANING, topic = "kg-language", stage = 2,
            prerequisites = listOf("kg-listen-express"),
            observable = "能用「谁 + 在哪里 + 做什么」说完整一句话",
            bkt = meaning(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-story-picture", subject = Question.SUBJECT_CHINESE, name = "看图说话",
            type = TYPE_MEANING, topic = "kg-language", stage = 3,
            prerequisites = listOf("kg-speak-sentence"),
            observable = "能按顺序说出图里的 3 件事，不跳跃",
            bkt = meaning(), stageCode = Stage.KG
        ),

        // ===== 幼儿园：生活中的数学 =====
        AnchorDef(
            code = "kg-time-day", subject = Question.SUBJECT_MATH, name = "时间与顺序",
            type = TYPE_RECOGNIZE, topic = "kg-life", stage = 1, prerequisites = emptyList(),
            observable = "能说清早上、中午、晚上，并按先后排出一天的三件事",
            bkt = recognize(), stageCode = Stage.KG
        ),
        AnchorDef(
            code = "kg-money-awareness", subject = Question.SUBJECT_MATH, name = "认识钱币",
            type = TYPE_RECOGNIZE, topic = "kg-life", stage = 2, prerequisites = listOf("kg-count-10"),
            observable = "能认出 1 元、5 元、10 元纸币和 1 元硬币",
            bkt = recognize(), stageCode = Stage.KG
        ),

        // ===== 小学二年级：100 以内加减 =====
        AnchorDef(
            code = "math-add-carry", subject = Question.SUBJECT_MATH, name = "100 以内进位加",
            type = TYPE_REASONING, topic = "g2-addsub", stage = 1, prerequisites = emptyList(),
            observable = "能连续算对 3 道进位加，并说出「满十进一」",
            bkt = reasoning()
        ),
        AnchorDef(
            code = "math-sub-borrow", subject = Question.SUBJECT_MATH, name = "退位减法的算理",
            type = TYPE_REASONING, topic = "g2-addsub", stage = 2,
            prerequisites = listOf("math-add-carry"),
            observable = "能解释「退一当十」并完成 3 道退位减",
            bkt = reasoning()
        ),
        AnchorDef(
            code = "g2-addsub-mixed", subject = Question.SUBJECT_MATH, name = "连加连减与混合",
            type = TYPE_REASONING, topic = "g2-addsub", stage = 3,
            prerequisites = listOf("math-sub-borrow"),
            observable = "能按从左到右的顺序计算 34 + 18 - 25，不跳步",
            bkt = reasoning()
        ),
        AnchorDef(
            code = "g2-addsub-word", subject = Question.SUBJECT_MATH, name = "加减法应用题",
            type = TYPE_MEANING, topic = "g2-addsub", stage = 4,
            prerequisites = listOf("g2-addsub-mixed"),
            observable = "能根据「多多少 / 少多少」选择加法或减法并说明理由",
            bkt = meaning()
        ),

        // ===== 小学二年级：表内乘法 =====
        AnchorDef(
            code = "math-mul-meaning", subject = Question.SUBJECT_MATH, name = "乘法的意义",
            type = TYPE_MEANING, topic = "g2-mul", stage = 1, prerequisites = listOf("math-add-carry"),
            observable = "能把「3 个 4」写成 3×4，并画出点子图",
            bkt = meaning()
        ),
        AnchorDef(
            code = "math-mul-table", subject = Question.SUBJECT_MATH, name = "表内乘法口诀",
            type = TYPE_RECOGNIZE, topic = "g2-mul", stage = 2,
            prerequisites = listOf("math-mul-meaning"),
            observable = "口诀 3 秒内答对，连续 6 题不超时",
            bkt = recognize()
        ),
        AnchorDef(
            code = "g2-mul-word", subject = Question.SUBJECT_MATH, name = "乘法应用题",
            type = TYPE_MEANING, topic = "g2-mul", stage = 3,
            prerequisites = listOf("math-mul-table"),
            observable = "能判断「每份 4 个，5 份」用乘法，并列出算式",
            bkt = meaning()
        ),

        // ===== 小学二年级：表内除法 =====
        AnchorDef(
            code = "g2-div-meaning", subject = Question.SUBJECT_MATH, name = "除法的意义",
            type = TYPE_MEANING, topic = "g2-div", stage = 1,
            prerequisites = listOf("math-mul-meaning"),
            observable = "能把 12 个平均分成 3 份，说成 12÷3=4",
            bkt = meaning()
        ),
        AnchorDef(
            code = "g2-div-table", subject = Question.SUBJECT_MATH, name = "用口诀求商",
            type = TYPE_REASONING, topic = "g2-div", stage = 2,
            prerequisites = listOf("g2-div-meaning", "math-mul-table"),
            observable = "看到 42÷6 能想「六七四十二」，3 秒内说出商",
            bkt = reasoning()
        ),
        AnchorDef(
            code = "g2-div-remainder", subject = Question.SUBJECT_MATH, name = "有余数的除法",
            type = TYPE_REASONING, topic = "g2-div", stage = 3,
            prerequisites = listOf("g2-div-table"),
            observable = "能算出 17÷5=3……2，并说出余数必须比除数小",
            bkt = reasoning()
        ),

        // ===== 小学二年级：万以内数的认识 =====
        AnchorDef(
            code = "g2-num-readwrite", subject = Question.SUBJECT_MATH, name = "读写与组成",
            type = TYPE_RECOGNIZE, topic = "g2-num", stage = 1, prerequisites = emptyList(),
            observable = "能正确读写 3050，并说出由 3 个千和 5 个十组成",
            bkt = recognize()
        ),
        AnchorDef(
            code = "g2-num-compare", subject = Question.SUBJECT_MATH, name = "比较与近似",
            type = TYPE_REASONING, topic = "g2-num", stage = 2,
            prerequisites = listOf("g2-num-readwrite"),
            observable = "能比较 4980 与 5001 的大小，并说出近似到整百的数",
            bkt = reasoning()
        ),
        AnchorDef(
            code = "g2-num-placevalue", subject = Question.SUBJECT_MATH, name = "数位与计数单位",
            type = TYPE_REASONING, topic = "g2-num", stage = 3,
            prerequisites = listOf("g2-num-readwrite"),
            observable = "能说出相邻两个数位之间的进率是 10",
            bkt = reasoning()
        ),

        // ===== 小学二年级：量与计量 =====
        AnchorDef(
            code = "g2-length-unit", subject = Question.SUBJECT_MATH, name = "长度单位",
            type = TYPE_RECOGNIZE, topic = "g2-measure", stage = 1, prerequisites = emptyList(),
            observable = "能选对厘米或米，知道 1 米 = 100 厘米",
            bkt = recognize()
        ),
        AnchorDef(
            code = "g2-mass-unit", subject = Question.SUBJECT_MATH, name = "克与千克",
            type = TYPE_RECOGNIZE, topic = "g2-measure", stage = 2,
            prerequisites = listOf("g2-length-unit"),
            observable = "能判断一个苹果约 200 克，一袋米约 10 千克",
            bkt = recognize()
        ),
        AnchorDef(
            code = "g2-time-clock", subject = Question.SUBJECT_MATH, name = "认识时间",
            type = TYPE_REASONING, topic = "g2-measure", stage = 3,
            prerequisites = listOf("g2-length-unit"),
            observable = "能读出 8:45，并算出经过 20 分钟后的时间",
            bkt = reasoning()
        ),
        AnchorDef(
            code = "g2-money", subject = Question.SUBJECT_MATH, name = "元角分换算",
            type = TYPE_REASONING, topic = "g2-measure", stage = 4,
            prerequisites = listOf("g2-num-readwrite"),
            observable = "能换算 3 元 5 角 = 35 角，并完成简单找零",
            bkt = reasoning()
        ),

        // ===== 小学二年级：图形与几何 =====
        AnchorDef(
            code = "g2-shape-recognize", subject = Question.SUBJECT_MATH, name = "认识图形",
            type = TYPE_RECOGNIZE, topic = "g2-geometry", stage = 1, prerequisites = emptyList(),
            observable = "能说出长方形、正方形、平行四边形的边角特征",
            bkt = recognize()
        ),
        AnchorDef(
            code = "g2-angle", subject = Question.SUBJECT_MATH, name = "角的初步认识",
            type = TYPE_RECOGNIZE, topic = "g2-geometry", stage = 2,
            prerequisites = listOf("g2-shape-recognize"),
            observable = "能指出角的顶点和两条边，辨认直角、锐角、钝角",
            bkt = recognize()
        ),
        AnchorDef(
            code = "g2-symmetry-move", subject = Question.SUBJECT_MATH, name = "对称与平移旋转",
            type = TYPE_REASONING, topic = "g2-geometry", stage = 3,
            prerequisites = listOf("g2-shape-recognize"),
            observable = "能判断图形是否轴对称，并说出平移后形状不变",
            bkt = reasoning()
        ),

        // ===== 小学二年级：数据与规律 =====
        AnchorDef(
            code = "g2-data-collect", subject = Question.SUBJECT_MATH, name = "数据收集与整理",
            type = TYPE_REASONING, topic = "g2-stat", stage = 1, prerequisites = emptyList(),
            observable = "能用画正字记录数据，并说出最多和最少",
            bkt = reasoning()
        ),
        AnchorDef(
            code = "g2-pattern", subject = Question.SUBJECT_MATH, name = "规律探索",
            type = TYPE_REASONING, topic = "g2-stat", stage = 2,
            prerequisites = listOf("g2-data-collect"),
            observable = "能找出 1、3、5、7 的规律并说出下一个数",
            bkt = reasoning()
        ),

        // ===== 小学二年级：拼音与语音 =====
        AnchorDef(
            code = "chinese-pinyin-polyphone", subject = Question.SUBJECT_CHINESE, name = "多音字辨读",
            type = TYPE_RECOGNIZE, topic = "g2-pinyin", stage = 1, prerequisites = emptyList(),
            observable = "在语境中选对多音字读音，2 道全对",
            bkt = recognize()
        ),
        AnchorDef(
            code = "g2-pinyin-similar", subject = Question.SUBJECT_CHINESE, name = "音近字辨析",
            type = TYPE_REASONING, topic = "g2-pinyin", stage = 2,
            prerequisites = listOf("chinese-pinyin-polyphone"),
            observable = "能分清「在 / 再」「做 / 作」在句子中的用法",
            bkt = reasoning()
        ),

        // ===== 小学二年级：识字与写字 =====
        AnchorDef(
            code = "g2-char-recognize", subject = Question.SUBJECT_CHINESE, name = "识字与组词",
            type = TYPE_RECOGNIZE, topic = "g2-literacy", stage = 1, prerequisites = emptyList(),
            observable = "能认读二年级常用字，并各组一个词",
            bkt = recognize()
        ),
        AnchorDef(
            code = "g2-char-form", subject = Question.SUBJECT_CHINESE, name = "形近字辨析",
            type = TYPE_REASONING, topic = "g2-literacy", stage = 2,
            prerequisites = listOf("g2-char-recognize"),
            observable = "能分清「己 / 已」「日 / 目」并正确组词",
            bkt = reasoning()
        ),
        AnchorDef(
            code = "g2-word-accumulate", subject = Question.SUBJECT_CHINESE, name = "词语积累与搭配",
            type = TYPE_MEANING, topic = "g2-literacy", stage = 3,
            prerequisites = listOf("g2-char-recognize"),
            observable = "能正确搭配「明亮的（眼睛）」这类词语",
            bkt = meaning()
        ),

        // ===== 小学二年级：阅读与表达 =====
        AnchorDef(
            code = "chinese-reading-main-idea", subject = Question.SUBJECT_CHINESE, name = "短文主旨",
            type = TYPE_MEANING, topic = "g2-reading", stage = 1, prerequisites = emptyList(),
            observable = "读完短文能说出「主要讲了什么」",
            bkt = meaning()
        ),
        AnchorDef(
            code = "g2-reading-detail", subject = Question.SUBJECT_CHINESE, name = "提取关键信息",
            type = TYPE_REASONING, topic = "g2-reading", stage = 2,
            prerequisites = listOf("chinese-reading-main-idea"),
            observable = "能在短文中找到「谁、什么时候、做了什么」",
            bkt = reasoning()
        ),
        AnchorDef(
            code = "g2-writing-sentence", subject = Question.SUBJECT_CHINESE, name = "看图写话与造句",
            type = TYPE_MEANING, topic = "g2-reading", stage = 3,
            prerequisites = listOf("g2-reading-detail"),
            observable = "能用「一边……一边……」造完整句子，语句通顺",
            bkt = meaning()
        )
    )

    /** v0.6.0 首版启用的三个核心锚点 */
    const val CODE_SUB_BORROW = "math-sub-borrow"
    const val CODE_MUL_MEANING = "math-mul-meaning"
    const val CODE_POLYPHONE = "chinese-pinyin-polyphone"

    val core: List<AnchorDef> = listOf(
        all.first { it.code == CODE_SUB_BORROW },
        all.first { it.code == CODE_MUL_MEANING },
        all.first { it.code == CODE_POLYPHONE }
    )

    const val CODE_KG_COUNT = "kg-count-10"
    const val CODE_KG_SHAPE = "kg-shape-space"
    const val CODE_KG_CLASSIFY = "kg-classify-compare"
    const val CODE_KG_LISTEN = "kg-listen-express"
    const val CODE_KG_PINYIN = "kg-pinyin-entry"
    const val CODE_KG_ADD_SUB = "kg-add-sub-10"

    /** 幼儿园首版启用的六个核心锚点（保留供历史引用） */
    val kgCore: List<AnchorDef> = listOf(
        all.first { it.code == CODE_KG_COUNT },
        all.first { it.code == CODE_KG_SHAPE },
        all.first { it.code == CODE_KG_CLASSIFY },
        all.first { it.code == CODE_KG_LISTEN },
        all.first { it.code == CODE_KG_PINYIN },
        all.first { it.code == CODE_KG_ADD_SUB }
    )

    /** 某学段全部锚点，按板块顺序与板块内顺序排列 */
    fun forStage(stage: String): List<AnchorDef> {
        val topicOrder = topics.filter { it.stage == stage }.associate { it.code to it.order }
        return all.filter { it.stageCode == stage }
            .sortedWith(compareBy({ topicOrder[it.topic] ?: 99 }, { it.stage }))
    }

    /** 兼容旧调用 */
    fun coreOf(stage: String): List<AnchorDef> = forStage(stage)

    fun topicsOf(stage: String): List<TopicDef> =
        topics.filter { it.stage == stage }.sortedBy { it.order }

    val levels: List<String> = listOf(
        Question.LEVEL_MEMORY,
        Question.LEVEL_UNDERSTANDING,
        Question.LEVEL_APPLICATION
    )

    fun byCode(code: String): AnchorDef? = all.firstOrNull { it.code == code }

    fun topicOf(anchorCode: String): TopicDef? =
        byCode(anchorCode)?.let { a -> topics.firstOrNull { it.code == a.topic } }

    fun anchorsOfTopic(topicCode: String): List<AnchorDef> =
        all.filter { it.topic == topicCode }.sortedBy { it.stage }

    fun nameOf(code: String): String = byCode(code)?.name ?: "未分类"

    fun levelLabel(level: String): String = when (level) {
        Question.LEVEL_MEMORY -> "记忆"
        Question.LEVEL_UNDERSTANDING -> "理解"
        Question.LEVEL_APPLICATION -> "应用"
        else -> level
    }

    fun statusLabel(status: String): String = when (status) {
        "SOLID" -> "已稳固"
        "IN_PROGRESS" -> "攻坚中"
        else -> "未触发"
    }

    /** 知识点的历史数据 → 锚点映射，用于老用户数据迁移 */
    fun legacyAnchorOf(knowledgePoint: String): String? = when {
        knowledgePoint.contains("退位") || knowledgePoint.contains("减法") -> CODE_SUB_BORROW
        knowledgePoint.contains("乘法") || knowledgePoint.contains("口诀") -> CODE_MUL_MEANING
        knowledgePoint.contains("拼音") || knowledgePoint.contains("多音字") -> CODE_POLYPHONE
        else -> null
    }
}
