package com.learningloop.app.data

import android.content.Context

/**
 * 朗读设置：语速与音色，存 SharedPreferences，做题页与设置页共用。
 */
class VoiceSettings(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("voice_settings", Context.MODE_PRIVATE)

    var rate: Float
        get() = prefs.getFloat(KEY_RATE, 1.0f)
        set(value) = prefs.edit().putFloat(KEY_RATE, value).apply()

    /** 系统 TTS 音色名；null 表示自动挑中文音色里质量最高的 */
    var voiceName: String?
        get() = prefs.getString(KEY_VOICE, null)
        set(value) = prefs.edit().putString(KEY_VOICE, value).apply()

    fun resetVoice() {
        prefs.edit().remove(KEY_VOICE).apply()
    }

    companion object {
        private const val KEY_RATE = "rate"
        private const val KEY_VOICE = "voice"

        val RATES = listOf(0.6f, 1.0f, 1.5f)

        fun rateLabel(rate: Float): String = when {
            rate < 0.95f -> "慢"
            rate > 1.05f -> "快"
            else -> "正常"
        }
    }
}
