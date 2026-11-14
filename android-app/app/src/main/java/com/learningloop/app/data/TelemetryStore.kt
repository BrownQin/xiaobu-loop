package com.learningloop.app.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * 匿名使用统计的本地状态：安装标识、总开关、去重标记。
 *
 * 默认启用最小化统计，家长可在隐私政策页随时关闭；既有用户的关闭选择继续生效。
 */
object TelemetryStore {

    private const val PREFS = "telemetry_settings"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_INSTALL_ID = "install_id"
    private const val KEY_ACTIVATED = "activated"
    private const val KEY_LAST_DAY_OPEN = "last_day_open"

    /** 默认开启；用户曾在内页关闭时保持关闭 */
    fun enabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLED, true)

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
