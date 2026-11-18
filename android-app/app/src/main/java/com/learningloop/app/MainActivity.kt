package com.learningloop.app

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.learningloop.app.data.ActiveChildStore
import com.learningloop.app.data.AnchorCatalog
import com.learningloop.app.data.AnchorSeeder
import com.learningloop.app.data.AppDatabase
import com.learningloop.app.data.BackupCodec
import com.learningloop.app.data.DefaultRewards
import com.learningloop.app.data.DiagnosticsExporter
import com.learningloop.app.data.GradeLadder
import com.learningloop.app.data.ParentPinStore
import com.learningloop.app.data.QualitySettings
import com.learningloop.app.data.QuestionCsv
import com.learningloop.app.data.Stage
import com.learningloop.app.data.TelemetryStore
import com.learningloop.app.data.entity.AnchorState
import com.learningloop.app.data.entity.ChildProfile
import com.learningloop.app.data.entity.LearningRecord
import com.learningloop.app.data.entity.Question
import com.learningloop.app.data.entity.RewardItem
import com.learningloop.app.engine.AdaptiveAssessmentEngine
import com.learningloop.app.engine.AnchorBktEngine
import com.learningloop.app.engine.AnchorLoopEngine
import com.learningloop.app.engine.AnswerJudge
import com.learningloop.app.engine.DiagnosticEngine
import com.learningloop.app.engine.GrowthCurveAnalyzer
import com.learningloop.app.engine.ItemQualityAnalyzer
import com.learningloop.app.engine.LoopPlan
import com.learningloop.app.engine.PointIncentiveEngine
import com.learningloop.app.engine.SignalAnalyzer
import com.learningloop.app.engine.TelemetryReporter
import com.learningloop.app.engine.Verdict
import com.learningloop.app.engine.WrongQuestionLoop
import com.learningloop.app.llm.LLMClient
import com.learningloop.app.ui.child.ChildHomeScreen
import com.learningloop.app.ui.child.QuizScreen
import com.learningloop.app.ui.child.QuizSummaryScreen
import com.learningloop.app.ui.child.ResultScreen
import com.learningloop.app.ui.child.RewardScreen
import com.learningloop.app.ui.parent.AboutScreen
import com.learningloop.app.ui.parent.BackupScreen
import com.learningloop.app.ui.parent.ChildManageScreen
import com.learningloop.app.ui.parent.ChildProfileEditScreen
import com.learningloop.app.ui.parent.AdjustPointsScreen
import com.learningloop.app.ui.parent.ManageScreen
import com.learningloop.app.ui.parent.ParentDashboardScreen
import com.learningloop.app.ui.parent.ParentPinScreen
import com.learningloop.app.ui.parent.PrivacyScreen
import com.learningloop.app.ui.parent.QuestionManageScreen
import com.learningloop.app.ui.parent.QuestionQualityScreen
import com.learningloop.app.ui.parent.RewardManageScreen
import com.learningloop.app.ui.parent.VersionHistoryScreen
import com.learningloop.app.ui.theme.LearningColors
import com.learningloop.app.ui.theme.LearningLoopTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 主 Activity + 状态容器。
 *
 * MVP 不引入导航框架，用 sealed class Screen 表达页面；
 * 所有学习数据以 activeChildId 作为 userId 隔离，支持一个家庭多个孩子。
 */
class MainActivity : ComponentActivity() {

    private val vm by viewModels<LearningViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(this)
        val store = ActiveChildStore(this)
        val pinStore = ParentPinStore(this)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (db.questionDao().count() == 0) {
                    db.questionDao().insertAll(SeedData.all())
                }
                if (db.childProfileDao().count() == 0) {
                    val id = db.childProfileDao().insertOrUpdate(ChildProfile(name = "小朋友"))
                    store.activeChildId = id
                }
                if (db.rewardItemDao().count() == 0) {
                    db.rewardItemDao().insertAll(DefaultRewards.items())
                }
            }
            vm.init(db = db, llm = LLMClient(), store = store, context = applicationContext)
        }

        setContent {
            LearningLoopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRoot(vm, pinStore)
                }
            }
        }
    }
}

// ==================== 页面状态机 ====================

sealed class Screen {
    data object ChildHome : Screen()
    data object Quiz : Screen()
    data class Result(
        val questionId: Long,
        val userAnswer: String,
        val verdict: Verdict,
        val earnedPoints: Long,
        val explanation: String,
        val feedback: String
    ) : Screen()
    data class Summary(
        val total: Int,
        val correct: Int,
        val points: Long,
        val ladder: GradeLadder.Status? = null
    ) : Screen()
    data object Reward : Screen()
    data object ParentDashboard : Screen()
    data object Manage : Screen()
    data object AdjustPoints : Screen()
    data class ChildProfileEdit(val editingId: Long = 0L) : Screen()
    data object ChildManage : Screen()
    data object RewardManage : Screen()
    data object ParentPin : Screen()
    data object ParentPinSet : Screen()
    data object QuestionManage : Screen()
    data object Backup : Screen()
    data object VersionHistory : Screen()
    data object About : Screen()
    data object QuestionQuality : Screen()
    data object Privacy : Screen()
}

// ==================== ViewModel ====================

class LearningViewModel : ViewModel() {

    private var dbInstance: AppDatabase? = null
    private var llmInstance: LLMClient? = null
    private var store: ActiveChildStore? = null
    private var appContext: Context? = null

    fun db(): AppDatabase? = dbInstance

