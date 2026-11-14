package com.learningloop.app.data

import android.content.Context

/**
 * 记录“当前正在使用的孩子”。
 *
 * 放在 SharedPreferences 而不是数据库，原因：
 *  - 这是界面会话级偏好，不是业务数据；
 *  - 避免每次切换都要写库；
 *  - 应用被杀后仍能恢复到上次使用的孩子。
 */
class ActiveChildStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var activeChildId: Long
        get() = prefs.getLong(KEY_ACTIVE_CHILD, 0L)
        set(value) = prefs.edit().putLong(KEY_ACTIVE_CHILD, value).apply()

    companion object {
        private const val PREFS_NAME = "learningloop_prefs"
        private const val KEY_ACTIVE_CHILD = "active_child_id"
    }
}
