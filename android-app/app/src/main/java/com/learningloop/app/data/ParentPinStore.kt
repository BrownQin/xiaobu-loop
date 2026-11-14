package com.learningloop.app.data

import android.content.Context

/**
 * 家长端 4 位数字密码。
 * MVP 存储于 SharedPreferences（仅本机），不做加密与找回，重装应用即重置。
 */
class ParentPinStore(context: Context) {

    private val prefs = context.getSharedPreferences("parent_guard", Context.MODE_PRIVATE)

    var pin: String
        get() = prefs.getString(KEY_PIN, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PIN, value).apply()

    fun hasPin(): Boolean = pin.length == 4

    fun verify(input: String): Boolean {
        val saved = pin
        return saved.length == 4 && saved == input
    }

    companion object {
        private const val KEY_PIN = "parent_pin"
    }
}
