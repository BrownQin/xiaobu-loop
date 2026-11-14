package com.learningloop.app.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * 匿名使用统计的本地状态：隐私同意、安装标识、总开关、去重标记。
 *
 * 合规要求：首次启动必须由用户（监护人）明确选择后才允许采集。
 * - 未同意前 [enabled] 恒为 false，不产生任何事件；
 * - 选择「同意并开启统计」才会真正发送；
 * - 家长仍可在「管理 → 说明」随时关闭。
 */
object TelemetryStore {

    private const val PREFS = "telemetry_settings"
    private const val KEY_CONSENT = "privacy_consent_v1"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_INSTALL_ID = "install_id"
    private const val KEY_ACTIVATED = "activated"
    private const val KEY_LAST_DAY_OPEN = "last_day_open"

    /** 是否已完成首次隐私告知与选择 */
    fun consentGiven(context: Context): Boolean =
        prefs(context).getBoolean(KEY_CONSENT, false)

    /**
     * 记录首次同意结果。
     * @param telemetryAllowed 用户是否同意匿名统计；不同意则仅使用本地功能。
     */
    fun setConsent(context: Context, telemetryAllowed: Boolean) {
        prefs(context).edit()
            .putBoolean(KEY_CONSENT, true)
            .putBoolean(KEY_ENABLED, telemetryAllowed)
            .apply()
    }

    /** 只有「已同意」且「开关开启」才算真正启用 */
    fun enabled(context: Context): Boolean =
        consentGiven(context) && prefs(context).getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    /** 安装级随机 UUID，不是设备标识，重装后重新生成 */
    fun installId(context: Context): String {
        val prefs = prefs(context)
        prefs.getString(KEY_INSTALL_ID, null)?.let { return it }
        val id = UUID.randomUUID().toString()
        prefs.edit().putString(KEY_INSTALL_ID, id).apply()
        return id
    }

    /** 首次启动返回 true，并记录已激活 */
    fun markActivatedOnce(context: Context): Boolean {
        val prefs = prefs(context)
        if (prefs.getBoolean(KEY_ACTIVATED, false)) return false
        prefs.edit().putBoolean(KEY_ACTIVATED, true).apply()
        return true
    }

    /** 当天首次打开返回 true */
    fun markDayOpenOnce(context: Context, now: Long = System.currentTimeMillis()): Boolean {
        val prefs = prefs(context)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(now))
        if (prefs.getString(KEY_LAST_DAY_OPEN, null) == today) return false
        prefs.edit().putString(KEY_LAST_DAY_OPEN, today).apply()
        return true
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
