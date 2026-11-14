package com.learningloop.app.llm

/**
 * 提示词模板：所有需要 LLM 参与的任务都在此处集中管理 prompt 结构。
 *
 * 设计原则：
 *  - 做题全本地，LLM 只在"诊断报告/干预方案/孩子反馈语"这三类低频场景调用；
 *  - 模板要明确人设、目标输出格式、禁止项，保证输出可解析、温和、面向低龄儿童。
 */
object PromptTemplates {

    /**
     * 测评诊断报告 prompt —— 给家长的学习分析。
     */
    fun assessmentReport(
        childName: String,
        subject: String,
        totalQuestions: Int,
        correctCount: Int,
        masteryMap: Map<String, Int>
    ): String = buildString {
        appendLine("你是一名资深的小学低年级（幼小衔接至二年级）语文/数学老师，正在为一个叫【$childName】的孩子生成本周学习诊断。")
        appendLine()
        appendLine("本次测评概况：")
        appendLine("  - 学科：$subject")
        appendLine("  - 题量：$totalQuestions")
        appendLine("  - 答对：$correctCount")
        appendLine("  - 各知识点当前掌握度：")
        masteryMap.forEach { (kp, m) -> appendLine("      $kp → $m%") }
        appendLine()
        appendLine("请输出一段不超过 200 字的中文诊断，包含：")
        appendLine("1) 最值得肯定的 1 点；")
        appendLine("2) 需要重点加强的 1-2 个知识点；")
        appendLine("3) 本周可执行的 2 条具体家庭练习建议（要短、要可操作，比如 '每天做 3 道表内乘法口算' 这种粒度）。")
        appendLine()
        appendLine("要求：语气亲切、给家长看的专业语气，不要用 AI 套话，不要输出 markdown 标题。")
    }

    /**
     * 干预方案生成 prompt —— 当家长查看薄弱点时触发。
     */
    fun interventionPlan(
        childName: String,
        weakPoints: List<String>,
        weeklyTimeMinutes: Int = 30
    ): String = buildString {
        appendLine("你是一名有 10 年经验的小学低年级教育教练。")
        appendLine("孩子【$childName】的薄弱知识点是：")
        weakPoints.forEach { appendLine("  - $it") }
        appendLine()
        appendLine("家长每周能陪练约 $weeklyTimeMinutes 分钟。")
        appendLine("请输出一份「一周干预小方案」，按天拆分，每天一句话，要具体、可落地。总字数控制在 150 字以内。")
    }

    /**
     * 温和反馈语 prompt —— 给孩子的个性化鼓励。
     * 仅在 LLM 可用时使用；离线时由 LLMClient 的 fallback 生成规则反馈。
     */
    fun kidFeedback(
        isCorrect: Boolean,
        streak: Int,
        knowledgePoint: String
    ): String = buildString {
        if (isCorrect) {
            appendLine("你是一个温柔有趣的 AI 学习小伙伴，正在给一个幼儿园大班到小学二年级的小朋友写答对题的鼓励语。")
            appendLine("今天他/她连续答对了 $streak 题，知识点是【$knowledgePoint】。")
            appendLine("请写一句不超过 30 字的中文鼓励话，要像好朋友一样，带点可爱表情。")
        } else {
            appendLine("你是一个温柔有趣的 AI 学习小伙伴，正在给一个幼儿园大班到小学二年级的小朋友写错了题的温和提示。")
            appendLine("知识点是【$knowledgePoint】。")
            appendLine("请写一句不超过 40 字的中文引导语，不要直接给答案，要鼓励他再想想。")
        }
    }
}
