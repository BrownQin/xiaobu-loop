package com.learningloop.app.ui.tts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.learningloop.app.data.VoiceSettings
import com.learningloop.app.engine.TtsController

/** 页面级 TTS 实例，离开页面自动释放 */
@Composable
fun rememberTtsController(): TtsController {
    val context = LocalContext.current
    val controller = remember { TtsController(context, VoiceSettings(context)) }
    DisposableEffect(Unit) {
        onDispose { controller.shutdown() }
    }
    return controller
}
