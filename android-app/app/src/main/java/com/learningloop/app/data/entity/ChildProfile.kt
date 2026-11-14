package com.learningloop.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.learningloop.app.data.Stage

/**
 * 孩子画像：姓名、年级、头像、积分、等级。
 *
 * 支持一个家庭维护多个孩子：id 由 Room 自增生成，
 * 当前正在使用的孩子 id 保存在 SharedPreferences 中。
 */
@Entity(tableName = "child_profile")
data class ChildProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String = "小朋友",

    /** 年级：如 "二年级" */
    val grade: String = "二年级",

    /** 头像 emoji 或 URL，MVP 用 emoji */
    val avatar: String = "🧒",

    @ColumnInfo(name = "total_points")
    val totalPoints: Long = 0L,

    @ColumnInfo(name = "current_level")
    val currentLevel: Int = 1,

    /** 学段：KG 幼儿园 / PRIMARY_LOW 小学低年级，决定使用哪套题库 */
    val stage: String = Stage.PRIMARY_LOW
)