    private val _screen = MutableStateFlow<Screen>(Screen.ChildHome)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _children = MutableStateFlow<List<ChildProfile>>(emptyList())
    val children: StateFlow<List<ChildProfile>> = _children.asStateFlow()

    private val _activeId = MutableStateFlow(0L)
    val activeId: StateFlow<Long> = _activeId.asStateFlow()

    private val _profile = MutableStateFlow<ChildProfile?>(null)
    val profile: StateFlow<ChildProfile?> = _profile.asStateFlow()

    private val _rewards = MutableStateFlow<List<RewardItem>>(emptyList())
    val rewards: StateFlow<List<RewardItem>> = _rewards.asStateFlow()

    private val _allRewards = MutableStateFlow<List<RewardItem>>(emptyList())
    val allRewards: StateFlow<List<RewardItem>> = _allRewards.asStateFlow()

    private val _quizQuestions = MutableStateFlow<List<Question>>(emptyList())
    val quizQuestions: StateFlow<List<Question>> = _quizQuestions.asStateFlow()

    private val _quizIndex = MutableStateFlow(0)
    val quizIndex: StateFlow<Int> = _quizIndex.asStateFlow()

    private val _quizLoading = MutableStateFlow(false)
    val quizLoading: StateFlow<Boolean> = _quizLoading.asStateFlow()

    private val _quizError = MutableStateFlow<String?>(null)
    val quizError: StateFlow<String?> = _quizError.asStateFlow()

    /** 题库全量（含停用），供题库管理使用 */
    private val _allQuestions = MutableStateFlow<List<Question>>(emptyList())
    val allQuestions: StateFlow<List<Question>> = _allQuestions.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    /** 今日 Loop 计划：一个锚点、一个层级、最多 3 题 */
    private val _todayPlan = MutableStateFlow<LoopPlan?>(null)
    val todayPlan: StateFlow<LoopPlan?> = _todayPlan.asStateFlow()

    /** 当前孩子的锚点 × 层级状态，供家长矩阵使用 */
    private val _anchorStates = MutableStateFlow<List<AnchorState>>(emptyList())
    val anchorStates: StateFlow<List<AnchorState>> = _anchorStates.asStateFlow()

    /** 当前年级阶梯结论，首页与报告共用 */
    private val _ladder = MutableStateFlow<GradeLadder.Status?>(null)
    val ladder: StateFlow<GradeLadder.Status?> = _ladder.asStateFlow()

    /** 当前孩子是否做过题：没做过就先引导一次摸底测评 */
    private val _hasAnyRecord = MutableStateFlow(false)
    val hasAnyRecord: StateFlow<Boolean> = _hasAnyRecord.asStateFlow()

    /** 匿名使用统计开关，家长可在隐私政策页关闭 */
    private val _telemetryEnabled = MutableStateFlow(true)
    val telemetryEnabled: StateFlow<Boolean> = _telemetryEnabled.asStateFlow()

    private val _wrongCount = MutableStateFlow(0)
    val wrongCount: StateFlow<Int> = _wrongCount.asStateFlow()

    private var quizCorrect = 0
    private var quizEarned = 0L
    private var lastSubject: String? = null
    private var comboStreak = 0

    /** 本次做题的入口：普通练习 / 今日 Loop / 摸底测评 */
    private enum class QuizMode { NORMAL, LOOP, DIAGNOSTIC }
    private var quizMode = QuizMode.NORMAL

    /** 本次会话是否已通过家长密码验证（回首页即失效） */
    private var parentVerified = false
    private var pendingParentScreen: Screen = Screen.ParentDashboard

    /** 从隐私政策页返回时回到哪里（管理页或关于页） */
    private var privacyReturn: Screen = Screen.Manage

    /** 匿名统计上报器与启动事件去重标记 */
    private var telemetry: TelemetryReporter? = null
    private var telemetryStarted = false

    // ---------- 初始化 ----------

    fun init(db: AppDatabase, llm: LLMClient, store: ActiveChildStore, context: Context? = null) {
        this.dbInstance = db
        this.llmInstance = llm
        this.store = store
        this.appContext = context
        _activeId.value = store.activeChildId
        _telemetryEnabled.value = context?.let { TelemetryStore.enabled(it) } ?: true

        viewModelScope.launch {
            db.questionDao().observeAll().collect { _allQuestions.value = it }
        }

        viewModelScope.launch {
            db.childProfileDao().observeAll().collect { list ->
                _children.value = list
                if (list.isNotEmpty() && list.none { it.id == _activeId.value }) {
                    _activeId.value = list.first().id
                    store.activeChildId = list.first().id
                }
                syncProfile()
                refreshWrongCount()
                refreshAnchorData()
                maybeStartTelemetry()
            }
        }
        viewModelScope.launch {
            db.rewardItemDao().observeEnabled().collect { _rewards.value = it }
        }
        viewModelScope.launch {
            db.rewardItemDao().observeAll().collect { _allRewards.value = it }
        }
    }

    private fun syncProfile() {
        val list = _children.value
        _profile.value = list.firstOrNull { it.id == _activeId.value } ?: list.firstOrNull()
    }

    private fun currentUserId(): Long {
        val id = _activeId.value
        if (id > 0 && _children.value.any { it.id == id }) return id
        return _children.value.firstOrNull()?.id ?: 1L
    }

    /** 当前孩子的学段：决定用幼儿园题库还是小学题库 */
    private fun currentStage(): String =
        _children.value.firstOrNull { it.id == currentUserId() }?.stage ?: Stage.PRIMARY_LOW

