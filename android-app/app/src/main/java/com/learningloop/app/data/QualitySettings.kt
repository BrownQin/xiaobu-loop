package com.learningloop.app.data

import android.content.Context

/**
 * 题目质量自动排除开关。
 *
 * 默认开启：根据作答证据把「偏难 / 疑似秒答」的题从组卷池里排除；
 * 家长可在「题目质量」页关闭，关闭后只展示结论，不影响出题。
 */
object QualitySettings {

    private const val PREFS = "quality_settings"
    private const val KEY_AUTO_EXCLUDE = "auto_exclude"

    fun autoExclude(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_AUTO_EXCLUDE, true)

    fun setAutoExclude(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_AUTO_EXCLUDE, enabled).apply()
    }
}
