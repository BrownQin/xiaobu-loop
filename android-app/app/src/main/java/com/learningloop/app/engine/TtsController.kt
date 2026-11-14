package com.learningloop.app.engine

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import com.learningloop.app.data.VoiceSettings
import java.util.Locale

/**
 * 系统 TTS 封装。
 *
 * 两个容易踩的坑，这里都做了规避：
 * 1) TextToSpeech 的初始化回调可能早于构造函数返回，此时成员 engine 还是 null，
 *    所以回调里只记状态，真正配置放到朗读前 / 回调后的主线程消息里；
 * 2) 部分引擎「设置音色」会把语速重置回默认，所以顺序必须是先音色、后语速。
 *
 * 每次朗读都重新从 SharedPreferences 读取语速与音色，避免任何缓存导致「调了没用」。
 */
class TtsController(context: Context, private val settings: VoiceSettings) {

    private var engine: TextToSpeech? = null
    private var initialized = false
    private val handler = Handler(Looper.getMainLooper())

    private var pendingText: String? = null
    private var pendingVoice: String? = null
    private var retryCount = 0

    init {
        engine = TextToSpeech(context.applicationContext) { status ->
            initialized = status == TextToSpeech.SUCCESS
            handler.post {
                retryCount = 0
                flushPending()
            }
        }
    }

    fun isReady(): Boolean = initialized

    /** 设备上所有中文音色；部分引擎不暴露音色列表，此时返回空 */
    fun chineseVoices(): List<Voice> {
        val e = engine ?: return emptyList()
        if (!initialized) return emptyList()
        return runCatching { e.voices }.getOrNull()
            ?.filter { it.locale.language == Locale.CHINESE.language }
            ?.sortedBy { it.name }
            ?: emptyList()
    }

    private fun bestVoice(e: TextToSpeech): Voice? =
        runCatching { e.voices }.getOrNull()
            ?.filter { it.locale.language == Locale.CHINESE.language }
            ?.sortedWith(
                compareByDescending<Voice> { it.quality }
                    .thenByDescending { !it.isNetworkConnectionRequired }
            )
            ?.firstOrNull()

    /** 先音色、再语速、最后音调 */
    private fun configure(e: TextToSpeech, voiceOverride: String?) {
        val wanted = voiceOverride ?: settings.voiceName
        val voices = runCatching { e.voices }.getOrNull()
        val target = wanted?.let { name -> voices?.firstOrNull { it.name == name } }
            ?: bestVoice(e)
        if (target != null) {
            runCatching { e.voice = target }
        } else {
            runCatching { e.language = Locale.SIMPLIFIED_CHINESE }
        }
        runCatching { e.setSpeechRate(settings.rate) }
        runCatching { e.setPitch(1.0f) }
    }

    fun speak(text: String) = speakWith(null, text)

    /** voiceOverride 为空时使用家长保存的音色 */
    fun speakWith(voiceOverride: String?, text: String) {
        if (text.isBlank()) return
        if (!initialized || engine == null) {
            pendingText = text
            pendingVoice = voiceOverride
            scheduleRetry()
            return
        }
        val e = engine ?: return
        configure(e, voiceOverride)
        e.speak(text, TextToSpeech.QUEUE_FLUSH, null, "reading")
    }

    private fun scheduleRetry() {
        if (retryCount >= 15) return
        retryCount++
        handler.postDelayed({
            if (initialized) flushPending() else scheduleRetry()
        }, 250)
    }

    private fun flushPending() {
        val text = pendingText ?: return
        if (!initialized) {
            scheduleRetry()
            return
        }
        pendingText = null
        speakWith(pendingVoice, text)
    }

    fun applyVoice() {
        val e = engine ?: return
        if (initialized) configure(e, null)
    }

    fun stop() {
        engine?.stop()
    }

    fun shutdown() {
        handler.removeCallbacksAndMessages(null)
        pendingText = null
        engine?.stop()
        engine?.shutdown()
        engine = null
        initialized = false
    }
}
