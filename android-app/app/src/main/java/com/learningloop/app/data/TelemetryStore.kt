package com.learningloop.app.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * 匿名使用统计的本地状态：安装标识、总开关、去重标记。
 *
 * 默认开启，家长可在「管理 → 说明」关闭；关闭后不再产生新事件，
 * 已排队未发送的事件由 ViewModel 清空。
 */
object TelemetryStore {

    private const val PREFS = "telemetry_settings"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_INSTALL_ID = "install_id"
    private const val KEY_ACTIVATED = "activated"
    private const val KEY_LAST_DAY_OPEN = "last_day_open"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    /** 安装级随机 UUID，不是设备标识，重装后重新生成 */
    fun installId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.getString(KEY_INSTALL_ID, null)?.let { return it }
        val id = UUID.randomUUID().toString()
        prefs.edit().putString(KEY_INSTALL_ID, id).apply()
        return id
    }

    /** 首次启动返回 true，并记录已激活 */
    fun markActivatedOnce(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_ACTIVATED, false)) return false
        prefs.edit().putBoolean(KEY_ACTIVATED, true).apply()
        return true
    }

    /** 当天首次打开返回 true */
    fun markDayOpenOnce(context: Context, now: Long = System.currentTimeMillis()): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(now))
        if (prefs.getString(KEY_LAST_DAY_OPEN, null) == today) return false
        prefs.edit().putString(KEY_LAST_DAY_OPEN, today).apply()
        return true
    }
}