    private fun refreshWrongCount() {
        val dao = dbInstance?.wrongAnswerDao() ?: return
        val uid = currentUserId()
        viewModelScope.launch { _wrongCount.value = dao.countActive(uid) }
    }

    /** 刷新当前孩子的锚点状态、年级阶梯与今日计划 */
    private fun refreshAnchorData() {
        val d = dbInstance ?: return
        val uid = currentUserId()
        viewModelScope.launch {
            val stage = currentStage()
            withContext(Dispatchers.IO) { AnchorSeeder.run(d, uid, stage, appContext) }
            val states = d.anchorStateDao().listByUser(uid)
            _anchorStates.value = states
            _ladder.value = GradeLadder.of(states, stage)
            _hasAnyRecord.value = d.learningRecordDao().countByUser(uid) > 0
            _todayPlan.value = withContext(Dispatchers.IO) {
                AnchorLoopEngine.plan(d, uid, stage, maxQuestions = 3)
            }
            // 用最新作答证据刷新题目质量结论（家长可关闭自动排除）
            val autoExclude = appContext?.let { QualitySettings.autoExclude(it) } ?: true
            withContext(Dispatchers.IO) {
                ItemQualityAnalyzer.refreshStates(
                    d.questionDao(), d.learningRecordDao(), uid, autoExclude
                )
            }
        }
    }

    // ---------- 匿名统计 ----------

    /** 首次拿到孩子档案后再记启动事件，保证学段字段准确 */
    private fun maybeStartTelemetry() {
        if (telemetryStarted) return
        val ctx = appContext ?: return
        val d = dbInstance ?: return
        if (_children.value.isEmpty()) return
        // 已关闭时不消耗「首次」标记，等重新启用后再补记
        if (!TelemetryStore.enabled(ctx)) return
        telemetryStarted = true
        val reporter = telemetry ?: TelemetryReporter(d, ctx).also { telemetry = it }
        viewModelScope.launch {
            val stage = currentStage()
            if (TelemetryStore.markActivatedOnce(ctx)) reporter.record("activate", stage)
            if (TelemetryStore.markDayOpenOnce(ctx)) reporter.record("day_open", stage)
            reporter.flush()
        }
    }

    fun setTelemetryEnabled(enabled: Boolean) {
        val ctx = appContext ?: return
        TelemetryStore.setEnabled(ctx, enabled)
        _telemetryEnabled.value = enabled
        val d = dbInstance ?: return
        if (!enabled) {
            viewModelScope.launch { withContext(Dispatchers.IO) { d.telemetryEventDao().clear() } }
        } else {
            maybeStartTelemetry()
        }
    }

    // ---------- 导航 ----------

    fun goHome() {
        parentVerified = false
        _screen.value = Screen.ChildHome
        refreshAnchorData()
    }

    fun openReward() { _screen.value = Screen.Reward }

    /** 家长区统一入口：未验证则先进入密码页，验证通过后跳转到目标页 */
    private fun openParentArea(target: Screen) {
        if (parentVerified) {
            _screen.value = target
            return
        }
        pendingParentScreen = target
        _screen.value = Screen.ParentPin
    }

    fun openParent() = openParentArea(Screen.Manage)
    fun openReport() = openParentArea(Screen.ParentDashboard)
    fun openAdjustPoints() = openParentArea(Screen.AdjustPoints)
    fun openChildManage() = openParentArea(Screen.ChildManage)
    fun openRewardManage() = openParentArea(Screen.RewardManage)
    fun openEditChild(id: Long) = openParentArea(Screen.ChildProfileEdit(id))

    fun onParentPinSuccess() {
        parentVerified = true
        _screen.value = pendingParentScreen
    }

    fun openChangeParentPin() {
        _screen.value = Screen.ParentPinSet
    }

    // ---------- 孩子管理 ----------

    fun switchChild(id: Long) {
        _activeId.value = id
        store?.activeChildId = id
        syncProfile()
        refreshWrongCount()
        // 切换孩子后立刻换用该孩子学段的题库与锚点
        refreshAnchorData()
    }

    fun saveChild(
        editingId: Long,
        name: String,
        grade: String,
        avatar: String,
        stage: String = Stage.fromGrade(grade)
    ) {
        val d = dbInstance ?: return
        viewModelScope.launch {
            if (editingId > 0) {
                val cur = d.childProfileDao().getById(editingId) ?: return@launch
                d.childProfileDao().update(
                    cur.copy(name = name, grade = grade, avatar = avatar, stage = stage)
                )
            } else {
                val id = d.childProfileDao().insertOrUpdate(
                    ChildProfile(name = name, grade = grade, avatar = avatar, stage = stage)
                )
                switchChild(id)
            }
            _screen.value = Screen.ChildManage
        }
    }

    fun deleteChild(child: ChildProfile) {
        val d = dbInstance ?: return
        viewModelScope.launch {
            if (d.childProfileDao().count() <= 1) return@launch
            d.childProfileDao().delete(child)
            if (_activeId.value == child.id) {
                d.childProfileDao().listAll().firstOrNull()?.let { switchChild(it.id) }
            }
        }
    }

    // ---------- 测评 ----------

