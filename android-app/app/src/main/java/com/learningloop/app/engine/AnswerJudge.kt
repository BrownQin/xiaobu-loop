package com.learningloop.app.engine

import com.learningloop.app.data.entity.Question

/**
 * 判分结果的三种状态。
 *
 * 为什么需要三态？
 *  - 选择题/填空题有唯一答案，非对即错；
 *  - 应用题（开放题）允许多种解法与表达，孩子写出“24个”“一共24个”“24”都应被认可，
 *    但不能因为答案里恰好包含“24”就把完全错误的思路判为正确（旧实现的缺陷）。
 */
enum class Verdict {
    /** 完全正确 */
    CORRECT,
    /** 思路接近 / 表达不完整 —— 值得肯定，但不发积分 */
    PARTIAL,
    /** 未掌握 */
    INCORRECT
}

data class JudgeResult(
    val verdict: Verdict,
    /** 便于统计与错题沉淀，仅 CORRECT 为 true */
    val isCorrect: Boolean,
    /** 给孩子的、不伤自尊的反馈语 */
    val message: String
)

/**
 * 统一判分器。
 *
 * 设计原则：
 *  1. 选择题严格匹配选项字母，杜绝“包含即正确”；
 *  2. 填空题做规范化（去空格/标点、全角转半角、去拼音声调）后**严格相等**；
 *  3. 应用题按“数值是否一致”判断，一致记 PARTIAL 并提示补全单位/过程，
 *     数值不一致才是 INCORRECT，且文案始终指向“看思路”而非“你错了”。
 */
object AnswerJudge {

    fun judge(question: Question, userAnswer: String): JudgeResult {
        val raw = userAnswer.trim()
        if (raw.isEmpty()) {
            return JudgeResult(
                Verdict.INCORRECT,
                false,
                "这次没有作答，没关系，我们一起看看这道题的思路。"
            )
        }
        return when (question.type) {
            Question.TYPE_CHOICE -> judgeChoice(question.answer, raw)
            Question.TYPE_WORD -> judgeOpen(question, raw)
            else -> judgeFill(question.answer, raw)
        }
    }

    // ---------- 选择题 ----------

    private fun judgeChoice(correct: String, user: String): JudgeResult {
        val ok = user.trim().uppercase() == correct.trim().uppercase()
        return if (ok) {
            JudgeResult(Verdict.CORRECT, true, "选对啦，判断得很准确！")
        } else {
            JudgeResult(Verdict.INCORRECT, false, "这个选项还差一点，我们看看题目里的关键线索。")
        }
    }

    // ---------- 填空题 ----------

    private fun judgeFill(correct: String, user: String): JudgeResult {
        val ok = normalize(user) == normalize(correct)
        return if (ok) {
            JudgeResult(Verdict.CORRECT, true, "写对啦，这个知识点你已经掌握了！")
        } else {
            JudgeResult(Verdict.INCORRECT, false, "答案还差一点，注意题目要求的小细节。")
        }
    }

    // ---------- 开放题 / 应用题 ----------

    private fun judgeOpen(question: Question, user: String): JudgeResult {
        val normUser = normalize(user)
        val normAnswer = normalize(question.answer)
        if (normUser.isEmpty()) {
            return JudgeResult(Verdict.INCORRECT, false, "先试着写一写你的想法，写错也没关系。")
        }
        if (normUser == normAnswer) {
            return JudgeResult(Verdict.CORRECT, true, "完全正确，思路和结果都很清楚！")
        }

        val answerNums = extractNumbers(question.answer)
        val userNums = extractNumbers(user)
        if (answerNums.isNotEmpty() && userNums.isNotEmpty()) {
            if (answerNums == userNums) {
                return JudgeResult(
                    Verdict.PARTIAL,
                    false,
                    "计算结果是正确的！把单位或解题过程补全就更完整了。"
                )
            }
            if (answerNums.any { it in userNums }) {
                return JudgeResult(
                    Verdict.PARTIAL,
                    false,
                    "思路方向对了，再检查一下计算过程。"
                )
            }
        }

        if (normAnswer.isNotEmpty() && (normUser.contains(normAnswer) || normAnswer.contains(normUser))) {
            return JudgeResult(
                Verdict.PARTIAL,
                false,
                "方向很接近，试着把答案表达得更完整一些。"
            )
        }

        return JudgeResult(
            Verdict.INCORRECT,
            false,
            "这是一道开放题，答案不止一种，先看看参考思路，再讲给爸爸妈妈听。"
        )
    }

    // ---------- 规范化 ----------

    private val toneMap = mapOf(
        'ā' to 'a', 'á' to 'a', 'ǎ' to 'a', 'à' to 'a',
        'ē' to 'e', 'é' to 'e', 'ě' to 'e', 'è' to 'e',
        'ī' to 'i', 'í' to 'i', 'ǐ' to 'i', 'ì' to 'i',
        'ō' to 'o', 'ó' to 'o', 'ǒ' to 'o', 'ò' to 'o',
        'ū' to 'u', 'ú' to 'u', 'ǔ' to 'u', 'ù' to 'u',
        'ǖ' to 'v', 'ǘ' to 'v', 'ǚ' to 'v', 'ǜ' to 'v', 'ü' to 'v'
    )

    private val punctuation = "，。！？；：、（）【】《》“”‘’·—…,.!?;:()[]{}<>\"'-~～"

    /**
     * 去掉空白与标点、全角转半角、忽略大小写与拼音声调。
     * 用于填空/开放题的宽松但**不产生假阳性**的比较。
     */
    fun normalize(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            val half = when (ch) {
                in '０'..'９' -> ('0' + (ch - '０'))
                in 'Ａ'..'Ｚ' -> ('A' + (ch - 'Ａ'))
                in 'ａ'..'ｚ' -> ('a' + (ch - 'ａ'))
                else -> ch
            }
            if (half.isWhitespace()) continue
            if (punctuation.indexOf(half) >= 0) continue
            sb.append(toneMap[half] ?: half)
        }
        return sb.toString().lowercase()
    }

    /** 提取答案中的全部数值（支持小数），用于开放题比对 */
    fun extractNumbers(input: String): List<String> {
        val out = mutableListOf<String>()
        val cur = StringBuilder()
        for (ch in input) {
            if (ch.isDigit() || ch == '.') {
                cur.append(ch)
            } else if (cur.isNotEmpty()) {
                out += cur.toString().trimEnd('.')
                cur.clear()
            }
        }
        if (cur.isNotEmpty()) out += cur.toString().trimEnd('.')
        return out.filter { it.isNotBlank() }
    }
}
