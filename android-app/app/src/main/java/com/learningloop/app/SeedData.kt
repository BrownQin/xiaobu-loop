package com.learningloop.app

import com.learningloop.app.data.Stage
import com.learningloop.app.data.entity.Question

/**
 * 二年级语文 + 数学题库种子数据。
 *
 * 启动时由 MainActivity 调用 QuestionDao.insertAll 一次性写入。
 * 共 187 题：语文 87 题（拼音多音字、识字写字、词语、句子、古诗、课文理解、口语交际）
 *          + 数学 100 题（100 以内加减、表内乘法、长度单位、角、观察物体、时间、搭配、统计、解决问题）。
 *
 * 部分题目带有锚点标注，供「锚点 × 认知层级」的 Loop 测评使用：
 *   math-sub-borrow 退位减法的算理 21 题
 *   math-mul-meaning 乘法的意义 20 题
 *   chinese-pinyin-polyphone 多音字辨读 25 题
 *   辅助锚点：math-add-carry、math-mul-table、chinese-reading-main-idea
 *
 * 知识点命名规范：
 *   chinese-拼音-多音字 / chinese-识字-同音字 / chinese-词语-量词 / chinese-句子-标点
 *   chinese-古诗-登鹳雀楼 / chinese-阅读-小蝌蚪找妈妈
 *   math-计算-100以内进位加 / math-乘法-口诀 / math-单位-长度 / math-几何-角 / math-时间-认识几时几分
 */
object SeedData {

    // 填空题需要键盘输入，不适合幼儿园和低年级孩子，统一不再进入题库
    fun all(): List<Question> =
        (chinese() + math() + kindergarten()).filter { it.type != Question.TYPE_FILL }

    // ==================== 语文 87 题 ====================

    private fun chinese() = chinesePinyin() +
        chinesePolyphoneExtra() +
        chineseCharacter() +
        chineseWord() +
        chineseSentence() +
        chinesePoem() +
        chineseReading() +
        chineseReadingExtra() +
        chineseSpeaking() +
        chineseGeneralExtra()

    // ---------- 语文：拼音与多音字（10 题） ----------
    private fun chinesePinyin() = listOf(
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "\"长大\" 中的 \"长\" 读音是？",
            options = """{"A":"zhǎng","B":"cháng","C":"chàng","D":"zhàng"}""",
            answer = "A",
            explanation = "表示生长、长大时读 zhǎng；表示长短时读 cháng，如\"长江\"。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "\"音乐\" 中的 \"乐\" 读音是？",
            options = """{"A":"lè","B":"yuè","C":"yào","D":"luò"}""",
            answer = "B",
            explanation = "表示音乐、乐曲时读 yuè；表示快乐时读 lè。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "\"银行\" 中的 \"行\" 读音是？",
            options = """{"A":"xíng","B":"háng","C":"hàng","D":"xìng"}""",
            answer = "B",
            explanation = "银行、一行字读 háng；行走、不行读 xíng。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "\"种花\" 中的 \"种\" 读音是？",
            options = """{"A":"zhǒng","B":"zhòng","C":"chóng","D":"zhōng"}""",
            answer = "B",
            explanation = "表示种下、种植时读 zhòng；表示种子时读 zhǒng。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "\"爱好\" 中的 \"好\" 读音是？",
            options = """{"A":"hǎo","B":"hào","C":"háo","D":"hāo"}""",
            answer = "B",
            explanation = "表示喜欢时读 hào；表示美好时读 hǎo，如\"好人\"。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-轻声",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "下面哪个词里的 \"子\" 读轻声？",
            options = """{"A":"子女","B":"桌子","C":"子孙","D":"子夜"}""",
            answer = "B",
            explanation = "\"桌子\" 的 \"子\" 读轻声 zi，又轻又短；其它三个都读 zǐ。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-儿化",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "下面哪个词读儿化音？",
            options = """{"A":"儿童","B":"花儿","C":"儿女","D":"儿科"}""",
            answer = "B",
            explanation = "\"花儿\" 读 huār，两个字合成一个音节，这就是儿化音。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-声母韵母",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "下面哪个字的声母是 zh？",
            options = """{"A":"知","B":"资","C":"思","D":"吃"}""",
            answer = "A",
            explanation = "\"知\" 读 zhī，声母是 zh；\"资\" 是 z，\"思\" 是 s，\"吃\" 是 ch。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-整体认读音节",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "下面哪个是整体认读音节？",
            options = """{"A":"yan","B":"yuan","C":"you","D":"yao"}""",
            answer = "B",
            explanation = "yuan 是整体认读音节，要一口读出，不能拼 y-u-an。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-声调",
            difficulty = 2, type = Question.TYPE_FILL,
            content = "请写出 \"雪\" 的拼音：____",
            options = "",
            answer = "xuě",
            explanation = "\"雪\" 读第三声 xuě，注意 ü 和 e 相拼时两点要去掉。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        )
    )