    fun startQuiz(subject: String?, loop: Boolean = false) {
        val d = dbInstance
        if (d == null) {
            _quizQuestions.value = emptyList()
            _quizError.value = "本地题库正在初始化，请稍候再试"
            _quizLoading.value = false
            _screen.value = Screen.Quiz
            return
        }
        if (_children.value.isEmpty()) {
            _quizQuestions.value = emptyList()
            _quizError.value = "还没有建立孩子档案，请先添加一个孩子"
            _screen.value = Screen.Quiz
            return
        }
        lastSubject = subject
        quizMode = if (loop) QuizMode.LOOP else QuizMode.NORMAL
        val uid = currentUserId()
        val stage = currentStage()
        viewModelScope.launch {
            _quizLoading.value = true
            _quizError.value = null
            val qs = withContext(Dispatchers.IO) {
                if (loop) {
                    // 今日 Loop：只练一个锚点、一个层级、最多 3 题
                    AnchorLoopEngine.plan(d, uid, stage, maxQuestions = 3)?.questions
                        ?: AdaptiveAssessmentEngine(
                            d.questionDao(), d.knowledgeMasteryDao(), d.wrongAnswerDao()
                        ).generateAssessment(
                            subject = null, targetCount = 8, userId = uid, stage = stage
                        )
                } else {
                    AdaptiveAssessmentEngine(
                        d.questionDao(), d.knowledgeMasteryDao(), d.wrongAnswerDao()
                    ).generateAssessment(
                        subject = subject, targetCount = 8, userId = uid, stage = stage
                    )
                }
            }.filter { it.type == Question.TYPE_CHOICE }
            _quizLoading.value = false
            if (qs.isEmpty()) {
                _quizQuestions.value = emptyList()
                _quizError.value = "题库暂时不可用，请稍后重试"
                _screen.value = Screen.Quiz
            } else {
                _quizQuestions.value = qs
                _quizIndex.value = 0
                quizCorrect = 0
                quizEarned = 0L
                comboStreak = 0
                _screen.value = Screen.Quiz
            }
        }
    }

    /**
     * 摸底测评：12 题，跨板块取样，摸清当前年级水平。
     * 新孩子首次进入时自动引导，家长也可以随时重测。
     */
    fun startDiagnostic() {
        val d = dbInstance
        if (d == null || _children.value.isEmpty()) {
            startQuiz(subject = null)
            return
        }
        quizMode = QuizMode.DIAGNOSTIC
        lastSubject = null
        val uid = currentUserId()
        val stage = currentStage()
        viewModelScope.launch {
            _quizLoading.value = true
            _quizError.value = null
            val qs = withContext(Dispatchers.IO) {
                DiagnosticEngine.generate(d, stage, DiagnosticEngine.DEFAULT_COUNT)
            }.filter { it.type == Question.TYPE_CHOICE }
            _quizLoading.value = false
            if (qs.isEmpty()) {
                _quizQuestions.value = emptyList()
                _quizError.value = "题库暂时不可用，请稍后再试"
                _screen.value = Screen.Quiz
            } else {
                _quizQuestions.value = qs
                _quizIndex.value = 0
                quizCorrect = 0
                quizEarned = 0L
                comboStreak = 0
                _screen.value = Screen.Quiz
            }
        }
    }

    fun answer(
        question: Question,
        userAnswer: String,
        durationMs: Long,
        answerChanges: Int = 0,
        optionSwitches: Int = 0
    ) {
        val d = dbInstance ?: return
        val uid = currentUserId()
        val judged = AnswerJudge.judge(question, userAnswer)

        viewModelScope.launch {
            val baseline = withContext(Dispatchers.IO) {
                SignalAnalyzer.baselineOf(
                    d.learningRecordDao().recentDurations(uid, question.cognitiveLevel)
                )
            }
            val signal = SignalAnalyzer.classify(durationMs, baseline, judged.isCorrect)

            d.learningRecordDao().insert(
                LearningRecord(
                    userId = uid,
                    questionId = question.id,
                    isCorrect = judged.isCorrect,
                    durationMs = durationMs,
                    anchorCode = question.anchorCode,
                    cognitiveLevel = question.cognitiveLevel,
                    signalQuality = signal,
                    optionSwitches = optionSwitches,
                    answerChanges = answerChanges
                )
            )

            val engine = AdaptiveAssessmentEngine(
                d.questionDao(), d.knowledgeMasteryDao(), d.wrongAnswerDao()
            )
            val prevMastery = d.knowledgeMasteryDao().get(uid, question.knowledgePoint)?.mastery ?: 50
            val delta = when (judged.verdict) {
                Verdict.CORRECT -> 10
                Verdict.PARTIAL -> 3
                Verdict.INCORRECT -> -15
            }
            val km = engine.applyMasteryDelta(question.knowledgePoint, delta, uid)
            applyAnchorAnswer(question, judged.verdict == Verdict.CORRECT, signal, uid)

            val wl = WrongQuestionLoop(
                d.wrongAnswerDao(), d.questionDao(), d.knowledgeMasteryDao(), engine
            )
            when (judged.verdict) {
                Verdict.CORRECT -> {
                    comboStreak++
                    wl.markCorrect(question.id, uid, updateMastery = false)
                }
                Verdict.PARTIAL -> {
                    comboStreak = 0
                    wl.recordWrong(question.id, userAnswer, uid, updateMastery = false)
                }
                Verdict.INCORRECT -> {
                    comboStreak = 0
                    wl.recordWrong(question.id, userAnswer, uid, updateMastery = false)
                }
            }

            val points = if (judged.verdict == Verdict.CORRECT) {
                PointIncentiveEngine(
                    d.childProfileDao(), d.pointLogDao(), d.knowledgeMasteryDao()
                ).rewardCorrect(
                    difficulty = question.difficulty,
                    currentStreak = comboStreak - 1,
                    knowledgePoint = question.knowledgePoint,
                    prevMastery = prevMastery,
                    newMastery = km.mastery,
                    userId = uid
                )
            } else 0L

            if (judged.verdict == Verdict.CORRECT) quizCorrect++
            quizEarned += points

            _screen.value = Screen.Result(
                questionId = question.id,
                userAnswer = userAnswer.ifBlank { "(未作答)" },
                verdict = judged.verdict,
                earnedPoints = points,
                explanation = question.explanation ?: "",
                feedback = judged.message
            )
        }
    }

