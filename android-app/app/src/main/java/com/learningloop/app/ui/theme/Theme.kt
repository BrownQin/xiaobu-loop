package com.learningloop.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * 智能伴学主题。
 * 面向低龄儿童，主色明亮活泼但不刺眼，鼓励探索。
 */
object LearningColors {
    // 明亮主题
    val PrimaryLight    = Color(0xFFFF8A65)   // 温暖橙
    val SecondaryLight  = Color(0xFF4FC3F7)   // 清亮蓝
    val TertiaryLight   = Color(0xFF81C784)   // 嫩芽绿（答对提示）
    val BackgroundLight = Color(0xFFFFF8E1)   // 奶油米色
    val SurfaceLight    = Color(0xFFFFFBF2)

    // 暗色主题
    val PrimaryDark     = Color(0xFFFFAB91)
    val SecondaryDark   = Color(0xFF4DD0E1)
    val TertiaryDark    = Color(0xFFA5D6A7)
    val BackgroundDark  = Color(0xFF1B1B1B)
    val SurfaceDark     = Color(0xFF2B2B2B)

    val CorrectGreen  = Color(0xFF66BB6A)
    val WrongRed      = Color(0xFFEF5350)
    val AccentStar    = Color(0xFFFFCA28)
}

private val LightScheme = lightColorScheme(
    primary = LearningColors.PrimaryLight,
    secondary = LearningColors.SecondaryLight,
    tertiary = LearningColors.TertiaryLight,
    background = LearningColors.BackgroundLight,
    surface = LearningColors.SurfaceLight
)

private val DarkScheme = darkColorScheme(
    primary = LearningColors.PrimaryDark,
    secondary = LearningColors.SecondaryDark,
    tertiary = LearningColors.TertiaryDark,
    background = LearningColors.BackgroundDark,
    surface = LearningColors.SurfaceDark
)

@Composable
fun LearningLoopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        content = content
    )
}
