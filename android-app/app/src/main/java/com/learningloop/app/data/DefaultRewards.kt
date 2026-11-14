package com.learningloop.app.data

import com.learningloop.app.data.entity.RewardItem

/**
 * 首次启动写入奖励商城的默认条目。
 * 家长可在“奖励管理”中增删改，这里的配置只作为初始值。
 *
 * 定价统一用「颗星」小单位：5–30 颗，孩子能自己数清并估算攒多久。
 */
object DefaultRewards {
    fun items(): List<RewardItem> = listOf(
        RewardItem(name = "棒棒糖", emoji = "🍬", points = 5, description = "完成一周学习目标", sortOrder = 1),
        RewardItem(name = "睡前多听一个故事", emoji = "📖", points = 8, description = "和爸爸妈妈约定", sortOrder = 2),
        RewardItem(name = "游戏时间 20 分钟", emoji = "🎮", points = 10, description = "周末使用", sortOrder = 3),
        RewardItem(name = "挑选一本新绘本", emoji = "📚", points = 20, description = "自己选", sortOrder = 4),
        RewardItem(name = "零花钱 10 元", emoji = "💰", points = 25, description = "与家长约定兑现", sortOrder = 5),
        RewardItem(name = "心愿清单一次", emoji = "🌟", points = 30, description = "实现一个小愿望", sortOrder = 6)
    )
}