    fun nextQuestion() {
        val total = _quizQuestions.value.size
        val next = _quizIndex.value + 1
        if (next >= total) settleQuiz() else {
            _quizIndex.value = next
            _screen.value = Screen.Quiz
        }
    }

    private fun settleQuiz() {
        val d = dbInstance ?: run { goHome(); return }
        val uid = currentUserId()
        val wasDiagnostic = quizMode == QuizMode.DIAGNOSTIC
        viewModelScope.launch {
            val bonus = PointIncentiveEngine(
                d.childProfileDao(), d.pointLogDao(), d.knowledgeMasteryDao()
            ).maybeWeeklyProgressBonus(uid)
            if (bonus > 0) quizEarned += bonus
            val stage = currentStage()
            val states = d.anchorStateDao().listByUser(uid)
            _anchorStates.value = states
            val ladder = GradeLadder.of(states, stage)
            _ladder.value = ladder
            _hasAnyRecord.value = d.learningRecordDao().countByUser(uid) > 0
            _screen.value = Screen.Summary(
                total = _quizQuestions.value.size,
                correct = quizCorrect,
                points = quizEarned,
                ladder = if (wasDiagnostic) ladder else null
            )
            quizMode = QuizMode.NORMAL
            telemetry?.let { reporter ->
                val event = if (wasDiagnostic) "diagnostic_done" else "quiz_done"
                reporter.record(event, stage)
                reporter.flush()
            }
        }
    }

    /** 按锚点 × 认知层级更新 BKT 掌握概率与三档状态 */
    private suspend fun applyAnchorAnswer(
        question: Question,
        correct: Boolean,
        signal: String,
        uid: Long
    ) {
        val d = dbInstance ?: return
        val code = question.anchorCode
        if (code.isEmpty()) return
        val anchor = AnchorCatalog.byCode(code) ?: return
        val level = question.cognitiveLevel

        val prev = d.anchorStateDao().find(uid, code, level)
            ?: AnchorState(
                userId = uid,
                anchorCode = code,
                cognitiveLevel = level,
                probability = anchor.bkt.init
            )

        val p = AnchorBktEngine.update(prev.probability, correct, anchor.bkt)
        val attempts = prev.attempts + 1
        val correctCount = prev.correct + if (correct) 1 else 0
        val confidence = AnchorBktEngine.confidenceOf(correctCount, signal, correct)
        val status = AnchorBktEngine.statusOf(p, attempts, confidence)

        d.anchorStateDao().upsert(
            prev.copy(
                probability = p,
                confidence = confidence,
                status = status,
                attempts = attempts,
                correct = correctCount,
                updatedAt = System.currentTimeMillis()
            )
        )
        _anchorStates.value = d.anchorStateDao().listByUser(uid)
    }

    fun exitQuiz() {
        _quizQuestions.value = emptyList()
        _quizIndex.value = 0
        goHome()
    }

    fun retryQuiz() = startQuiz(lastSubject)

    // ---------- 题库管理 ----------

    fun saveQuestion(q: Question) {
        val d = dbInstance ?: return
        viewModelScope.launch {
            if (q.id > 0 && d.questionDao().getById(q.id) != null) d.questionDao().update(q)
            else d.questionDao().insert(q)
            _uiMessage.value = "题目已保存"
        }
    }

    fun toggleQuestion(q: Question, enabled: Boolean) {
        val d = dbInstance ?: return
        viewModelScope.launch {
            d.questionDao().setEnabled(q.id, enabled, System.currentTimeMillis())
        }
    }

    fun deleteQuestion(q: Question) {
        val d = dbInstance ?: return
        viewModelScope.launch {
            d.questionDao().deleteById(q.id)
            _uiMessage.value = "题目已删除"
        }
    }

    fun importQuestionsCsv(text: String) {
        val d = dbInstance ?: return
        viewModelScope.launch {
            val parsed = QuestionCsv.parse(text)
            parsed.questions.forEach { q ->
                if (q.id > 0 && d.questionDao().getById(q.id) != null) {
                    d.questionDao().update(q)
                } else {
                    d.questionDao().insert(q.copy(id = 0L))
                }
            }
            _uiMessage.value = parsed.result.summary()
        }
    }

    suspend fun buildQuestionsCsv(): String {
        val d = dbInstance ?: return ""
        return QuestionCsv.export(d.questionDao().listAll())
    }

    fun openQuestionManage() = openParentArea(Screen.QuestionManage)

    // ---------- 数据备份 ----------

    suspend fun buildBackup(): String {
        val d = dbInstance ?: return ""
        return BackupCodec.export(d)
    }

    /** 诊断日志：只读数据库，用于把问题现象交回开发者分析 */
    suspend fun buildDiagnostics(context: android.content.Context): String {
        val d = dbInstance ?: return ""
        return DiagnosticsExporter.build(context, d)
    }

