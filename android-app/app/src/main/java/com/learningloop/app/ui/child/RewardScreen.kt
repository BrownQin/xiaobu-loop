package com.learningloop.app.ui.child

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learningloop.app.ScreenHeader
import com.learningloop.app.data.entity.RewardItem
import com.learningloop.app.ui.theme.LearningColors

/**
 * 奖励商城：孩子用积分兑换礼物。
 * 奖励条目由家长在“奖励管理”中自定义，这里只展示 enabled 的条目。
 */
@Composable
fun RewardScreen(
    totalPoints: Long,
    rewards: List<RewardItem>,
    onExchange: (RewardItem) -> Unit = {},
    onBack: () -> Unit = {}
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            ScreenHeader(
                title = "奖励商城",
                subtitle = "努力有回报",
                onBack = onBack
            )
            Spacer(Modifier.height(8.dp))

            // 当前积分
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Star, contentDescription = null, tint = LearningColors.AccentStar,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = "我的星星", fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$totalPoints 颗", fontSize = 28.sp, fontWeight = FontWeight.Bold,
                        color = LearningColors.AccentStar
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("🎁 可兑换奖励", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            if (rewards.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "家长还没有配置奖励，\n可以请爸爸妈妈在「奖励管理」里添加哦",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(rewards, key = { it.id }) { reward ->
                        RewardCard(
                            reward = reward,
                            canAfford = totalPoints >= reward.points
                        ) { onExchange(reward) }
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardCard(
    reward: RewardItem,
    canAfford: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = canAfford) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (canAfford) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LearningColors.SecondaryLight.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) { Text(reward.emoji, fontSize = 30.sp) }

            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(reward.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (reward.description.isNotBlank()) {
                    Text(
                        reward.description, fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = onClick,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canAfford) LearningColors.AccentStar
                    else Color.Gray.copy(alpha = 0.4f),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) { Text("${reward.points} 颗", fontWeight = FontWeight.Bold) }
        }
    }
}