    // ---------- 语文：识字与写字（12 题） ----------
    private fun chineseCharacter() = listOf(
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-识字-同音字",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "他____下头，看见一只小蚂蚁。横线上填哪个字？",
            options = """{"A":"底","B":"低","C":"抵","D":"第"}""",
            answer = "B",
            explanation = "\"低头\" 是把头放低，用单人旁的 \"低\"；\"底\" 指最下面的部分。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-识字-同音字",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "妈妈____上围巾出门了。横线上填哪个字？",
            options = """{"A":"带","B":"代","C":"戴","D":"待"}""",
            answer = "C",
            explanation = "把东西穿在头上、戴在身上用 \"戴\"，如\"戴帽子\"\"戴围巾\"。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-识字-同音字",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "请____在椅子上。横线上填哪个字？",
            options = """{"A":"座","B":"做","C":"作","D":"坐"}""",
            answer = "D",
            explanation = "\"坐\" 是坐下的动作；\"座位\" 的 \"座\" 是名词，用广字头。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-识字-同音字",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "我在家里____作业。横线上填哪个字？",
            options = """{"A":"做","B":"作","C":"坐","D":"座"}""",
            answer = "A",
            explanation = "\"做作业\" 的 \"做\" 表示干一件事，用单人旁。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-识字-同音字",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "公____里开满了鲜花。横线上填哪个字？",
            options = """{"A":"圆","B":"员","C":"园","D":"元"}""",
            answer = "C",
            explanation = "种花种树的地方是 \"公园\"，用方框里一个 \"元\" 的 \"园\"。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-识字-同音字",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "爸爸是公司里的一名____工。横线上填哪个字？",
            options = """{"A":"元","B":"员","C":"园","D":"圆"}""",
            answer = "B",
            explanation = "\"员工\" 指工作人员，用口字底的 \"员\"。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-识字-同音字",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "今天天气很____，我们一起去公园。横线上填哪个字？",
            options = """{"A":"睛","B":"精","C":"清","D":"晴"}""",
            answer = "D",
            explanation = "天气好是 \"晴天\"，用日字旁；\"眼睛\" 才是目字旁的 \"睛\"。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-识字-同音字",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "我____经写完作业了。横线上填哪个字？",
            options = """{"A":"己","B":"巳","C":"已","D":"几"}""",
            answer = "C",
            explanation = "\"已经\" 表示事情做完了，用 \"已\"；\"自己\" 才用 \"己\"。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-识字-同音字",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "妈妈去市场____菜。横线上填哪个字？",
            options = """{"A":"卖","B":"迈","C":"麦","D":"买"}""",
            answer = "D",
            explanation = "拿钱换东西是 \"买\"；把东西换钱才是 \"卖\"，两个字只差一个十字头。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-识字-偏旁部首",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "\"河\" 字的偏旁是什么？",
            options = """{"A":"讠","B":"口","C":"氵","D":"可"}""",
            answer = "C",
            explanation = "\"河\" 是三点水旁，说明这个字和水有关系。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-识字-偏旁部首",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "带有 \"木\" 字旁的字，大多和什么有关？",
            options = """{"A":"树木","B":"水","C":"说话","D":"火"}""",
            answer = "A",
            explanation = "木字旁表示树木，如 \"树、林、桥\"，都和木头有关。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-识字-偏旁部首",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "\"晴\" 的偏旁是 \"日\"，说明这个字和什么有关？",
            options = """{"A":"太阳","B":"水","C":"眼睛","D":"说话"}""",
            answer = "A",
            explanation = "日字旁和太阳、天气有关；\"晴\" 就是有太阳的好天气。"
        )
    )

    // ---------- 语文：词语（10 题） ----------
    private fun chineseWord() = listOf(
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-词语-量词",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "一____马",
            options = """{"A":"匹","B":"头","C":"只","D":"条"}""",
            answer = "A",
            explanation = "马用 \"匹\" 来数，一头牛、一只鸟、一条鱼。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-词语-量词",
            difficulty = 2, type = Question.TYPE_FILL,
            content = "一____雨伞",
            options = "",
            answer = "把",
            explanation = "雨伞有把手，用 \"把\" 来数，如\"一把伞\"\"一把刀\"。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-词语-量词",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "一____纸",
            options = """{"A":"块","B":"张","C":"片","D":"条"}""",
            answer = "B",
            explanation = "又薄又平的东西用 \"张\"，如\"一张纸\"\"一张桌子\"。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-词语-AABB式",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "下面哪个词是 AABB 式？",
            options = """{"A":"商量商量","B":"干干净净","C":"雪白","D":"高兴"}""",
            answer = "B",
            explanation = "\"干干净净\" 是 \"干净\" 两个字各重复一次，属于 AABB 式。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-词语-ABAB式",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "下面哪个词是 ABAB 式？",
            options = """{"A":"高高兴兴","B":"大大小小","C":"讨论讨论","D":"又高又大"}""",
            answer = "C",
            explanation = "\"讨论讨论\" 是整个词重复一次，属于 ABAB 式；AABB 式是每个字各重复。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-词语-近义词",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "\"美丽\" 的近义词是？",
            options = """{"A":"漂亮","B":"丑陋","C":"难看","D":"生气"}""",
            answer = "A",
            explanation = "\"美丽\" 和 \"漂亮\" 意思相近，都说好看。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-词语-反义词",
            difficulty = 1, type = Question.TYPE_FILL,
            content = "\"冷\" 的反义词是____",
            options = "",
            answer = "热",
            explanation = "冷和热是一对反义词，意思相反。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-词语-反义词",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "\"危险\" 的反义词是？",
            options = """{"A":"害怕","B":"安全","C":"小心","D":"安心"}""",
            answer = "B",
            explanation = "危险是可能受到伤害，安全是没有危险，意思正好相反。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-词语-词语搭配",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "（ ）的荷叶",
            options = """{"A":"跳跳","B":"圆圆","C":"跑跑","D":"说说"}""",
            answer = "B",
            explanation = "荷叶是又圆又大的，所以说\"圆圆的荷叶\"。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-词语-词语搭配",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "（ ）的秋天",
            options = """{"A":"金黄","B":"跳跳","C":"跑跑","D":"看看"}""",
            answer = "A",
            explanation = "秋天树叶黄了、稻子熟了，用\"金黄\"来形容最合适。"
        )
    )

    // ---------- 语文：句子与标点（8 题） ----------
    private fun chineseSentence() = listOf(
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-句子-标点",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "\"这本书是谁的____\" 横线上应填什么标点？",
            options = """{"A":"。","B":"？","C":"！","D":"，"}""",
            answer = "B",
            explanation = "\"谁\" 是疑问词，问句末尾用问号。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-句子-标点",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "\"多美的花啊____\" 横线上应填什么标点？",
            options = """{"A":"，","B":"。","C":"！","D":"？"}""",
            answer = "C",
            explanation = "\"啊\" 表示赞叹，感情很强烈，句末用感叹号。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-句子-标点",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "妈妈对我说____\"早点回家。\" 横线上应填什么标点？",
            options = """{"A":"，","B":"：","C":"。","D":"？"}""",
            answer = "B",
            explanation = "提示语后面要用冒号，再用引号引出说的话。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-句子-标点",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "\"春天来了____小草绿了。\" 横线上应填什么标点？",
            options = """{"A":"、","B":"，","C":"：","D":"！"}""",
            answer = "B",
            explanation = "一句话中间停顿用逗号，句子说完了才用句号。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-句子-把字句被字句",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "\"小明吃掉了苹果。\" 改成 \"被\" 字句是？",
            options = """{"A":"苹果被小明吃掉了。","B":"小明被苹果吃掉了。","C":"苹果把小明吃掉了。","D":"小明把苹果吃掉了。"}""",
            answer = "A",
            explanation = "\"被\" 字句要把接受动作的\"苹果\"放到前面：苹果被小明吃掉了。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-句子-照样子写句子",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "例：弯弯的月儿像小船。下面哪句也是打比方？",
            options = """{"A":"圆圆的太阳像火球。","B":"太阳升起来了。","C":"我喜欢太阳。","D":"太阳红红的。"}""",
            answer = "A",
            explanation = "例句用\"像\"把月儿比作小船，A 句也用\"像\"把太阳比作火球。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-句子-病句",
            difficulty = 5, type = Question.TYPE_CHOICE,
            content = "下面哪一句没有毛病？",
            options = """{"A":"我大概差不多八点到。","B":"我八点到。","C":"我大约八点一定到。","D":"我八点可能一定到。"}""",
            answer = "B",
            explanation = "\"大概\"\"差不多\"意思重复，\"可能\"\"一定\"互相矛盾，只有 B 句通顺。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-句子-排列句子",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "把句子排通顺：①小蚂蚁爬出洞口 ②它看见一只小虫 ③就把它搬回家",
            options = """{"A":"②①③","B":"①②③","C":"③②①","D":"②③①"}""",
            answer = "B",
            explanation = "先爬出洞口，再看见小虫，最后搬回家，按事情先后排：①②③。"
        )
    )

    // ---------- 语文：古诗积累（11 题） ----------
    private fun chinesePoem() = listOf(
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-古诗-梅花",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "《梅花》墙角数枝梅，____。",
            options = """{"A":"为有暗香来","B":"凌寒独自开","C":"遥知不是雪","D":"春风吹又生"}""",
            answer = "B",
            explanation = "王安石《梅花》：墙角数枝梅，凌寒独自开。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-古诗-梅花",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "《梅花》遥知不是雪，____。",
            options = """{"A":"为有暗香来","B":"凌寒独自开","C":"疑是银河落九天","D":"霜叶红于二月花"}""",
            answer = "A",
            explanation = "遥知不是雪，为有暗香来。因为有淡淡的花香飘来。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-古诗-小儿垂钓",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "《小儿垂钓》蓬头稚子学垂纶，____。",
            options = """{"A":"侧坐莓苔草映身","B":"怕得鱼惊不应人","C":"路上行人欲断魂","D":"忙趁东风放纸鸢"}""",
            answer = "A",
            explanation = "胡令能《小儿垂钓》：蓬头稚子学垂纶，侧坐莓苔草映身。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-古诗-小儿垂钓",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "《小儿垂钓》路人借问遥招手，____。",
            options = """{"A":"侧坐莓苔草映身","B":"怕得鱼惊不应人","C":"独钓寒江雪","D":"手可摘星辰"}""",
            answer = "B",
            explanation = "小孩怕说话惊跑了鱼，所以远远摇手，不敢回答路人。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-古诗-登鹳雀楼",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "《登鹳雀楼》白日依山尽，____。",
            options = """{"A":"黄河入海流","B":"更上一层楼","C":"欲穷千里目","D":"遥看瀑布挂前川"}""",
            answer = "A",
            explanation = "王之涣《登鹳雀楼》：白日依山尽，黄河入海流。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-古诗-登鹳雀楼",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "《登鹳雀楼》欲穷千里目，____。",
            options = """{"A":"黄河入海流","B":"更上一层楼","C":"白日依山尽","D":"疑是银河落九天"}""",
            answer = "B",
            explanation = "想看得更远，就要再登高一层：欲穷千里目，更上一层楼。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-古诗-望庐山瀑布",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "《望庐山瀑布》日照香炉生紫烟，____。",
            options = """{"A":"飞流直下三千尺","B":"遥看瀑布挂前川","C":"疑是银河落九天","D":"黄河入海流"}""",
            answer = "B",
            explanation = "李白《望庐山瀑布》：日照香炉生紫烟，遥看瀑布挂前川。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-古诗-望庐山瀑布",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "《望庐山瀑布》飞流直下三千尺，____。",
            options = """{"A":"疑是银河落九天","B":"遥看瀑布挂前川","C":"更上一层楼","D":"手可摘星辰"}""",
            answer = "A",
            explanation = "瀑布像银河从天上落下来：疑是银河落九天。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-古诗-江雪",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "《江雪》千山鸟飞绝，____。",
            options = """{"A":"万径人踪灭","B":"孤舟蓑笠翁","C":"独钓寒江雪","D":"风吹草低见牛羊"}""",
            answer = "A",
            explanation = "柳宗元《江雪》：千山鸟飞绝，万径人踪灭。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-古诗-敕勒歌",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "《敕勒歌》天苍苍，野茫茫，____。",
            options = """{"A":"笼盖四野","B":"风吹草低见牛羊","C":"天似穹庐","D":"阴山下"}""",
            answer = "B",
            explanation = "天苍苍，野茫茫，风吹草低见牛羊。\"见\"在这里读 xiàn，是出现的意思。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-古诗-夜宿山寺",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "《夜宿山寺》危楼高百尺，____。",
            options = """{"A":"不敢高声语","B":"手可摘星辰","C":"恐惊天上人","D":"疑是银河落九天"}""",
            answer = "B",
            explanation = "李白《夜宿山寺》：危楼高百尺，手可摘星辰。"
        )
    )

    // ---------- 语文：课文理解（11 题） ----------
    private fun chineseReading() = listOf(
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-阅读-小蝌蚪找妈妈",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "《小蝌蚪找妈妈》中，小蝌蚪先长出什么？",
            options = """{"A":"两条前腿","B":"两条后腿","C":"尾巴","D":"翅膀"}""",
            answer = "B",
            explanation = "小蝌蚪先长出两条后腿，再长出两条前腿，最后尾巴不见了，变成青蛙。",
            anchorCode = "chinese-reading-main-idea",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-阅读-我是什么",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "《我是什么》中，\"我\"会变成云、雨、雪，\"我\"是？",
            options = """{"A":"风","B":"火","C":"水","D":"土"}""",
            answer = "C",
            explanation = "水变成汽升上天空成了云，又会变成雨、冰雹和雪落下来。",
            anchorCode = "chinese-reading-main-idea",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-阅读-植物妈妈有办法",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "《植物妈妈有办法》中，蒲公英的种子靠什么传播？",
            options = """{"A":"风","B":"动物的皮毛","C":"水","D":"太阳"}""",
            answer = "A",
            explanation = "蒲公英的种子像降落伞，风一吹就飘走了。",
            anchorCode = "chinese-reading-main-idea",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-阅读-植物妈妈有办法",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "《植物妈妈有办法》中，苍耳靠什么传播种子？",
            options = """{"A":"风","B":"动物的皮毛","C":"水","D":"自己弹开"}""",
            answer = "B",
            explanation = "苍耳身上有带钩的刺，挂在动物皮毛上就被带到远方。",
            anchorCode = "chinese-reading-main-idea",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-阅读-曹冲称象",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "《曹冲称象》中，曹冲用什么代替大象的重量？",
            options = """{"A":"木船","B":"石头","C":"水","D":"人"}""",
            answer = "B",
            explanation = "把大象赶上船画线，再换成石头装到同样位置，称石头就知道象有多重。",
            anchorCode = "chinese-reading-main-idea",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-阅读-玲玲的画",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "《玲玲的画》中，玲玲把弄脏的地方画成了什么？",
            options = """{"A":"小鸟","B":"小花狗","C":"小猫","D":"花朵"}""",
            answer = "B",
            explanation = "玲玲在弄脏的地方画了一只小花狗，画反而更好了。",
            anchorCode = "chinese-reading-main-idea",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-阅读-坐井观天",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "《坐井观天》中，青蛙认为天有多大？",
            options = """{"A":"无边无际","B":"井口那么大","C":"像大海一样大","D":"像草原一样大"}""",
            answer = "B",
            explanation = "青蛙坐在井里，只能看见井口那么大的天，所以它说错了。",
            anchorCode = "chinese-reading-main-idea",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-阅读-我要的是葫芦",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "《我要的是葫芦》中，种葫芦的人为什么没有得到葫芦？",
            options = """{"A":"不治蚜虫","B":"不浇水","C":"不施肥","D":"太阳太晒"}""",
            answer = "A",
            explanation = "叶子上长了蚜虫他不治，叶子被吃光，葫芦也慢慢变黄掉了。",
            anchorCode = "chinese-reading-main-idea",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-阅读-大禹治水",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "《大禹治水》中，大禹治水用的办法是？",
            options = """{"A":"把水堵住","B":"挖通河道，让水流走","C":"用桶挑水","D":"搬家躲开"}""",
            answer = "B",
            explanation = "大禹用疏导的办法，挖通河道把洪水引到大海里去。",
            anchorCode = "chinese-reading-main-idea",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-阅读-狐假虎威",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "\"狐假虎威\" 中，狐狸借着谁的威风吓跑百兽？",
            options = """{"A":"老虎","B":"狮子","C":"大象","D":"狼"}""",
            answer = "A",
            explanation = "狐狸走在老虎前面，百兽怕的是老虎，狐狸只是借了老虎的威风。",
            anchorCode = "chinese-reading-main-idea",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-阅读-小马过河",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "《小马过河》中，小马最后明白了什么？",
            options = """{"A":"老牛说得对","B":"松鼠说得对","C":"遇事要自己动脑筋试一试","D":"河水很浅"}""",
            answer = "C",
            explanation = "老牛说浅、松鼠说深，小马自己试了才知道：遇事要动脑筋、亲自试试。",
            anchorCode = "chinese-reading-main-idea",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        )
    )

    // ---------- 语文：口语交际 / 看图写话（3 题） ----------
    private fun chineseSpeaking() = listOf(
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-口语交际-补充句子",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "\"下课了，同学们在操场上____。\" 哪句补充得最合适？",
            options = """{"A":"安静地睡觉","B":"快乐地做游戏","C":"慢慢地吃饭","D":"认真地开会"}""",
            answer = "B",
            explanation = "下课了，操场上应该是同学们快乐地做游戏。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-口语交际-排列顺序",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "称象的步骤：①把大象赶上船 ②在船舷上画线 ③往船上装石头。正确顺序是？",
            options = """{"A":"①②③","B":"②①③","C":"③①②","D":"①③②"}""",
            answer = "A",
            explanation = "先赶象上船画线，再换成石头，按这个顺序才能称出大象的重量。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-口语交际-礼貌用语",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "想请同学帮忙，应该怎么说？",
            options = """{"A":"喂，帮我拿一下！","B":"快给我拿！","C":"请你帮我拿一下，好吗？","D":"你必须帮我！"}""",
            answer = "C",
            explanation = "请人帮忙要用\"请\"，再加上\"好吗\"，这样说话最有礼貌。"
        )
    )

    // ==================== 数学 100 题 ====================

    private fun math() = mathCalc() +
        mathSubBorrowExtra() +
        mathMultiply() +
        mathMulMeaningExtra() +
        mathMulTableExtra() +
        mathAddCarryExtra() +
        mathLength() +
        mathAngle() +
        mathObserve() +
        mathTime() +
        mathMatch() +
        mathStatistics() +
        mathWordProblem() +
        mathGeneralExtra()

    // ---------- 数学：100 以内加减法（14 题） ----------
    private fun mathCalc() = listOf(
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内不进位加",
            difficulty = 1, type = Question.TYPE_FILL,
            content = "45 + 32 = ?",
            options = "",
            answer = "77",
            explanation = "个位 5+2=7，十位 4+3=7，结果是 77。",
            anchorCode = "math-add-carry",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内进位加",
            difficulty = 3, type = Question.TYPE_FILL,
            content = "36 + 29 = ?",
            options = "",
            answer = "65",
            explanation = "个位 6+9=15，写 5 向十位进 1；十位 3+2+1=6，结果是 65。",
            anchorCode = "math-add-carry",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内进位加",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "47 + 38 = ?",
            options = """{"A":"75","B":"85","C":"84","D":"74"}""",
            answer = "B",
            explanation = "个位 7+8=15，写 5 进 1；十位 4+3+1=8，结果是 85。",
            anchorCode = "math-add-carry",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内不退位减",
            difficulty = 1, type = Question.TYPE_FILL,
            content = "78 - 45 = ?",
            options = "",
            answer = "33",
            explanation = "个位 8-5=3，十位 7-4=3，结果是 33。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "63 - 28 = ?",
            options = """{"A":"45","B":"35","C":"34","D":"91"}""",
            answer = "B",
            explanation = "个位 3 不够减 8，向十位借 1 变 13，13-8=5；十位 6 变 5，5-2=3，结果是 35。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 3, type = Question.TYPE_FILL,
            content = "90 - 46 = ?",
            options = "",
            answer = "44",
            explanation = "个位 0 不够减 6，向十位借 1 变 10，10-6=4；十位 9 变 8，8-4=4，结果是 44。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-连加",
            difficulty = 3, type = Question.TYPE_FILL,
            content = "25 + 17 + 38 = ?",
            options = "",
            answer = "80",
            explanation = "先算 25+17=42，再算 42+38=80。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-连减",
            difficulty = 4, type = Question.TYPE_FILL,
            content = "92 - 35 - 27 = ?",
            options = "",
            answer = "30",
            explanation = "先算 92-35=57，再算 57-27=30。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-加减混合",
            difficulty = 4, type = Question.TYPE_FILL,
            content = "56 - 29 + 18 = ?",
            options = "",
            answer = "45",
            explanation = "从左往右算：56-29=27，27+18=45。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-竖式",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "竖式计算 34+58，个位 4+8=12，应该怎么写？",
            options = """{"A":"个位写 2，向十位进 1","B":"个位写 12","C":"个位写 1，向十位进 2","D":"个位写 0"}""",
            answer = "A",
            explanation = "个位满十要向十位进 1，个位写 2，十位多算 1。",
            anchorCode = "math-add-carry",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-竖式",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "计算 62-38，个位 2 减 8 不够减，应该怎么办？",
            options = """{"A":"个位加 10，同时十位少 1","B":"用 8 减 2","C":"个位直接写 0","D":"十位借 1 给个位，个位当 12"}""",
            answer = "D",
            explanation = "从十位借 1 个十，个位就变成 12，12-8=4。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-比较大小",
            difficulty = 3, type = Question.TYPE_FILL,
            content = "45 + 27 ○ 70，○ 里填 >、< 或 =",
            options = "",
            answer = ">",
            explanation = "45+27=72，72 比 70 大，所以填 >。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-求未知数",
            difficulty = 3, type = Question.TYPE_FILL,
            content = "38 + ____ = 62",
            options = "",
            answer = "24",
            explanation = "用 62-38=24，所以横线上填 24。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-被减数减数",
            difficulty = 2, type = Question.TYPE_FILL,
            content = "被减数是 85，减数是 37，差是多少？",
            options = "",
            answer = "48",
            explanation = "差 = 被减数 - 减数 = 85 - 37 = 48。"
        )
    )

    // ---------- 数学：表内乘法（13 题） ----------
    private fun mathMultiply() = listOf(
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-口诀",
            difficulty = 1, type = Question.TYPE_FILL,
            content = "2 × 8 = ?",
            options = "",
            answer = "16",
            explanation = "口诀：二八十六。",
            anchorCode = "math-mul-table",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-口诀",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "3 × 7 = ?",
            options = """{"A":"21","B":"24","C":"18","D":"27"}""",
            answer = "A",
            explanation = "口诀：三七二十一。",
            anchorCode = "math-mul-table",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-口诀",
            difficulty = 3, type = Question.TYPE_FILL,
            content = "4 × 6 = ?",
            options = "",
            answer = "24",
            explanation = "口诀：四六二十四。",
            anchorCode = "math-mul-table",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-口诀",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "6 × 8 = ?",
            options = """{"A":"42","B":"48","C":"54","D":"56"}""",
            answer = "B",
            explanation = "口诀：六八四十八。",
            anchorCode = "math-mul-table",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-口诀",
            difficulty = 2, type = Question.TYPE_FILL,
            content = "7 × 9 = ?",
            options = "",
            answer = "63",
            explanation = "口诀：七九六十三。",
            anchorCode = "math-mul-table",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-口诀",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "8 × 8 = ?",
            options = """{"A":"56","B":"63","C":"64","D":"72"}""",
            answer = "C",
            explanation = "口诀：八八六十四。两个因数相同，只能写一句口诀。",
            anchorCode = "math-mul-table",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-口诀",
            difficulty = 2, type = Question.TYPE_FILL,
            content = "口诀：五六____",
            options = "",
            answer = "三十",
            explanation = "五六三十，就是 5×6=30。",
            anchorCode = "math-mul-table",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-乘加",
            difficulty = 3, type = Question.TYPE_FILL,
            content = "3 × 6 + 7 = ?",
            options = "",
            answer = "25",
            explanation = "先乘后加：3×6=18，18+7=25。",
            anchorCode = "math-mul-table",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-乘减",
            difficulty = 3, type = Question.TYPE_FILL,
            content = "5 × 7 - 9 = ?",
            options = "",
            answer = "26",
            explanation = "先乘后减：5×7=35，35-9=26。",
            anchorCode = "math-mul-table",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-倍的认识",
            difficulty = 4, type = Question.TYPE_WORD,
            content = "苹果有 4 个，梨的个数是苹果的 3 倍，梨有多少个？",
            options = "",
            answer = "12个",
            explanation = "求 4 的 3 倍是多少，用乘法：4×3=12（个）。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-应用题",
            difficulty = 2, type = Question.TYPE_WORD,
            content = "每排坐 8 人，5 排一共坐多少人？",
            options = "",
            answer = "40人",
            explanation = "求 5 个 8 是多少：8×5=40（人）。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-求积",
            difficulty = 2, type = Question.TYPE_FILL,
            content = "一个因数是 9，另一个因数是 4，积是多少？",
            options = "",
            answer = "36",
            explanation = "积 = 因数 × 因数 = 9×4=36，口诀\"四九三十六\"。",
            anchorCode = "math-mul-table",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-交换因数",
            difficulty = 3, type = Question.TYPE_FILL,
            content = "6 × 7 ○ 7 × 6，○ 里填 >、< 或 =",
            options = "",
            answer = "=",
            explanation = "交换两个因数的位置，积不变，所以两边都等于 42。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_EXPRESSION
        )
    )

    // ---------- 数学：长度单位（6 题） ----------
    private fun mathLength() = listOf(
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-单位-长度",
            difficulty = 1, type = Question.TYPE_FILL,
            content = "1 米 = ____ 厘米",
            options = "",
            answer = "100",
            explanation = "1 米 = 100 厘米，这是长度单位之间的进率。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-单位-长度",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "一支铅笔长约 18 ____",
            options = """{"A":"米","B":"厘米","C":"分米","D":"千米"}""",
            answer = "B",
            explanation = "铅笔只有十几厘米长，用\"厘米\"最合适。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-单位-长度",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "一棵大树高约 10 ____",
            options = """{"A":"厘米","B":"米","C":"千米"}""",
            answer = "B",
            explanation = "10 厘米只有一根手指长，大树高约 10 米。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-单位-长度",
            difficulty = 3, type = Question.TYPE_FILL,
            content = "3 米 = ____ 厘米",
            options = "",
            answer = "300",
            explanation = "1 米是 100 厘米，3 米就是 3 个 100 厘米，即 300 厘米。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-单位-长度",
            difficulty = 4, type = Question.TYPE_FILL,
            content = "1 米 - 30 厘米 = ____ 厘米",
            options = "",
            answer = "70",
            explanation = "先把 1 米换成 100 厘米，再算 100-30=70 厘米。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-单位-长度",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "量教室的长，用什么单位合适？",
            options = """{"A":"厘米","B":"米","C":"千米"}""",
            answer = "B",
            explanation = "教室比较长，用\"米\"量最方便；千米是量很远的路程用的。"
        )
    )

    // ---------- 数学：角的初步认识（5 题） ----------
    private fun mathAngle() = listOf(
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-几何-角",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "一个角有几个顶点、几条边？",
            options = """{"A":"1 个顶点、2 条边","B":"2 个顶点、1 条边","C":"1 个顶点、1 条边","D":"2 个顶点、2 条边"}""",
            answer = "A",
            explanation = "角由一个顶点和两条边组成。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-几何-角",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "比直角小的角叫什么角？",
            options = """{"A":"钝角","B":"锐角","C":"直角"}""",
            answer = "B",
            explanation = "比直角小的是锐角，比直角大的是钝角。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-几何-角",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "比直角大的角叫什么角？",
            options = """{"A":"钝角","B":"锐角","C":"直角"}""",
            answer = "A",
            explanation = "张口比直角大的角是钝角，比如张得很开的扇子形成的角。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-几何-角",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "长方形的四个角都是什么角？",
            options = """{"A":"锐角","B":"钝角","C":"直角"}""",
            answer = "C",
            explanation = "长方形的每个角都是方方正正的直角。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-几何-角",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "三角尺上最大的角是什么角？",
            options = """{"A":"锐角","B":"直角","C":"钝角"}""",
            answer = "B",
            explanation = "三角尺上最大的角是直角，另外两个角都是锐角。"
        )
    )

    // ---------- 数学：观察物体（3 题） ----------
    private fun mathObserve() = listOf(
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-观察物体-不同方向",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "从不同方向观察同一个物体，看到的形状会怎样？",
            options = """{"A":"一定相同","B":"可能不同","C":"一定不同"}""",
            answer = "B",
            explanation = "站的位置不同，看到的形状可能不同，也可能相同。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-观察物体-形状判断",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "从上面看一个长方体，看到的是什么形状？",
            options = """{"A":"长方形","B":"圆形","C":"三角形"}""",
            answer = "A",
            explanation = "长方体的上面是长方形，所以从上面看是长方形。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-观察物体-形状判断",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "从任何方向看一个球，看到的都是什么形状？",
            options = """{"A":"圆形","B":"正方形","C":"三角形"}""",
            answer = "A",
            explanation = "球是圆溜溜的，不管从哪边看都是圆形。"
        )
    )

    // ---------- 数学：认识时间（5 题） ----------
    private fun mathTime() = listOf(
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-时间-认识整时",
            difficulty = 1, type = Question.TYPE_FILL,
            content = "时针指向 3，分针指向 12，是几时？",
            options = "",
            answer = "3时",
            explanation = "分针指向 12、时针指向几，就是几时。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-时间-认识半时",
            difficulty = 3, type = Question.TYPE_FILL,
            content = "分针指向 6，时针在 7 和 8 之间，是几时几分？",
            options = "",
            answer = "7时30分",
            explanation = "分针指向 6 是 30 分，时针过了 7 还没到 8，所以是 7 时 30 分。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-时间-认识几时几分",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "8 时 20 分时，分针指向数字几？",
            options = """{"A":"3","B":"4","C":"6","D":"8"}""",
            answer = "B",
            explanation = "分针走一大格是 5 分，20 分要走 4 大格，所以指向 4。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-时间-经过时间",
            difficulty = 4, type = Question.TYPE_WORD,
            content = "一节课 40 分钟，8 时 10 分开始上课，什么时候下课？",
            options = "",
            answer = "8时50分",
            explanation = "10 分 + 40 分 = 50 分，所以 8 时 50 分下课。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-时间-经过时间",
            difficulty = 2, type = Question.TYPE_FILL,
            content = "从 7 时 30 分到 8 时，经过了____分。",
            options = "",
            answer = "30",
            explanation = "7 时 30 分到 8 时，分针走了 30 小格，就是 30 分。"
        )
    )

    // ---------- 数学：数学广角——搭配（3 题） ----------
    private fun mathMatch() = listOf(
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-搭配-简单排列",
            difficulty = 3, type = Question.TYPE_FILL,
            content = "2 件上衣和 3 条裤子，一共有几种不同的搭配？",
            options = "",
            answer = "6种",
            explanation = "每件上衣都能配 3 条裤子，2×3=6（种）。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-搭配-数字组数",
            difficulty = 4, type = Question.TYPE_FILL,
            content = "用数字卡 1、2、3 组成两位数（数字不重复），能组成几个？",
            options = "",
            answer = "6个",
            explanation = "十位有 3 种选法，个位剩 2 种：3×2=6（个），如 12、13、21、23、31、32。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-搭配-握手问题",
            difficulty = 5, type = Question.TYPE_FILL,
            content = "3 个小朋友，每两人握一次手，一共握几次？",
            options = "",
            answer = "3次",
            explanation = "甲乙、甲丙、乙丙，一共 3 次，不要重复数。"
        )
    )

    // ---------- 数学：数据收集整理（3 题） ----------
    private fun mathStatistics() = listOf(
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-统计-读统计表",
            difficulty = 2, type = Question.TYPE_WORD,
            content = "喜欢苹果的有 12 人，喜欢香蕉的有 8 人。喜欢苹果的比喜欢香蕉的多几人？",
            options = "",
            answer = "4人",
            explanation = "求多几人用减法：12-8=4（人）。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-统计-读统计表",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "跳绳 15 人、踢球 10 人、跑步 8 人，喜欢哪项运动的人最多？",
            options = """{"A":"跳绳","B":"踢球","C":"跑步"}""",
            answer = "A",
            explanation = "15 最大，所以喜欢跳绳的人最多。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-统计-条形图",
            difficulty = 4, type = Question.TYPE_FILL,
            content = "条形图中一格代表 2 人，某项占 4 格，表示多少人？",
            options = "",
            answer = "8人",
            explanation = "一格 2 人，4 格就是 4 个 2，2×4=8（人）。"
        )
    )

    // ---------- 数学：解决问题（3 题） ----------
    private fun mathWordProblem() = listOf(
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-解决问题-两步计算",
            difficulty = 4, type = Question.TYPE_WORD,
            content = "妈妈买了 3 袋苹果，每袋 6 个，吃了 5 个，还剩多少个？",
            options = "",
            answer = "13个",
            explanation = "先求一共：3×6=18（个）；再求剩下：18-5=13（个）。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-解决问题-购物",
            difficulty = 4, type = Question.TYPE_WORD,
            content = "小明有 50 元，买书包用去 28 元，买笔用去 9 元，还剩多少钱？",
            options = "",
            answer = "13元",
            explanation = "先算一共花掉：28+9=37（元）；再算剩下：50-37=13（元）。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-解决问题-比多比少",
            difficulty = 5, type = Question.TYPE_WORD,
            content = "二（2）班有男生 24 人，女生比男生少 5 人，全班一共有多少人？",
            options = "",
            answer = "43人",
            explanation = "女生：24-5=19（人）；全班：24+19=43（人）。"
        )
    )

    // ==================== 锚点补充题 ====================
    //
    // 每道锚点题都标注 anchorCode（锚点）、cognitiveLevel（认知层级）、
    // variantType（表征方式），供「锚点 × 认知层级」的 Loop 抽题与换表征验证。

    // ---------- math-sub-borrow 退位减法的算理（17 题） ----------
    private fun mathSubBorrowExtra() = listOf(
        // 记忆层：直接计算、直接认规则
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 2, type = Question.TYPE_FILL,
            content = "72 - 35 = ?",
            options = "",
            answer = "37",
            explanation = "个位 2 不够减 5，向十位借 1，12-5=7；十位 7 变 6，6-3=3，结果是 37。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "50 - 24 = ?",
            options = """{"A":"26","B":"36","C":"24","D":"74"}""",
            answer = "A",
            explanation = "个位 0 不够减 4，退一当十，10-4=6；十位 5 变 4，4-2=2，结果是 26。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "退位减法中，个位不够减时，要向十位借几个十？",
            options = """{"A":"借 1 个十","B":"借 2 个十","C":"借 1 个百","D":"不用借"}""",
            answer = "A",
            explanation = "个位不够减，就向十位借 1 个十，1 个十就是 10 个一。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 3, type = Question.TYPE_FILL,
            content = "数位表十位 4 颗、个位 2 颗。个位减 6 不够，退 1 颗十位珠后个位多几？",
            options = "",
            answer = "10",
            explanation = "退 1 颗十位珠就是 1 个十，个位多 10 个一，2+10=12，12-6=6。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_DIAGRAM
        ),
        // 理解层：说清算理、判断对错
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "计算 52-27 时，个位 2 不够减 7，应该怎么办？",
            options = """{"A":"向十位借 1 个十，用 12-7","B":"用 7-2 得 5","C":"个位写 0","D":"把 52 看成 50"}""",
            answer = "A",
            explanation = "个位不够减，就从十位退 1 当十，个位变成 12，12-7=5；十位 4-2=2，结果是 25。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "小明算 63-28=45，他错在哪里？",
            options = """{"A":"个位不够减，没有退位","B":"十位忘了少算 1","C":"个位算错了","D":"没有错"}""",
            answer = "B",
            explanation = "个位 3 不够减 8，退一当十后 13-8=5；十位 6 退 1 后是 5，5-2=3，应该得 35，他十位忘了少算 1。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "为什么 70-36 的竖式里十位要少算 1？",
            options = """{"A":"个位不够减，向十位借了 1 个十","B":"70 是整十数","C":"个位是 0","D":"要加上 36"}""",
            answer = "A",
            explanation = "个位 0 不够减 6，向十位借 1 个十，十位就从 7 变成 6，所以少算 1。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 3, type = Question.TYPE_FILL,
            content = "81-47 的竖式中，十位上算 7-4，横线上应写几？",
            options = "",
            answer = "3",
            explanation = "个位 1 不够减 7，退一当十，十位剩 7，7-4=3；个位 11-7=4，结果是 34。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "小棒图：4 捆和 5 根，要拿走 28 根。拆开 1 捆后，个位一共有几根？",
            options = """{"A":"5","B":"15","C":"10","D":"45"}""",
            answer = "B",
            explanation = "拆开 1 捆是 10 根，加上原来的 5 根，个位一共 15 根，15-8=7。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_DIAGRAM
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "竖式 62-38 中，个位 2 上方的退位点表示什么？",
            options = """{"A":"个位借到 1 个十，变成 12","B":"十位要加 1","C":"个位变成 2","D":"结果要减 1"}""",
            answer = "A",
            explanation = "退位点提醒：十位借出 1 个十，个位的 2 就变成 12，12-8=4。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_DIAGRAM
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "小华拿 30 元买 8 元的本子，个位 0 不够减 8，他该怎么想？",
            options = """{"A":"向十位借 1 当十，找回 22 元","B":"用 8-0=8，找回 8 元","C":"不用找钱","D":"找回 30 元"}""",
            answer = "A",
            explanation = "个位 0 不够减 8，从十位退 1 当十：10-8=2，十位剩 2，所以找回 22 元。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_CONTEXT
        ),
        // 应用层：生活情境、两步问题
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 3, type = Question.TYPE_WORD,
            content = "妈妈有 60 元，买水果花了 25 元，还剩多少元？",
            options = "",
            answer = "35元",
            explanation = "60-25：个位 0 不够减 5，退一当十，10-5=5；十位 5-2=3，还剩 35 元。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 3, type = Question.TYPE_WORD,
            content = "图书角有 82 本书，借走 47 本，还剩多少本？",
            options = "",
            answer = "35本",
            explanation = "82-47：个位 12-7=5，十位 7-4=3，还剩 35 本。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 3, type = Question.TYPE_WORD,
            content = "公交车上有 45 人，到站下去 18 人，车上还有多少人？",
            options = "",
            answer = "27人",
            explanation = "45-18：个位 15-8=7，十位 3-1=2，还有 27 人。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 4, type = Question.TYPE_WORD,
            content = "小明跳绳 73 下，小红比小明少跳 28 下，小红跳了多少下？",
            options = "",
            answer = "45下",
            explanation = "求比 73 少 28 的数，用减法：73-28=45（下）。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-解决问题-两步计算",
            difficulty = 5, type = Question.TYPE_CHOICE,
            content = "一本故事书 48 页，小东第一天看 19 页，第二天看 15 页，还剩多少页？",
            options = """{"A":"14 页","B":"24 页","C":"34 页","D":"4 页"}""",
            answer = "A",
            explanation = "先算两天一共看了 19+15=34（页），再算还剩 48-34=14（页）。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内退位减",
            difficulty = 4, type = Question.TYPE_WORD,
            content = "线段图：全长 65 米，左边一段 28 米，右边一段长多少米？",
            options = "",
            answer = "37米",
            explanation = "求剩下的一段用减法：65-28=37（米），个位 15-8=7，十位 5-2=3。",
            anchorCode = "math-sub-borrow",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_DIAGRAM
        )
    )

    // ---------- math-mul-meaning 乘法的意义（17 题） ----------
    private fun mathMulMeaningExtra() = listOf(
        // 记忆层：算式与读法、各部分名称
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-意义",
            difficulty = 2, type = Question.TYPE_FILL,
            content = "4 + 4 + 4 = ____ × ____",
            options = "",
            answer = "4×3",
            explanation = "3 个 4 相加，可以写成 4×3，也可以写成 3×4。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-意义",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "下面哪个算式表示 3 个 6 相加？",
            options = """{"A":"6×3","B":"6+3","C":"6-3","D":"3+3"}""",
            answer = "A",
            explanation = "3 个 6 相加就是 6+6+6，写成乘法是 6×3。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-意义",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "6×3 读作什么？",
            options = """{"A":"6 乘 3","B":"6 加 3","C":"3 乘 6","D":"6 除以 3"}""",
            answer = "A",
            explanation = "6×3 读作 6 乘 3，表示 3 个 6 相加。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-意义",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "乘法算式中，相乘的两个数叫什么？",
            options = """{"A":"因数","B":"加数","C":"差","D":"和"}""",
            answer = "A",
            explanation = "乘法算式中相乘的两个数叫因数，乘得的结果叫积。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-意义",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "4×5=20 里，20 叫什么？",
            options = """{"A":"积","B":"和","C":"差","D":"因数"}""",
            answer = "A",
            explanation = "4×5=20，20 是乘得的结果，叫积。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-意义",
            difficulty = 2, type = Question.TYPE_FILL,
            content = "点子图：每行 4 个点，一共 3 行。用乘法算式表示是____",
            options = "",
            answer = "4×3",
            explanation = "3 行、每行 4 个，就是 3 个 4，乘法算式是 4×3。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_DIAGRAM
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-意义",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "图中有 3 盘苹果，每盘 6 个。求一共多少个，用哪个算式？",
            options = """{"A":"6×3","B":"6+3","C":"6-3","D":"3+3"}""",
            answer = "A",
            explanation = "3 盘、每盘 6 个，就是 3 个 6，用 6×3 算。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_DIAGRAM
        ),
        // 理解层：说清为什么、辨析意义
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-意义",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "求 3 个 4 是多少，为什么可以用乘法算？",
            options = """{"A":"乘法是求几个相同加数的和","B":"乘法算得快","C":"3 和 4 都很小","D":"3+4=7"}""",
            answer = "A",
            explanation = "3 个 4 相加是 4+4+4，加数都相同，用乘法 4×3 更简便。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-意义",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "每盒 6 支、3 盒，和每盒 3 支、6 盒，总数一样吗？",
            options = """{"A":"一样，都是 18 支","B":"第一个多","C":"第二个多","D":"没法比较"}""",
            answer = "A",
            explanation = "6×3 和 3×6 都等于 18，交换因数的位置，积不变。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-意义",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "下面哪道题不能用 4×5 解决？",
            options = """{"A":"4 个 5 相加","B":"5 个 4 相加","C":"4 和 5 相加","D":"每排 5 人、4 排的人数"}""",
            answer = "C",
            explanation = "4×5 表示几个 4 或几个 5 相加；4 和 5 相加只有两个数，不是相同加数。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-意义",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "两幅点子图：第一幅每份 3 个共 4 份，第二幅每份 4 个共 3 份。点数？",
            options = """{"A":"一样多，都是 12 个","B":"第一幅多","C":"第二幅多","D":"无法比较"}""",
            answer = "A",
            explanation = "两幅图都是 12 个点，3×4 和 4×3 的结果相同。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_DIAGRAM
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-意义",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "小红摆 6 个三角形，每个用 3 根小棒，她列式 6+3，对吗？",
            options = """{"A":"不对，应该用 6×3","B":"对","C":"应该用 6-3","D":"应该用 3+3"}""",
            answer = "A",
            explanation = "6 个三角形、每个 3 根，是 6 个 3 相加，要用 6×3=18（根）。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_CONTEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-意义",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "2+2+2+2+2 写成乘法是 2×5，还可以写成？",
            options = """{"A":"5×2","B":"2+5","C":"5+5","D":"2×2"}""",
            answer = "A",
            explanation = "5 个 2 相加写成乘法是 2×5，交换因数就是 5×2，结果都是 10。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_EXPRESSION
        ),
        // 应用层：情境迁移、两步问题
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-应用题",
            difficulty = 2, type = Question.TYPE_WORD,
            content = "妈妈买了 3 盒铅笔，每盒 4 支，一共多少支？",
            options = "",
            answer = "12支",
            explanation = "求 3 个 4 是多少，用乘法：4×3=12（支）。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-应用题",
            difficulty = 2, type = Question.TYPE_WORD,
            content = "每张桌子坐 6 人，5 张桌子一共坐多少人？",
            options = "",
            answer = "30人",
            explanation = "求 5 个 6 是多少：6×5=30（人）。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-应用题",
            difficulty = 3, type = Question.TYPE_WORD,
            content = "一件衣服钉 4 颗扣子，7 件衣服一共要钉多少颗？",
            options = "",
            answer = "28颗",
            explanation = "求 7 个 4 是多少：4×7=28（颗）。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-解决问题-两步计算",
            difficulty = 5, type = Question.TYPE_CHOICE,
            content = "一盒鸡蛋有 8 个，买 4 盒，吃了 5 个，还剩多少个？",
            options = """{"A":"27 个","B":"32 个","C":"37 个","D":"13 个"}""",
            answer = "A",
            explanation = "先求一共 8×4=32（个），再算还剩 32-5=27（个）。",
            anchorCode = "math-mul-meaning",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_TEXT
        )
    )

    // ---------- chinese-pinyin-polyphone 多音字辨读（15 题） ----------
    private fun chinesePolyphoneExtra() = listOf(
        // 记忆层：认读音（图示卡、生活语境）
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "拼音卡上 \"长\" 有两个读音：zhǎng、cháng。\"长大\" 的 \"长\" 读？",
            options = """{"A":"zhǎng","B":"cháng","C":"zhàng","D":"chāng"}""",
            answer = "A",
            explanation = "表示生长、长大时读 zhǎng；表示长短时读 cháng。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_DIAGRAM
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "妹妹量完身高说：\"我又长高啦！\" 这里的 \"长\" 读？",
            options = """{"A":"zhǎng","B":"cháng","C":"zhàng","D":"zàng"}""",
            answer = "A",
            explanation = "长高是生长，读 zhǎng；表示长短才读 cháng。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_CONTEXT
        ),
        // 理解层：说清为什么、指出错误
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "为什么 \"长大\" 读 zhǎng，\"长江\" 读 cháng？",
            options = """{"A":"表示生长读 zhǎng，表示长短读 cháng","B":"两个字写法不同","C":"两个读音一样","D":"读哪个都行"}""",
            answer = "A",
            explanation = "多音字意思不同，读音也不同：生长读 zhǎng，长短读 cháng。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "\"音乐\" 和 \"快乐\" 里的 \"乐\" 读音不同，是因为？",
            options = """{"A":"意思不同，读音就不同","B":"两个字位置不同","C":"一个是轻声","D":"没有原因"}""",
            answer = "A",
            explanation = "表示音乐读 yuè，表示快乐读 lè，意思不同，读音也不同。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "下面哪个词语中 \"行\" 的读音标错了？",
            options = """{"A":"银行（xíng）","B":"行走（xíng）","C":"一行字（háng）","D":"不行（xíng）"}""",
            answer = "A",
            explanation = "\"银行\" 的 \"行\" 读 háng；行走、不行读 xíng。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "\"爱好\" 的 \"好\" 读 hào，是因为它表示？",
            options = """{"A":"喜欢","B":"美好","C":"很小","D":"很快"}""",
            answer = "A",
            explanation = "表示喜欢时读 hào，如 \"爱好\"；表示美好时读 hǎo。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 5, type = Question.TYPE_CHOICE,
            content = "注音卡：①音乐 yuè ②快乐 lè ③银行 xíng，哪张标错了？",
            options = """{"A":"③","B":"①","C":"②","D":"都没错"}""",
            answer = "A",
            explanation = "银行应读 yín háng，其余两张都标对了。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_DIAGRAM
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "弟弟读 \"我长（cháng）大了\"，姐姐说读错了。为什么？",
            options = """{"A":"表示生长要读 zhǎng","B":"弟弟声音太小","C":"两个字写法不同","D":"读哪个都行"}""",
            answer = "A",
            explanation = "\"长大\" 表示生长，应该读 zhǎng，不读 cháng。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_CONTEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 5, type = Question.TYPE_CHOICE,
            content = "\"风吹草低见牛羊\" 中的 \"见\" 读 xiàn，意思是？",
            options = """{"A":"出现","B":"看见","C":"见面","D":"听见"}""",
            answer = "A",
            explanation = "这里的 \"见\" 读 xiàn，是出现、显露的意思。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_CONTEXT
        ),
        // 应用层：在生活语境中选读音
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "小云的头发有些长，她说：\"我要剪头发。\" 这里的 \"发\" 读？",
            options = """{"A":"fà","B":"fā","C":"fǎ","D":"fāi"}""",
            answer = "A",
            explanation = "头发读 tóu fa，\"发\" 读 fà；发生、发芽读 fā。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "妈妈数了数苹果说：\"一共 8 个。\" \"数一数\" 的 \"数\" 读？",
            options = """{"A":"shǔ","B":"shù","C":"shuò","D":"sù"}""",
            answer = "A",
            explanation = "表示一个一个地数，读 shǔ；表示数目读 shù。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "动物园里有一只熊猫。\"一只\" 的 \"只\" 读？",
            options = """{"A":"zhī","B":"zhǐ","C":"zhí","D":"zī"}""",
            answer = "A",
            explanation = "数东西用 \"只\" 读 zhī；表示 \"只有\" 时读 zhǐ。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "下面句子中 \"行\" 读 háng 的是哪一句？",
            options = """{"A":"妈妈去银行上班","B":"他每天步行上学","C":"这样做不行","D":"行人要走人行道"}""",
            answer = "A",
            explanation = "银行读 yín háng，其余三句里的 \"行\" 都读 xíng。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "下面句子中 \"长\" 读 cháng 的是哪一句？",
            options = """{"A":"柳枝又细又长","B":"我长大了","C":"小树长高了","D":"妹妹长牙了"}""",
            answer = "A",
            explanation = "表示长短读 cháng；长大、长高、长牙表示生长，读 zhǎng。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-拼音-多音字",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "图上日历写着 \"9 月 10 日 教师节\"。\"教师\" 的 \"教\" 读？",
            options = """{"A":"jiào","B":"jiāo","C":"jiǎo","D":"jiáo"}""",
            answer = "A",
            explanation = "教师、教室读 jiào；教书、教写字读 jiāo。",
            anchorCode = "chinese-pinyin-polyphone",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_DIAGRAM
        )
    )

    // ---------- math-add-carry 100 以内进位加（4 题） ----------
    private fun mathAddCarryExtra() = listOf(
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内进位加",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "进位加法中，个位相加满十，要向哪一位进 1？",
            options = """{"A":"十位","B":"个位","C":"百位","D":"不用进"}""",
            answer = "A",
            explanation = "个位满十，就要向十位进 1，这叫 \"满十进一\"。",
            anchorCode = "math-add-carry",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内进位加",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "计算 56+38 时，十位为什么是 5+3+1？",
            options = """{"A":"个位 6+8=14，满十向十位进了 1","B":"十位本来就多 1","C":"个位是 0","D":"要减掉 1"}""",
            answer = "A",
            explanation = "个位 6+8=14，写 4 进 1，所以十位要 5+3+1=9，结果是 94。",
            anchorCode = "math-add-carry",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内进位加",
            difficulty = 4, type = Question.TYPE_CHOICE,
            content = "小棒图：3 捆 6 根和 2 捆 9 根合起来，个位有 15 根，应该怎么摆？",
            options = """{"A":"捆成 1 捆，个位留 5 根","B":"15 根都放个位","C":"都放十位","D":"去掉 10 根"}""",
            answer = "A",
            explanation = "个位满十就捆成 1 捆放到十位，个位留 5 根，这就是满十进一。",
            anchorCode = "math-add-carry",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_DIAGRAM
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-计算-100以内进位加",
            difficulty = 3, type = Question.TYPE_WORD,
            content = "一年级有 27 人，二年级有 38 人，两个年级一共多少人？",
            options = "",
            answer = "65人",
            explanation = "27+38：个位 7+8=15，写 5 进 1；十位 2+3+1=6，一共 65 人。",
            anchorCode = "math-add-carry",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT
        )
    )

    // ---------- math-mul-table 表内乘法口诀（2 题） ----------
    private fun mathMulTableExtra() = listOf(
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-口诀",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "口诀 \"三七二十一\" 写出的乘法算式是？",
            options = """{"A":"3×7=21","B":"3+7=21","C":"7-3=21","D":"3×3=21"}""",
            answer = "A",
            explanation = "三七二十一，就是 3×7=21，也可以写成 7×3=21。",
            anchorCode = "math-mul-table",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-乘法-口诀",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "妈妈买了 4 双袜子，一双 2 只，一共几只？用哪句口诀？",
            options = """{"A":"二四得八","B":"三四十二","C":"四五二十","D":"二五一十"}""",
            answer = "A",
            explanation = "4 双、每双 2 只，是 4 个 2，用口诀 \"二四得八\"，共 8 只。",
            anchorCode = "math-mul-table",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT
        )
    )

    // ---------- chinese-reading-main-idea 短文主旨（2 题） ----------
    private fun chineseReadingExtra() = listOf(
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-阅读-概括主要内容",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "读句子：\"下雨了，小蚂蚁躲到蘑菇下，把叶子盖在头上。\" 主要讲什么？",
            options = """{"A":"小蚂蚁躲雨","B":"蘑菇很大","C":"叶子很绿","D":"天气很好"}""",
            answer = "A",
            explanation = "句子说的是下雨时小蚂蚁怎样躲雨，主要意思是小蚂蚁躲雨。",
            anchorCode = "chinese-reading-main-idea",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-阅读-概括主要内容",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "读完《小蝌蚪找妈妈》，用一个词说主要内容，选哪个？",
            options = """{"A":"找妈妈","B":"学游泳","C":"吃虫子","D":"变颜色"}""",
            answer = "A",
            explanation = "全文围着 \"找妈妈\" 写，小蝌蚪一路问、一路找，最后找到了妈妈。",
            anchorCode = "chinese-reading-main-idea",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_TEXT
        )
    )

    // ---------- 语文：普通练习补充（5 题，未标注锚点） ----------
    private fun chineseGeneralExtra() = listOf(
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-词语-量词",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "一____桥",
            options = """{"A":"座","B":"只","C":"匹","D":"头"}""",
            answer = "A",
            explanation = "桥用 \"座\" 来数，如 \"一座桥\"；马用 \"匹\"，牛用 \"头\"。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-句子-标点",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "\"小朋友____你几岁了____\" 横线上依次填什么标点？",
            options = """{"A":"，？","B":"。，","C":"！。","D":"：！"}""",
            answer = "A",
            explanation = "中间停顿用逗号，问句末尾用问号。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-古诗-咏柳",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "《咏柳》不知细叶谁裁出，____",
            options = """{"A":"二月春风似剪刀","B":"万条垂下绿丝绦","C":"春风吹又生","D":"碧玉妆成一树高"}""",
            answer = "A",
            explanation = "贺知章《咏柳》：不知细叶谁裁出，二月春风似剪刀。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-识字-偏旁部首",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "\"妈、奶、姐\" 都有女字旁，这些字大多和什么有关？",
            options = """{"A":"女性","B":"水","C":"树木","D":"说话"}""",
            answer = "A",
            explanation = "女字旁的字大多和女性、亲属有关，如 \"妈妈、奶奶、姐姐\"。"
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "chinese-阅读-守株待兔",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "\"守株待兔\" 的故事告诉我们什么？",
            options = """{"A":"不能光等运气，要靠劳动","B":"兔子跑得很快","C":"树桩很结实","D":"要多种树"}""",
            answer = "A",
            explanation = "农夫不干活只等兔子撞树，结果一无所获，告诉我们不能靠运气，要靠劳动。"
        )
    )

    // ---------- 数学：普通练习补充（5 题，未标注锚点） ----------
    private fun mathGeneralExtra() = listOf(
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-单位-长度",
            difficulty = 1, type = Question.TYPE_FILL,
            content = "2 米 = ____ 厘米",
            options = "",
            answer = "200",
            explanation = "1 米是 100 厘米，2 米就是 2 个 100 厘米，即 200 厘米。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-几何-角",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "一把三角尺上有几个直角？",
            options = """{"A":"1","B":"2","C":"3","D":"0"}""",
            answer = "A",
            explanation = "三角尺有一个直角，另外两个角是锐角。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-时间-认识几时几分",
            difficulty = 4, type = Question.TYPE_FILL,
            content = "分针指向 9，时针刚过 4，是几时几分？",
            options = "",
            answer = "4时45分",
            explanation = "分针指向 9 是 9 个 5 分，即 45 分；时针刚过 4，所以是 4 时 45 分。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-统计-读统计表",
            difficulty = 3, type = Question.TYPE_WORD,
            content = "三个小组种树 9 棵、12 棵、7 棵，一共种多少棵？",
            options = "",
            answer = "28棵",
            explanation = "求总数用加法：9+12+7=28（棵）。"
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "math-搭配-简单排列",
            difficulty = 3, type = Question.TYPE_FILL,
            content = "3 件上衣和 2 条裤子，一共有几种不同的搭配？",
            options = "",
            answer = "6种",
            explanation = "每件上衣都能配 2 条裤子，3×2=6（种）。"
        )
    )

    // ==================== 幼儿园中班 60 题 ====================

    private fun kindergarten() = kgCount10() +
        kgShapeSpace() +
        kgClassifyCompare() +
        kgListenExpress() +
        kgPinyinEntry() +
        kgAddSub10() +
        kgGeneral()

    // ---------- 幼儿园：10 以内数量（14 题） ----------
    private fun kgCount10() = listOf(
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-10以内数量",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "数一数：🍎🍎🍎 一共有几个苹果？",
            options = """{"A":"3 个","B":"4 个","C":"5 个"}""",
            answer = "A",
            explanation = "一个一个数，1、2、3，一共 3 个苹果。",
            anchorCode = "kg-count-10",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-10以内数量",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "数一数：🐟🐟🐟🐟🐟 一共有几条小鱼？",
            options = """{"A":"4 条","B":"5 条","C":"6 条"}""",
            answer = "B",
            explanation = "数到 5 就停，一共 5 条小鱼。",
            anchorCode = "kg-count-10",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-10以内数量",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "数一数：⭐⭐⭐⭐⭐⭐ 一共有几颗星星？",
            options = """{"A":"6 颗","B":"7 颗","C":"8 颗"}""",
            answer = "A",
            explanation = "1、2、3、4、5、6，一共 6 颗星星。",
            anchorCode = "kg-count-10",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-10以内数量",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "数字 7 念作什么？",
            options = """{"A":"六","B":"七","C":"八"}""",
            answer = "B",
            explanation = "7 念作七，长得像一把小勺子。",
            anchorCode = "kg-count-10",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-10以内数量",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "数一数：🍌🍌🍌🍌🍌🍌🍌🍌 一共有几根香蕉？",
            options = """{"A":"7 根","B":"8 根","C":"9 根"}""",
            answer = "B",
            explanation = "从 1 数到 8，一共 8 根香蕉。",
            anchorCode = "kg-count-10",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-10以内数量",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "🍎🍎🍎 和 🍐🍐🍐 哪个多？",
            options = """{"A":"一样多","B":"苹果多","C":"梨多"}""",
            answer = "A",
            explanation = "苹果 3 个，梨也是 3 个，一样多。",
            anchorCode = "kg-count-10",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-10以内数量",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "🐰🐰🐰🐰 和 🥕🥕🥕 比一比，哪个少？",
            options = """{"A":"小兔少","B":"萝卜少","C":"一样多"}""",
            answer = "B",
            explanation = "兔子有 4 只，萝卜只有 3 根，萝卜少。",
            anchorCode = "kg-count-10",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-10以内数量",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "5 个小朋友，每人 1 块饼干，要几块？",
            options = """{"A":"4 块","B":"5 块","C":"6 块"}""",
            answer = "B",
            explanation = "一人一块，5 个人就要 5 块饼干。",
            anchorCode = "kg-count-10",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-10以内数量",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "4 和 6 比，哪个数大？",
            options = """{"A":"4","B":"6","C":"一样大"}""",
            answer = "B",
            explanation = "6 排在 4 后面，所以 6 比 4 大。",
            anchorCode = "kg-count-10",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-10以内数量",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "3 个再添上 1 个，是几个？",
            options = """{"A":"2 个","B":"3 个","C":"4 个"}""",
            answer = "C",
            explanation = "3 后面接着数一个是 4，所以是 4 个。",
            anchorCode = "kg-count-10",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-10以内数量",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "盘子里有 4 个苹果，又放 2 个，现在几个？",
            options = """{"A":"5 个","B":"6 个","C":"7 个"}""",
            answer = "B",
            explanation = "4 个再添 2 个，数一数一共 6 个。",
            anchorCode = "kg-count-10",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-10以内数量",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "小兔有 5 根胡萝卜，吃了 2 根，还剩几根？",
            options = """{"A":"2 根","B":"3 根","C":"4 根"}""",
            answer = "B",
            explanation = "5 根拿走 2 根，还剩 3 根。",
            anchorCode = "kg-count-10",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-10以内数量",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "排队时小明排第 3，他前面有几个人？",
            options = """{"A":"1 个","B":"2 个","C":"3 个"}""",
            answer = "B",
            explanation = "第 3 个的前面有 2 个人。",
            anchorCode = "kg-count-10",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-10以内数量",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "篮子里有 6 个球，拿走 4 个，还剩几个？",
            options = """{"A":"1 个","B":"2 个","C":"3 个"}""",
            answer = "B",
            explanation = "6 个拿走 4 个，还剩 2 个。",
            anchorCode = "kg-count-10",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        )
    )

    // ---------- 幼儿园：平面图形与空间（12 题） ----------
    private fun kgShapeSpace() = listOf(
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-图形-平面与空间",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "⭕ 这个图形叫什么？",
            options = """{"A":"圆形","B":"正方形","C":"三角形"}""",
            answer = "A",
            explanation = "圆圆的，没有角，这是圆形。",
            anchorCode = "kg-shape-space",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-图形-平面与空间",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "🔺 这个图形叫什么？",
            options = """{"A":"圆形","B":"三角形","C":"正方形"}""",
            answer = "B",
            explanation = "有三条边、三个角，是三角形。",
            anchorCode = "kg-shape-space",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-图形-平面与空间",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "皮球是什么形状的？",
            options = """{"A":"圆形","B":"三角形","C":"正方形"}""",
            answer = "A",
            explanation = "皮球圆圆的，能滚动，是圆形。",
            anchorCode = "kg-shape-space",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-图形-平面与空间",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "手帕是什么形状的？",
            options = """{"A":"圆形","B":"三角形","C":"正方形"}""",
            answer = "C",
            explanation = "手帕四条边一样长，是正方形。",
            anchorCode = "kg-shape-space",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-图形-平面与空间",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "三角形有几条边？",
            options = """{"A":"2 条","B":"3 条","C":"4 条"}""",
            answer = "B",
            explanation = "三角形有 3 条边、3 个角。",
            anchorCode = "kg-shape-space",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-图形-平面与空间",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "正方形有几条边？",
            options = """{"A":"3 条","B":"4 条","C":"5 条"}""",
            answer = "B",
            explanation = "正方形有 4 条边，还有 4 个角。",
            anchorCode = "kg-shape-space",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-图形-平面与空间",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "车轮做成圆形，因为它能怎样？",
            options = """{"A":"滚动","B":"站立","C":"堆高"}""",
            answer = "A",
            explanation = "圆圆的轮子能滚动，车子才会跑。",
            anchorCode = "kg-shape-space",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-图形-平面与空间",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "下面哪个东西是三角形的？",
            options = """{"A":"三角尺","B":"皮球","C":"手帕"}""",
            answer = "A",
            explanation = "三角尺是三角形，皮球是圆形，手帕是正方形。",
            anchorCode = "kg-shape-space",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-图形-平面与空间",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "小猫躲在桌子下面，小猫在哪儿？",
            options = """{"A":"桌子上面","B":"桌子下面","C":"桌子外面"}""",
            answer = "B",
            explanation = "小猫在桌子下面，低头就能看见。",
            anchorCode = "kg-shape-space",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-图形-平面与空间",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "苹果放进盒子里，苹果在哪儿？",
            options = """{"A":"盒子外面","B":"盒子上面","C":"盒子里面"}""",
            answer = "C",
            explanation = "苹果放进盒子，就在盒子里面。",
            anchorCode = "kg-shape-space",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-图形-平面与空间",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "气球飞到房子上面，气球在哪儿？",
            options = """{"A":"房子下面","B":"房子里面","C":"房子上面"}""",
            answer = "C",
            explanation = "气球往高处飞，停在房子上面。",
            anchorCode = "kg-shape-space",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-图形-平面与空间",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "用 3 根小棒能摆出什么图形？",
            options = """{"A":"圆形","B":"三角形","C":"正方形"}""",
            answer = "B",
            explanation = "3 根小棒正好能围成三角形。",
            anchorCode = "kg-shape-space",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        )
    )

    // ---------- 幼儿园：分类、比较与排序（12 题） ----------
    private fun kgClassifyCompare() = listOf(
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-分类比较排序",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "苹果、香蕉、橘子都是什么？",
            options = """{"A":"水果","B":"蔬菜","C":"玩具"}""",
            answer = "A",
            explanation = "它们都是水果，可以吃，甜甜的。",
            anchorCode = "kg-classify-compare",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-分类比较排序",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "红色、黄色、蓝色都是什么？",
            options = """{"A":"形状","B":"颜色","C":"数字"}""",
            answer = "B",
            explanation = "这些都是颜色，眼睛能看见。",
            anchorCode = "kg-classify-compare",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-分类比较排序",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "小狗、小猫、小兔都是什么？",
            options = """{"A":"动物","B":"水果","C":"车子"}""",
            answer = "A",
            explanation = "它们都是小动物，会跑也会跳。",
            anchorCode = "kg-classify-compare",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-分类比较排序",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "圆形、三角形、正方形都是什么？",
            options = """{"A":"数字","B":"图形","C":"颜色"}""",
            answer = "B",
            explanation = "它们都是图形，各有各的样子。",
            anchorCode = "kg-classify-compare",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-分类比较排序",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "大象和蚂蚁比，哪个大？",
            options = """{"A":"大象大","B":"蚂蚁大","C":"一样大"}""",
            answer = "A",
            explanation = "大象很大，蚂蚁很小，大象大。",
            anchorCode = "kg-classify-compare",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-分类比较排序",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "长颈鹿和小羊比，哪个高？",
            options = """{"A":"长颈鹿高","B":"小羊高","C":"一样高"}""",
            answer = "A",
            explanation = "长颈鹿脖子长，比小羊高很多。",
            anchorCode = "kg-classify-compare",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-分类比较排序",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "跳绳和铅笔比，哪个长？",
            options = """{"A":"铅笔长","B":"跳绳长","C":"一样长"}""",
            answer = "B",
            explanation = "跳绳长长的，铅笔短短的。",
            anchorCode = "kg-classify-compare",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-分类比较排序",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "🍎🍌🍎 里，哪两个是一类？",
            options = """{"A":"两个 🍎","B":"一个 🍎 和一个 🍌","C":"三个都是一类"}""",
            answer = "A",
            explanation = "两个苹果都是苹果，可以放在一起。",
            anchorCode = "kg-classify-compare",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-分类比较排序",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "三只小兔从矮到高排队，谁排最前面？",
            options = """{"A":"最矮的","B":"最高的","C":"不高不矮的"}""",
            answer = "A",
            explanation = "从矮到高排，最矮的小兔站最前面。",
            anchorCode = "kg-classify-compare",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-分类比较排序",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "红球 3 个，蓝球 1 个，哪种球多？",
            options = """{"A":"红球多","B":"蓝球多","C":"一样多"}""",
            answer = "A",
            explanation = "红球有 3 个，蓝球只有 1 个，红球多。",
            anchorCode = "kg-classify-compare",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-分类比较排序",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "🍎🍐🍎🍐 里，苹果有几个？",
            options = """{"A":"1 个","B":"2 个","C":"3 个"}""",
            answer = "B",
            explanation = "数一数，苹果有 2 个，梨也有 2 个。",
            anchorCode = "kg-classify-compare",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-数感-分类比较排序",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "三根绳子从长到短排，最长的排第几？",
            options = """{"A":"第一","B":"第二","C":"第三"}""",
            answer = "A",
            explanation = "从长到短排，最长的绳子排第一。",
            anchorCode = "kg-classify-compare",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        )
    )

    // ---------- 幼儿园：倾听与表达（12 题） ----------
    private fun kgListenExpress() = listOf(
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-语言-倾听与表达",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "「请把书放在桌上」，要拿什么？",
            options = """{"A":"书","B":"杯子","C":"玩具"}""",
            answer = "A",
            explanation = "听清楚，让拿的东西是书。",
            anchorCode = "kg-listen-express",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-语言-倾听与表达",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "别人说话时，我们应该怎么做？",
            options = """{"A":"认真听","B":"大声喊","C":"跑开"}""",
            answer = "A",
            explanation = "别人说话时认真听，才听得懂。",
            anchorCode = "kg-listen-express",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-语言-倾听与表达",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "想说话的时候，应该先怎么做？",
            options = """{"A":"直接大声说","B":"先举手","C":"拉别人衣服"}""",
            answer = "B",
            explanation = "想说话先举手，等老师请你再说。",
            anchorCode = "kg-listen-express",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-语言-倾听与表达",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "别人给你东西，要说什么？",
            options = """{"A":"谢谢","B":"不要","C":"给我"}""",
            answer = "A",
            explanation = "收到别人的东西，要说谢谢。",
            anchorCode = "kg-listen-express",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-语言-倾听与表达",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "妈妈说「先洗手，再吃饭」，先做什么？",
            options = """{"A":"洗手","B":"吃饭","C":"睡觉"}""",
            answer = "A",
            explanation = "「先」的事情先做，所以先去洗手。",
            anchorCode = "kg-listen-express",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-语言-倾听与表达",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "「收好玩具，再坐好」，接下来做什么？",
            options = """{"A":"继续玩","B":"坐好","C":"出去玩"}""",
            answer = "B",
            explanation = "玩具收好了，下一步就是坐好。",
            anchorCode = "kg-listen-express",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-语言-倾听与表达",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "小朋友哭了，你应该怎么说？",
            options = """{"A":"别哭，我陪你","B":"你走开","C":"我不理你"}""",
            answer = "A",
            explanation = "安慰小朋友，说别哭，我陪着你。",
            anchorCode = "kg-listen-express",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-语言-倾听与表达",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "想玩别人的玩具，要怎么说？",
            options = """{"A":"抢过来","B":"我能玩一下吗","C":"藏起来"}""",
            answer = "B",
            explanation = "先问一问，别人同意才能玩。",
            anchorCode = "kg-listen-express",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-语言-倾听与表达",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "「拿起水杯，放到桌上」，第一步做什么？",
            options = """{"A":"拿起水杯","B":"放到桌上","C":"喝水"}""",
            answer = "A",
            explanation = "第一步先拿起水杯，第二步再放桌上。",
            anchorCode = "kg-listen-express",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-语言-倾听与表达",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "妈妈说「穿好鞋，去公园」，先做什么？",
            options = """{"A":"穿鞋","B":"去公园","C":"看电视"}""",
            answer = "A",
            explanation = "出门前先穿好鞋，再去公园。",
            anchorCode = "kg-listen-express",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-语言-倾听与表达",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "小朋友问「你叫什么名字」，怎么回答？",
            options = """{"A":"告诉他我的名字","B":"不理他","C":"哭起来"}""",
            answer = "A",
            explanation = "大方地说出自己的名字，就是好回答。",
            anchorCode = "kg-listen-express",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-语言-倾听与表达",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "想上厕所，应该怎么对老师说？",
            options = """{"A":"老师，我想上厕所","B":"自己跑出去","C":"憋着不说"}""",
            answer = "A",
            explanation = "有事情要告诉老师，老师才会帮你。",
            anchorCode = "kg-listen-express",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        )
    )

    // ---------- 幼儿园：拼音入门 20 题 ----------
    private fun kgPinyinEntry() = listOf(
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "声母 b 的读音像哪个字？",
            options = """{"A":"波","B":"坡","C":"摸"}""",
            answer = "A",
            explanation = "b 读「波」，两片嘴唇轻轻碰一下。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "韵母 a 的读音像哪个字？",
            options = """{"A":"啊","B":"哦","C":"鹅"}""",
            answer = "A",
            explanation = "a 读「啊」，张大嘴巴念 a。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "p 和 b 长得像，p 读什么？",
            options = """{"A":"p","B":"d","C":"q"}""",
            answer = "A",
            explanation = "p 读「坡」，和 b 是好朋友但不一样。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "哪个字母长得像一扇小门？",
            options = """{"A":"m","B":"n","C":"h"}""",
            answer = "A",
            explanation = "m 像两扇小门，读「摸」。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "哪个字母的读音像「得」？",
            options = """{"A":"d","B":"b","C":"g"}""",
            answer = "A",
            explanation = "d 读「得」，像一个小马蹄。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "单韵母一共有哪六个？",
            options = """{"A":"a o e i u ü","B":"b p m f d t","C":"zh ch sh r z c s"}""",
            answer = "A",
            explanation = "单韵母有 a、o、e、i、u、ü 六个。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "i 和 ü 像双胞胎，ü 长什么样？",
            options = """{"A":"ü","B":"i","C":"u"}""",
            answer = "A",
            explanation = "ü 是 u 加两个小点，读「迂」。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "声母 t 的读音像哪个字？",
            options = """{"A":"特","B":"得","C":"呢"}""",
            answer = "A",
            explanation = "t 读「特」，像一把小伞柄。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "mā má mǎ mà，哪个是第一声？",
            options = """{"A":"mā","B":"má","C":"mǎ"}""",
            answer = "A",
            explanation = "第一声平平的，mā 读起来又平又长。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "mā má mǎ mà，哪个是第二声？",
            options = """{"A":"má","B":"mā","C":"mà"}""",
            answer = "A",
            explanation = "第二声往上扬，má 像在问问题。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "mā má mǎ mà，哪个是第三声？",
            options = """{"A":"mǎ","B":"mā","C":"má"}""",
            answer = "A",
            explanation = "第三声先拐弯再下去，mǎ 像拐了个弯。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "mā má mǎ mà，哪个是第四声？",
            options = """{"A":"mà","B":"mǎ","C":"mā"}""",
            answer = "A",
            explanation = "第四声往下掉，mà 像在生气喊话。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "b 和 a 拼在一起，读什么？",
            options = """{"A":"ba","B":"pa","C":"da"}""",
            answer = "A",
            explanation = "b 和 a 碰一碰，就读 ba。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "「妈」的拼音是哪个？",
            options = """{"A":"mā","B":"má","C":"mà"}""",
            answer = "A",
            explanation = "「妈」读第一声 mā，就是叫妈妈。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "「小猫」的第一个音节是？",
            options = """{"A":"xiǎo","B":"māo","C":"gǒu"}""",
            answer = "A",
            explanation = "小猫的「小」读 xiǎo，就是第一个音节。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "「小马」的第一个音节是？",
            options = """{"A":"xiǎo","B":"mǎ","C":"māo"}""",
            answer = "A",
            explanation = "小马的「小」读 xiǎo，是第一个音节。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "「爸爸」的第一个音节是哪个？",
            options = """{"A":"bà","B":"ba","C":"pà"}""",
            answer = "A",
            explanation = "「爸爸」的爸读 bà，第四声。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "「妈妈」的第一个音节是哪个？",
            options = """{"A":"mā","B":"má","C":"ma"}""",
            answer = "A",
            explanation = "「妈妈」的妈读 mā，第一声。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "「兔子」的第一个音节是哪个？",
            options = """{"A":"tù","B":"zi","C":"tú"}""",
            answer = "A",
            explanation = "兔子的「兔」读 tù，第四声。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-拼音-声母韵母与拼读",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "「牙齿」里「齿」的声母是哪个？",
            options = """{"A":"ch","B":"sh","C":"c"}""",
            answer = "A",
            explanation = "「齿」的声母是 ch，读翘舌音。",
            anchorCode = "kg-pinyin-entry",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        )
    )

    // ---------- 幼儿园：10 以内加减 20 题 ----------
    private fun kgAddSub10() = listOf(
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "🍎🍎 + 🍎 = ？",
            options = """{"A":"2 个","B":"3 个","C":"4 个"}""",
            answer = "B",
            explanation = "2 个苹果再添 1 个，就是 3 个。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "🍎🍎 + 🍎🍎 = ？",
            options = """{"A":"4 个","B":"3 个","C":"5 个"}""",
            answer = "A",
            explanation = "2 个加 2 个，数一数是 4 个。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "🍎🍎🍎 - 🍎 = ？",
            options = """{"A":"2 个","B":"3 个","C":"4 个"}""",
            answer = "A",
            explanation = "3 个苹果拿走 1 个，还剩 2 个。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_DIAGRAM,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "3 + 2 = ？",
            options = """{"A":"4","B":"5","C":"6"}""",
            answer = "B",
            explanation = "3 再数 2 个，就是 5。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "5 - 2 = ？",
            options = """{"A":"2","B":"3","C":"4"}""",
            answer = "B",
            explanation = "5 个拿走 2 个，还剩 3 个。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "2 + 3 = ？",
            options = """{"A":"5","B":"4","C":"6"}""",
            answer = "A",
            explanation = "2 再数 3 个，就是 5。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_EXPRESSION,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "5 可以分成 2 和几？",
            options = """{"A":"2","B":"3","C":"4"}""",
            answer = "B",
            explanation = "5 分成 2 和 3，2 加 3 等于 5。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_EXPRESSION,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "7 可以分成 4 和几？",
            options = """{"A":"3","B":"4","C":"5"}""",
            answer = "A",
            explanation = "7 分成 4 和 3，4 加 3 等于 7。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_EXPRESSION,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "9 再添上 1 个，是几？",
            options = """{"A":"10","B":"9","C":"8"}""",
            answer = "A",
            explanation = "9 添上 1 个就是 10，满十啦。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_EXPRESSION,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "6 和几合起来是 10？",
            options = """{"A":"4","B":"5","C":"3"}""",
            answer = "A",
            explanation = "6 加 4 等于 10，4 就是凑十的好朋友。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_EXPRESSION,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "「合起来」表示用加法还是减法？",
            options = """{"A":"加法","B":"减法","C":"都不对"}""",
            answer = "A",
            explanation = "合起来就是越来越多，用加法。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "「剩下」表示用加法还是减法？",
            options = """{"A":"减法","B":"加法","C":"都不对"}""",
            answer = "A",
            explanation = "剩下就是变少了，用减法。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "小明有 4 块饼干，吃了 1 块，还剩几块？",
            options = """{"A":"3 块","B":"4 块","C":"5 块"}""",
            answer = "A",
            explanation = "4 块吃掉 1 块，还剩 3 块。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "盘子里有 3 个桃子，又放上 2 个，现在几个？",
            options = """{"A":"4 个","B":"5 个","C":"6 个"}""",
            answer = "B",
            explanation = "3 个再加 2 个，就是 5 个桃子。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "树上有 6 只小鸟，飞走了 2 只，还剩几只？",
            options = """{"A":"3 只","B":"4 只","C":"5 只"}""",
            answer = "B",
            explanation = "6 只飞走 2 只，还剩 4 只小鸟。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "小华有 2 支铅笔，又买了 3 支，一共有几支？",
            options = """{"A":"4 支","B":"5 支","C":"6 支"}""",
            answer = "B",
            explanation = "2 支加 3 支，一共 5 支铅笔。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "妈妈买来 7 个苹果，吃了 2 个，还剩几个？",
            options = """{"A":"4 个","B":"5 个","C":"6 个"}""",
            answer = "B",
            explanation = "7 个吃掉 2 个，还剩 5 个苹果。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "篮子里有 5 个鸡蛋，又放了 5 个，现在几个？",
            options = """{"A":"9 个","B":"10 个","C":"11 个"}""",
            answer = "B",
            explanation = "5 个加 5 个，就是 10 个鸡蛋。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "小熊有 8 颗糖，分给朋友 3 颗，还剩几颗？",
            options = """{"A":"4 颗","B":"5 颗","C":"6 颗"}""",
            answer = "B",
            explanation = "8 颗分走 3 颗，还剩 5 颗糖。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-算术-10以内加减",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "小兔有 3 根胡萝卜，妈妈又给了 4 根，一共几根？",
            options = """{"A":"6 根","B":"7 根","C":"8 根"}""",
            answer = "B",
            explanation = "3 根加 4 根，一共 7 根胡萝卜。",
            anchorCode = "kg-add-sub-10",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        )
    )

    // ---------- 幼儿园：生活与安全常识（10 题） ----------
    private fun kgGeneral() = listOf(
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-常识-生活与安全",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "红灯亮了，应该怎么做？",
            options = """{"A":"停下来","B":"快跑","C":"慢慢走"}""",
            answer = "A",
            explanation = "红灯停，绿灯行，红灯亮了要停下。",
            anchorCode = "",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-常识-生活与安全",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "吃饭前要先做什么？",
            options = """{"A":"洗手","B":"看电视","C":"跑步"}""",
            answer = "A",
            explanation = "饭前洗手，细菌才不会进肚子。",
            anchorCode = "",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-常识-生活与安全",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "插座上的小洞，能用手摸吗？",
            options = """{"A":"能摸","B":"不能摸","C":"湿手能摸"}""",
            answer = "B",
            explanation = "插座的洞里有电，小手千万不能摸。",
            anchorCode = "",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_MATH,
            knowledgePoint = "kg-常识-生活与安全",
            difficulty = 1, type = Question.TYPE_CHOICE,
            content = "天亮了，我们应该做什么？",
            options = """{"A":"起床","B":"睡觉","C":"关灯"}""",
            answer = "A",
            explanation = "天亮了起床，天黑了才睡觉。",
            anchorCode = "",
            cognitiveLevel = Question.LEVEL_MEMORY,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-常识-生活与安全",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "为什么饭前一定要洗手？",
            options = """{"A":"手上有细菌","B":"手会变白","C":"手会变香"}""",
            answer = "A",
            explanation = "手上有看不见的细菌，洗掉才不生病。",
            anchorCode = "",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-常识-生活与安全",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "为什么不能用手摸插座？",
            options = """{"A":"会触电","B":"会弄脏手","C":"会弄坏插座"}""",
            answer = "A",
            explanation = "电流会伤人，摸插座很危险。",
            anchorCode = "",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-常识-生活与安全",
            difficulty = 2, type = Question.TYPE_CHOICE,
            content = "陌生人给糖，还要带你走，怎么办？",
            options = """{"A":"跟他走","B":"不要，去找爸爸妈妈","C":"先吃糖"}""",
            answer = "B",
            explanation = "不认识的人不能跟走，要去找爸爸妈妈。",
            anchorCode = "",
            cognitiveLevel = Question.LEVEL_UNDERSTANDING,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-常识-生活与安全",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "和妈妈走散了，应该怎么办？",
            options = """{"A":"站在原地等","B":"到处乱跑","C":"跟陌生人走"}""",
            answer = "A",
            explanation = "站在原地等，也可以找穿制服的叔叔阿姨。",
            anchorCode = "",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-常识-生活与安全",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "看到小朋友摔倒了，应该怎么做？",
            options = """{"A":"扶他起来","B":"笑他","C":"走开"}""",
            answer = "A",
            explanation = "小朋友摔倒了，扶他起来才是有爱心。",
            anchorCode = "",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_CONTEXT,
            stage = Stage.KG
        ),
        Question(
            subject = Question.SUBJECT_CHINESE,
            knowledgePoint = "kg-常识-生活与安全",
            difficulty = 3, type = Question.TYPE_CHOICE,
            content = "坐车时能把头伸出窗外吗？",
            options = """{"A":"能","B":"不能","C":"开慢点就能"}""",
            answer = "B",
            explanation = "头伸出窗外很危险，坐车要坐好。",
            anchorCode = "",
            cognitiveLevel = Question.LEVEL_APPLICATION,
            variantType = Question.VARIANT_TEXT,
            stage = Stage.KG
        )
    )
}