    fun importBackup(text: String) {
        val d = dbInstance ?: return
        viewModelScope.launch {
            runCatching { BackupCodec.restore(d, text) }
                .onSuccess { _uiMessage.value = it.summary() }
                .onFailure { _uiMessage.value = "恢复失败：${it.message}" }
        }
    }

    fun openBackup() = openParentArea(Screen.Backup)

    fun openVersionHistory() = openParentArea(Screen.VersionHistory)

    fun openQuestionQuality() = openParentArea(Screen.QuestionQuality)

    /** 关于页面向所有用户，不经过家长密码 */
    fun openAbout() {
        _screen.value = Screen.About
    }

    /** 隐私政策对所有人开放，不额外要求家长密码 */
    fun openPrivacy(fromAbout: Boolean = false) {
        privacyReturn = if (fromAbout) Screen.About else Screen.Manage
        _screen.value = Screen.Privacy
    }

    fun closePrivacy() {
        _screen.value = privacyReturn
    }

    fun showMessage(message: String) {
        _uiMessage.value = message
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    // ---------- 奖励 ----------

    /** 家长手工发放 / 扣减积分 */
    fun adjustPoints(delta: Long, reason: String) {
        val d = dbInstance ?: return
        val uid = currentUserId()
        viewModelScope.launch {
            PointIncentiveEngine(
                d.childProfileDao(), d.pointLogDao(), d.knowledgeMasteryDao()
            ).manualAdjust(delta, reason, uid)
        }
    }

    fun exchange(item: RewardItem) {
        val d = dbInstance ?: return
        val uid = currentUserId()
        viewModelScope.launch {
            PointIncentiveEngine(
                d.childProfileDao(), d.pointLogDao(), d.knowledgeMasteryDao()
            ).exchange(item.points, item.name, uid)
        }
    }

    fun saveReward(item: RewardItem) {
        val d = dbInstance ?: return
        viewModelScope.launch {
            if (item.id > 0) d.rewardItemDao().update(item)
            else d.rewardItemDao().insert(item)
        }
    }

    fun toggleReward(item: RewardItem) {
        val d = dbInstance ?: return
        viewModelScope.launch { d.rewardItemDao().update(item.copy(enabled = !item.enabled)) }
    }

    fun deleteReward(item: RewardItem) {
        val d = dbInstance ?: return
        viewModelScope.launch { d.rewardItemDao().delete(item) }
    }
}

// ==================== UI 根 ====================

@Composable
fun AppRoot(vm: LearningViewModel, pinStore: ParentPinStore) {
    val screen by vm.screen.collectAsState()
    val profile by vm.profile.collectAsState()
    val children by vm.children.collectAsState()
    val activeId by vm.activeId.collectAsState()
    val rewards by vm.rewards.collectAsState()
    val allRewards by vm.allRewards.collectAsState()
    val quizQs by vm.quizQuestions.collectAsState()
    val quizIndex by vm.quizIndex.collectAsState()
    val quizLoading by vm.quizLoading.collectAsState()
    val quizError by vm.quizError.collectAsState()
    val wrongCount by vm.wrongCount.collectAsState()
    val allQuestions by vm.allQuestions.collectAsState()
    val uiMessage by vm.uiMessage.collectAsState()
    val todayPlan by vm.todayPlan.collectAsState()
    val anchorStates by vm.anchorStates.collectAsState()
    val ladder by vm.ladder.collectAsState()
    val hasAnyRecord by vm.hasAnyRecord.collectAsState()
    val telemetryEnabled by vm.telemetryEnabled.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val stamp = remember {
        java.text.SimpleDateFormat("yyyyMMdd-HHmm", java.util.Locale.getDefault())
            .format(java.util.Date())
    }

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            vm.clearUiMessage()
        }
    }

    val csvImportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            }.onSuccess { text ->
                if (text.isNullOrBlank()) vm.showMessage("文件为空或无法读取")
                else vm.importQuestionsCsv(text)
            }.onFailure { vm.showMessage("读取失败：${it.message}") }
        }
    }

    val csvExportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val content = vm.buildQuestionsCsv()
                runCatching {
                    context.contentResolver.openOutputStream(uri)
                        ?.bufferedWriter()?.use { it.write(content) }
                }.onSuccess { vm.showMessage("题库已导出，可用飞书表格或 Excel 打开") }
                    .onFailure { vm.showMessage("导出失败：${it.message}") }
            }
        }
    }

    val backupExportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val content = vm.buildBackup()
                runCatching {
                    context.contentResolver.openOutputStream(uri)
                        ?.bufferedWriter()?.use { it.write(content) }
                }.onSuccess { vm.showMessage("备份已导出，请妥善保存") }
                    .onFailure { vm.showMessage("导出失败：${it.message}") }
            }
        }
    }

    val backupImportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            }.onSuccess { text ->
                if (text.isNullOrBlank()) vm.showMessage("文件为空或无法读取")
                else vm.importBackup(text)
            }.onFailure { vm.showMessage("读取失败：${it.message}") }
        }
    }

    val diagnosticsExportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val content = vm.buildDiagnostics(context)
                runCatching {
                    context.contentResolver.openOutputStream(uri)
                        ?.bufferedWriter()?.use { it.write(content) }
                }.onSuccess { vm.showMessage("诊断日志已导出，请发给开发者分析") }
                    .onFailure { vm.showMessage("导出失败：${it.message}") }
            }
        }
    }

    BackHandler(enabled = screen != Screen.ChildHome) {
        when (screen) {
            is Screen.Quiz, is Screen.Result -> vm.exitQuiz()
            is Screen.ChildProfileEdit -> vm.openChildManage()
            is Screen.Manage -> vm.goHome()
            is Screen.ParentDashboard, is Screen.AdjustPoints,
            is Screen.ChildManage, is Screen.RewardManage,
            is Screen.QuestionManage, is Screen.Backup,
            is Screen.QuestionQuality, is Screen.VersionHistory -> vm.openParent()
            is Screen.About -> vm.goHome()
            is Screen.Privacy -> vm.closePrivacy()
            is Screen.Summary -> vm.goHome()
            is Screen.ParentPinSet -> vm.openChildManage()
            else -> vm.goHome()
        }
    }

    when (val s = screen) {
        is Screen.ChildHome -> ChildHomeScreen(
            childName = profile?.name ?: "小朋友",
            avatar = profile?.avatar ?: "🧒",
            totalPoints = profile?.totalPoints ?: 0L,
            level = profile?.currentLevel ?: 1,
            wrongCount = wrongCount,
            stage = profile?.stage ?: Stage.PRIMARY_LOW,
            todayPlan = todayPlan,
            levelText = ladder?.headline,
            showDiagnosticEntry = !hasAnyRecord,
            children = children,
            activeId = activeId,
            onSelectChild = { id -> vm.switchChild(id) },
            onStartQuiz = { subject -> vm.startQuiz(subject) },
            onStartToday = { vm.startQuiz(subject = null, loop = true) },
            onStartDiagnostic = { vm.startDiagnostic() },
            onOpenWrongBook = { vm.startQuiz(subject = null) },
            onOpenReward = { vm.openReward() },
            onOpenAbout = { vm.openAbout() },
            onOpenParentDashboard = { vm.openParent() }
        )

        is Screen.Quiz -> when {
            quizLoading -> LoadingState("正在为你准备今天的题目…")
            quizError != null -> ErrorState(
                message = quizError ?: "加载失败",
                onRetry = { vm.retryQuiz() },
                onBack = { vm.exitQuiz() }
            )
            quizQs.isEmpty() -> LoadingState("正在准备题目…")
            else -> QuizScreen(
                questions = quizQs,
                startIndex = quizIndex,
                total = quizQs.size,
                stage = profile?.stage ?: Stage.PRIMARY_LOW,
                onAnswer = { q, a, d, changes, switches -> vm.answer(q, a, d, changes, switches) },
                onExit = { vm.exitQuiz() }
            )
        }

        is Screen.Result -> {
            val question = quizQs.firstOrNull { it.id == s.questionId }
            if (question == null) {
                ErrorState("题目已失效", onRetry = { vm.retryQuiz() }, onBack = { vm.goHome() })
            } else {
                ResultScreen(
                    question = question,
                    userAnswer = s.userAnswer,
                    verdict = s.verdict,
                    earnedPoints = s.earnedPoints,
                    explanation = s.explanation,
                    message = s.feedback,
                    index = quizIndex,
                    total = quizQs.size,
                    onNext = { vm.nextQuestion() },
                    onBack = { vm.exitQuiz() }
                )
            }
        }

        is Screen.Summary -> QuizSummaryScreen(
            total = s.total,
            correct = s.correct,
            earnedPoints = s.points,
            ladder = s.ladder,
            onRetry = { vm.startQuiz(subject = null) },
            onHome = { vm.goHome() }
        )

        is Screen.Reward -> RewardScreen(
            totalPoints = profile?.totalPoints ?: 0L,
            rewards = rewards,
            onExchange = { vm.exchange(it) },
            onBack = { vm.goHome() }
        )

        is Screen.Manage -> ManageScreen(
            childName = profile?.name ?: "小朋友",
            onOpenReport = { vm.openReport() },
            onOpenChildren = { vm.openChildManage() },
            onOpenPoints = { vm.openAdjustPoints() },
            onOpenRewards = { vm.openRewardManage() },
            onOpenQuestions = { vm.openQuestionManage() },
            onOpenQuality = { vm.openQuestionQuality() },
            onRediagnose = { vm.startDiagnostic() },
            onOpenBackup = { vm.openBackup() },
            onExportLog = { diagnosticsExportLauncher.launch(DiagnosticsExporter.fileName()) },
            onOpenVersions = { vm.openVersionHistory() },
            onOpenAbout = { vm.openAbout() },
            onOpenPrivacy = { vm.openPrivacy(fromAbout = false) },
            onBack = { vm.goHome() }
        )

        is Screen.AdjustPoints -> AdjustPointsScreen(
            childName = profile?.name ?: "小朋友",
            currentPoints = profile?.totalPoints ?: 0L,
            onAdjust = { delta, reason -> vm.adjustPoints(delta, reason) },
            onBack = { vm.openParent() }
        )

        is Screen.ParentDashboard -> {
            val db = vm.db()
            if (db == null) LoadingState("正在加载学习报告…")
            else ParentDashboardComposable(
                childName = profile?.name ?: "小朋友",
                userId = activeId,
                db = db,
                stage = profile?.stage ?: Stage.PRIMARY_LOW,
                ladder = ladder,
                onBack = { vm.openParent() }
            )
        }

        is Screen.VersionHistory -> VersionHistoryScreen(
            onBack = { vm.openParent() }
        )

        is Screen.QuestionQuality -> {
            val db = vm.db()
            if (db == null) LoadingState("正在整理作答证据…")
            else QuestionQualityScreen(
                userId = activeId,
                db = db,
                onBack = { vm.openParent() }
            )
        }

        is Screen.About -> AboutScreen(
            onBack = { vm.goHome() },
            onOpenPrivacy = { vm.openPrivacy(fromAbout = true) }
        )

        is Screen.Privacy -> PrivacyScreen(
            telemetryEnabled = telemetryEnabled,
            onToggleTelemetry = { vm.setTelemetryEnabled(it) },
            onBack = { vm.closePrivacy() }
        )

        is Screen.ChildProfileEdit -> ChildProfileEditScreen(
            initial = children.firstOrNull { it.id == s.editingId } ?: ChildProfile(),
            onSave = { vm.saveChild(s.editingId, it.name, it.grade, it.avatar, it.stage) },
            onBack = { vm.openChildManage() }
        )

        is Screen.ChildManage -> ChildManageScreen(
            children = children,
            activeId = activeId,
            onSwitch = { vm.switchChild(it) },
            onAdd = { vm.openEditChild(0L) },
            onEdit = { vm.openEditChild(it) },
            onDelete = { vm.deleteChild(it) },
            onManageRewards = { vm.openRewardManage() },
            onManagePin = { vm.openChangeParentPin() },
            onManageQuestions = { vm.openQuestionManage() },
            onManageBackup = { vm.openBackup() },
            onBack = { vm.openParent() }
        )

        is Screen.QuestionManage -> QuestionManageScreen(
            questions = allQuestions,
            message = uiMessage,
            onSave = { vm.saveQuestion(it) },
            onToggle = { q, enabled -> vm.toggleQuestion(q, enabled) },
            onDelete = { vm.deleteQuestion(it) },
            onCreate = {},
            onImport = { csvImportLauncher.launch(arrayOf("*/*")) },
            onExport = { csvExportLauncher.launch("题库-$stamp.csv") },
            onBack = { vm.openParent() }
        )

        is Screen.Backup -> BackupScreen(
            stats = "${children.size} 个孩子 · ${allQuestions.size} 道题 · 当前孩子：${profile?.name ?: "未创建"}",
            message = uiMessage,
            onExport = { backupExportLauncher.launch("伴学备份-$stamp.json") },
            onImport = { backupImportLauncher.launch(arrayOf("application/json", "*/*")) },
            onBack = { vm.openParent() }
        )

        is Screen.ParentPin -> ParentPinScreen(
            pinStore = pinStore,
            onSuccess = { vm.onParentPinSuccess() },
            onBack = { vm.goHome() }
        )

        is Screen.ParentPinSet -> ParentPinScreen(
            pinStore = pinStore,
            forceSet = true,
            onSuccess = { vm.openChildManage() },
            onBack = { vm.openChildManage() }
        )

        is Screen.RewardManage -> RewardManageScreen(
            rewards = allRewards,
            onSave = { vm.saveReward(it) },
            onToggle = { vm.toggleReward(it) },
            onDelete = { vm.deleteReward(it) },
            onBack = { vm.openParent() }
        )
    }
}

