package com.learningloop.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 奖励商城条目 —— 由家长自定义维护。
 *
 * 设计说明：
 *  - 奖励是家庭约定，因此做成可增删改的配置，而不是写死在代码里；
 *  - enabled = false 表示下架，孩子端不可见，但历史兑换记录仍保留；
 *  - sortOrder 用于家长控制展示顺序。
 */
@Entity(tableName = "reward_item")
data class RewardItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String = "",

    val emoji: String = "🎁",

    /** 兑换所需积分 */
    val points: Long = 100L,

    val description: String = "",

    val enabled: Boolean = true,

    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0
)