@Composable
fun LoadingState(text: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = LearningColors.PrimaryLight)
        Spacer(Modifier.height(18.dp))
        Text(text, fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("⚠️", fontSize = 44.sp)
        Spacer(Modifier.height(14.dp))
        Text("出了点小状况", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(message, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f))
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) { Text("返回首页") }
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = LearningColors.PrimaryLight)
            ) { Text("重新加载") }
        }
    }
}

/** 子页面统一顶部返回栏 */
@Composable
fun ScreenHeader(title: String, subtitle: String? = null, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onBack,
            modifier = Modifier.size(width = 64.dp, height = 48.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) { Text("← 返回", fontSize = 13.sp) }

        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (subtitle != null) {
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun ParentDashboardComposable(
    childName: String,
    userId: Long,
    db: AppDatabase,
    stage: String = Stage.PRIMARY_LOW,
    ladder: GradeLadder.Status? = null,
    onBack: () -> Unit
) {
    var overall by remember(userId) { mutableStateOf(0.0) }
    var wrongC by remember(userId) { mutableStateOf(0) }
    var weak by remember(userId) { mutableStateOf<List<GrowthCurveAnalyzer.WeakKpItem>>(emptyList()) }
    var daily by remember(userId) { mutableStateOf<List<GrowthCurveAnalyzer.DailyPoint>>(emptyList()) }
    var weekly by remember(userId) { mutableStateOf<List<GrowthCurveAnalyzer.WeeklySummary>>(emptyList()) }
    var anchors by remember(userId) { mutableStateOf<List<AnchorState>>(emptyList()) }
    var ready by remember(userId) { mutableStateOf(false) }

    LaunchedEffect(userId) {
        ready = false
        val analyzer = GrowthCurveAnalyzer(db.learningRecordDao(), db.knowledgeMasteryDao())
        overall = analyzer.overallMastery(userId)
        wrongC = db.wrongAnswerDao().countActive(userId)
        weak = analyzer.listWeakKnowledgePoints(userId)
        daily = analyzer.dailyAccuracyLastNDays(userId)
        weekly = analyzer.weeklySummary(userId)
        anchors = db.anchorStateDao().listByUser(userId)
        ready = true
    }

    if (!ready) {
        LoadingState("正在生成学习报告…")
        return
    }

    ParentDashboardScreen(
        childName = childName,
        overallMastery = overall,
        wrongCount = wrongC,
        weakKps = weak,
        dailyAccuracy = daily,
        weeklySummary = weekly,
        anchorStates = anchors,
        stage = stage,
        ladder = ladder,
        onBack = onBack
    )
}
